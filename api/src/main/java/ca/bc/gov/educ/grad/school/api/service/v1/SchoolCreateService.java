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
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import ca.bc.gov.educ.grad.school.api.util.EventUtil;
import ca.bc.gov.educ.grad.school.api.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventType.UPDATE_GRAD_SCHOOL;


/**
 * The type School update service.
 */
@Service
@Slf4j
public class SchoolCreateService extends BaseService<School> {

    private final GradSchoolEventRepository gradSchoolEventRepository;
    private final GradSchoolRepository gradSchoolRepository;
    private final GradSchoolHistoryService gradSchoolHistoryService;
    private static final GradSchoolMapper gradSchoolMapper = GradSchoolMapper.mapper;

    public SchoolCreateService(GradSchoolEventRepository gradSchoolEventRepository, GradSchoolRepository gradSchoolRepository, GradSchoolHistoryService gradSchoolHistoryService) {
        super(gradSchoolEventRepository);
        this.gradSchoolEventRepository = gradSchoolEventRepository;
        this.gradSchoolRepository = gradSchoolRepository;
        this.gradSchoolHistoryService = gradSchoolHistoryService;
    }

    /**
     * Process event.
     *
     * @param event the event
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GradSchoolEventEntity processEvent(final School school, final GradSchoolEventEntity event) {
        GradSchoolEventEntity gradSchoolEventEntity = null;
        try {
            log.info("Received and processing event: " + event.getEventId());

            var optGradSchool = gradSchoolRepository.findBySchoolID(UUID.fromString(school.getSchoolId()));
            if(optGradSchool.isEmpty()) {
                GradSchoolEntity newGradSchool = new GradSchoolEntity();
                if(school.getSchoolCategoryCode().equalsIgnoreCase("FED_BAND")){
                    newGradSchool.setCanIssueCertificates("N");
                    newGradSchool.setCanIssueTranscripts("N");
                }else{
                    setTranscriptAndCertificateFlags(school, newGradSchool);
                }
                newGradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
                newGradSchool.setSubmissionModeCode(SubmissionModeCode.REPLACE.toString());
                newGradSchool.setCreateUser(school.getCreateUser());
                newGradSchool.setUpdateDate(LocalDateTime.now());
                newGradSchool.setCreateDate(LocalDateTime.now());
                newGradSchool.setUpdateUser(school.getUpdateUser());
                gradSchoolRepository.save(newGradSchool);
                gradSchoolHistoryService.createSchoolHistory(newGradSchool);
                gradSchoolEventEntity = EventUtil.createEvent(
                        school.getUpdateUser(), school.getUpdateUser(),
                        JsonUtil.getJsonStringFromObject(gradSchoolMapper.toStructure(newGradSchool)),
                        UPDATE_GRAD_SCHOOL, EventOutcome.GRAD_SCHOOL_UPDATED);
            }else{
                log.info("Ignoring choreography create event with ID {} :: payload is: {} :: school already exists or is FED_BAND in the service", event.getEventId(), school);
            }
            this.updateEvent(event);
        } catch (JsonProcessingException e) {
            throw new GradSchoolAPIRuntimeException(e.getMessage());
        }
        return gradSchoolEventEntity;
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
