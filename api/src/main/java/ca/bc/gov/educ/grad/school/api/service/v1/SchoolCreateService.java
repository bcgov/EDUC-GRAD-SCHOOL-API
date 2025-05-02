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
public class SchoolCreateService extends BaseService<School> {

    private final GradSchoolEventRepository gradSchoolEventRepository;
    private final GradSchoolRepository gradSchoolRepository;

    public SchoolCreateService(GradSchoolEventRepository gradSchoolEventRepository, GradSchoolRepository gradSchoolRepository) {
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

        var optGradSchool = gradSchoolRepository.findBySchoolID(UUID.fromString(school.getSchoolId()));
        if(!optGradSchool.isPresent() && !school.getSchoolCategoryCode().equalsIgnoreCase("FED_BAND")) {
            GradSchoolEntity newGradSchool = new GradSchoolEntity();
            setTranscriptAndCertificateFlags(school, newGradSchool);
            newGradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
            newGradSchool.setSubmissionModeCode(SubmissionModeCode.REPLACE.toString());
            newGradSchool.setCreateUser(school.getCreateUser());
            newGradSchool.setUpdateDate(LocalDateTime.now());
            newGradSchool.setCreateDate(LocalDateTime.now());
            newGradSchool.setUpdateUser(school.getUpdateUser());
            gradSchoolRepository.save(newGradSchool);
        }else{
            log.info("Ignoring choreography update event with ID {} :: payload is: {} :: school already exists or is FED_BAND in the service", event.getEventId(), school);
        }
        this.updateEvent(event);
    }


    /**
     * Gets event type.
     *
     * @return the event type
     */
    @Override
    public String getEventType() {
        return EventType.CREATE_SCHOOL.toString();
    }

}
