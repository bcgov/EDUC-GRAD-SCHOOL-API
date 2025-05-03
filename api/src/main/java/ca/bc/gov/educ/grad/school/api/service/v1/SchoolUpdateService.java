package ca.bc.gov.educ.grad.school.api.service.v1;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.constants.v1.SubmissionModeCode;
import ca.bc.gov.educ.grad.school.api.exception.GradSchoolAPIRuntimeException;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolHistoryRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.rest.RestUtils;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolGrade;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolGradeSchoolHistory;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;


/**
 * The type School update service.
 */
@Service
@Slf4j
public class SchoolUpdateService extends BaseService<School> {

    private final GradSchoolEventRepository gradSchoolEventRepository;
    private final GradSchoolRepository gradSchoolRepository;
    private final GradSchoolHistoryService gradSchoolHistoryService;
    private final RestUtils restUtils;

    public SchoolUpdateService(GradSchoolEventRepository gradSchoolEventRepository, GradSchoolRepository gradSchoolRepository, GradSchoolHistoryService gradSchoolHistoryService, RestUtils restUtils) {
        super(gradSchoolEventRepository);
        this.gradSchoolEventRepository = gradSchoolEventRepository;
        this.gradSchoolRepository = gradSchoolRepository;
        this.gradSchoolHistoryService = gradSchoolHistoryService;
        this.restUtils = restUtils;
    }

    /**
     * Process event.
     *
     * @param event the event
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void processEvent(final School school, final GradSchoolEventEntity event) {
        log.info("Received and processing event: " + event.getEventId());

        if(!school.getSchoolCategoryCode().equalsIgnoreCase("FED_BAND")) {
            var schoolHistory = restUtils.getSchoolHistoryPaginatedFromInstituteApi(school.getSchoolId());
            if(schoolHistory.isEmpty()){
                throw new GradSchoolAPIRuntimeException("School history cannot be empty - this should not have happened");
            }
            var gradesHaveChanged = haveGradesChanged(school, schoolHistory);

            var optGradSchool = gradSchoolRepository.findBySchoolID(UUID.fromString(school.getSchoolId()));
            if (optGradSchool.isEmpty()) {
                GradSchoolEntity newGradSchool = new GradSchoolEntity();
                setTranscriptAndCertificateFlags(school, newGradSchool);
                newGradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
                newGradSchool.setSubmissionModeCode(SubmissionModeCode.REPLACE.toString());
                newGradSchool.setCreateUser(school.getUpdateUser());
                newGradSchool.setUpdateDate(LocalDateTime.now());
                newGradSchool.setCreateDate(LocalDateTime.now());
                newGradSchool.setUpdateUser(school.getUpdateUser());
                gradSchoolRepository.save(newGradSchool);
                gradSchoolHistoryService.createSchoolHistory(newGradSchool);
            } else if(gradesHaveChanged) {
                var gradSchool = optGradSchool.get();
                setTranscriptAndCertificateFlags(school, gradSchool);
                gradSchool.setUpdateDate(LocalDateTime.now());
                gradSchool.setUpdateUser(school.getUpdateUser());
                gradSchoolRepository.save(gradSchool);
                gradSchoolHistoryService.createSchoolHistory(gradSchool);
            }
            this.updateEvent(event);
        }
    }

    private boolean haveGradesChanged(School school, Page<SchoolHistory> schoolHistory){
        if(schoolHistory != null && !schoolHistory.isEmpty()) {
            var schoolHistoryPrior = schoolHistory.stream().skip(1).findFirst().orElse(null);

            if(schoolHistoryPrior == null){
                return true;
            }
            var currentSchoolGrades = new java.util.ArrayList<>(school.getGrades().stream().map(SchoolGrade::getSchoolGradeCode).toList());
            var priorSchoolGrades = new java.util.ArrayList<>(schoolHistoryPrior.getSchoolGrades().stream().map(SchoolGradeSchoolHistory::getSchoolGradeCode).toList());

            Collections.sort(currentSchoolGrades);
            Collections.sort(priorSchoolGrades);

            return !currentSchoolGrades.equals(priorSchoolGrades);
        }
        return false;
    }

    /**
     * Gets event type.
     *
     * @return the event type
     */
    @Override
    public String getEventType() {
        return EventType.UPDATE_SCHOOL.toString();
    }

}
