package ca.bc.gov.educ.grad.school.api.service.v1;

import ca.bc.gov.educ.grad.school.api.exception.BusinessError;
import ca.bc.gov.educ.grad.school.api.exception.BusinessException;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.ChoreographedEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus.DB_COMMITTED;

@Service
@Slf4j
public class ChoreographedEventPersistenceService {
  private final GradSchoolEventRepository gradSchoolEventRepository;

  @Autowired
  public ChoreographedEventPersistenceService(GradSchoolEventRepository gradSchoolEventRepository) {
      this.gradSchoolEventRepository = gradSchoolEventRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public GradSchoolEventEntity persistEventToDB(final ChoreographedEvent choreographedEvent) throws BusinessException {
    final var eventOptional = this.gradSchoolEventRepository.findByEventId(choreographedEvent.getEventID());
    if (eventOptional.isPresent()) {
      throw new BusinessException(BusinessError.EVENT_ALREADY_PERSISTED, choreographedEvent.getEventID().toString());
    }
    val event = GradSchoolEventEntity.builder()
      .eventType(choreographedEvent.getEventType().toString())
      .eventId(choreographedEvent.getEventID())
      .eventOutcome(choreographedEvent.getEventOutcome().toString())
      .eventPayload(choreographedEvent.getEventPayload())
      .eventStatus(DB_COMMITTED.toString())
      .createUser(StringUtils.isBlank(choreographedEvent.getCreateUser()) ? "GRAD-SCHOOL-API" : choreographedEvent.getCreateUser())
      .updateUser(StringUtils.isBlank(choreographedEvent.getUpdateUser()) ? "GRAD-SCHOOL-API" : choreographedEvent.getUpdateUser())
      .createDate(LocalDateTime.now())
      .updateDate(LocalDateTime.now())
      .build();
    return this.gradSchoolEventRepository.save(event);
  }
}
