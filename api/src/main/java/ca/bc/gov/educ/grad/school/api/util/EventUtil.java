package ca.bc.gov.educ.grad.school.api.util;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventOutcome;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;

import java.time.LocalDateTime;


public class EventUtil {
  private EventUtil() {
  }

  public static GradSchoolEventEntity createEvent(String createUser, String updateUser, String jsonString, EventType eventType, EventOutcome eventOutcome) {
    return GradSchoolEventEntity.builder()
      .createDate(LocalDateTime.now())
      .updateDate(LocalDateTime.now())
      .createUser(createUser)
      .updateUser(updateUser)
      .eventPayload(jsonString)
      .eventType(eventType.toString())
      .eventStatus(EventStatus.DB_COMMITTED.toString())
      .eventOutcome(eventOutcome.toString())
      .build();
  }
}
