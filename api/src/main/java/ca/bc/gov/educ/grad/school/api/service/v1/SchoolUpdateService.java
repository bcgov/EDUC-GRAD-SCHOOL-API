package ca.bc.gov.educ.grad.school.api.service.v1;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventOutcome;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.constants.v1.SubmissionModeCode;
import ca.bc.gov.educ.grad.school.api.exception.GradSchoolAPIRuntimeException;
import ca.bc.gov.educ.grad.school.api.mapper.v1.GradSchoolMapper;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.rest.RestUtils;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolGrade;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolGradeSchoolHistory;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolHistory;
import ca.bc.gov.educ.grad.school.api.util.EventUtil;
import ca.bc.gov.educ.grad.school.api.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventType.UPDATE_GRAD_SCHOOL;


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
    private static final GradSchoolMapper gradSchoolMapper = GradSchoolMapper.mapper;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GradSchoolEventEntity processEvent(final School school, final GradSchoolEventEntity event) {
        log.info("Received and processing event: " + event.getEventId());

        try {
            if(!school.getSchoolCategoryCode().equalsIgnoreCase("FED_BAND")) {
                var schoolHistory = restUtils.getSchoolHistoryPaginatedFromInstituteApi(school.getSchoolId());
                if(schoolHistory.isEmpty()){
                    throw new GradSchoolAPIRuntimeException("School history cannot be empty - this should not have happened");
                }
                var gradesHaveChanged = haveGradesChanged(school, schoolHistory);
                var fundingGroupsChanged = haveFundingGroupsChanged(school, schoolHistory);
                GradSchoolEventEntity gradSchoolEventEntity = null;

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
                    gradSchoolEventEntity = EventUtil.createEvent(
                            school.getUpdateUser(), school.getUpdateUser(),
                            JsonUtil.getJsonStringFromObject(gradSchoolMapper.toStructure(newGradSchool)),
                            UPDATE_GRAD_SCHOOL, EventOutcome.GRAD_SCHOOL_UPDATED);
                } else if(gradesHaveChanged || fundingGroupsChanged) {
                    var gradSchool = optGradSchool.get();
                    setTranscriptAndCertificateFlags(school, gradSchool);
                    gradSchool.setUpdateDate(LocalDateTime.now());
                    gradSchool.setUpdateUser(school.getUpdateUser());
                    gradSchoolRepository.save(gradSchool);
                    gradSchoolHistoryService.createSchoolHistory(gradSchool);
                    gradSchoolEventEntity = EventUtil.createEvent(
                            school.getUpdateUser(), school.getUpdateUser(),
                            JsonUtil.getJsonStringFromObject(gradSchoolMapper.toStructure(gradSchool)),
                            UPDATE_GRAD_SCHOOL, EventOutcome.GRAD_SCHOOL_UPDATED);
                }

                if(gradSchoolEventEntity != null){
                    gradSchoolEventRepository.save(gradSchoolEventEntity);
                }

                this.updateEvent(event);
                return gradSchoolEventEntity;
            }

        } catch (JsonProcessingException e) {
            throw new GradSchoolAPIRuntimeException(e.getMessage());
        }
        return null;
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

    private boolean haveFundingGroupsChanged(School school, Page<SchoolHistory> schoolHistory){
        if(schoolHistory != null && !schoolHistory.isEmpty()) {
            var schoolHistoryPrior = schoolHistory.stream().skip(1).findFirst().orElse(null);

            if(schoolHistoryPrior == null){
                return true;
            }
            var currentSchoolFundingGroups = new java.util.ArrayList<>(school.getSchoolFundingGroups().stream().map(independentSchoolFundingGroup -> independentSchoolFundingGroup.getSchoolFundingGroupCode() + independentSchoolFundingGroup.getSchoolGradeCode()).toList());
            var priorSchoolFundingGroups = new java.util.ArrayList<>(schoolHistoryPrior.getSchoolFundingGroups().stream().map(independentSchoolFundingGroup -> independentSchoolFundingGroup.getSchoolFundingGroupCode() + independentSchoolFundingGroup.getSchoolGradeCode()).toList());

            Collections.sort(currentSchoolFundingGroups);
            Collections.sort(priorSchoolFundingGroups);

            return !currentSchoolFundingGroups.equals(priorSchoolFundingGroups);
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
