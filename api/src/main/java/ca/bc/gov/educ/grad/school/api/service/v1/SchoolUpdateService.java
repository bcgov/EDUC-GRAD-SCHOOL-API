package ca.bc.gov.educ.grad.school.api.service.v1;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.constants.v1.SubmissionModeCode;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * The type School update service.
 */
@Service
@Slf4j
public class SchoolUpdateService extends BaseService<School> {

    private final GradSchoolEventRepository gradSchoolEventRepository;
    private final GradSchoolRepository gradSchoolRepository;

    public SchoolUpdateService(GradSchoolEventRepository gradSchoolEventRepository, GradSchoolRepository gradSchoolRepository) {
        super(gradSchoolEventRepository);
        this.gradSchoolEventRepository = gradSchoolEventRepository;
        this.gradSchoolRepository = gradSchoolRepository;
    }

    /**
     * Process event.
     *
     * @param event the event
     */
    @Override
    public void processEvent(final School school, final GradSchoolEventEntity event) {
        log.info("Received and processing event: " + event.getEventId());

        if(!school.getSchoolCategoryCode().equalsIgnoreCase("FED_BAND")) {
            var optGradSchool = gradSchoolRepository.findBySchoolID(UUID.fromString(school.getSchoolId()));
            if (!optGradSchool.isPresent()) {
                GradSchoolEntity newGradSchool = new GradSchoolEntity();
                setTranscriptAndCertificateFlags(school, newGradSchool);
                newGradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
                newGradSchool.setSubmissionModeCode(SubmissionModeCode.REPLACE.toString());
                newGradSchool.setCreateUser(school.getCreateUser());
                newGradSchool.setUpdateDate(LocalDateTime.now());
                newGradSchool.setCreateDate(LocalDateTime.now());
                newGradSchool.setUpdateUser(school.getCreateUser());
                gradSchoolRepository.save(newGradSchool);
            } else {
                var gradSchool = optGradSchool.get();
                setTranscriptAndCertificateFlags(school, gradSchool);
                gradSchool.setUpdateDate(LocalDateTime.now());
                gradSchool.setUpdateUser(school.getCreateUser());
                gradSchoolRepository.save(gradSchool);
            }
            this.updateEvent(event);
        }
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
