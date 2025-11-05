package ca.bc.gov.educ.grad.school.api.util;

import ca.bc.gov.educ.grad.school.api.constants.v1.EventOutcome;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.exception.IgnoreEventException;
import ca.bc.gov.educ.grad.school.api.struct.v1.ChoreographedEvent;
import ca.bc.gov.educ.grad.school.api.struct.v1.ChoreographedEventValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;


public final class EventUtils {
  private EventUtils() {
  }

  public static ChoreographedEvent getChoreographedEventIfValid(String eventString) throws JsonProcessingException, IgnoreEventException {
    final ChoreographedEventValidation event = JsonUtil.getJsonObjectFromString(ChoreographedEventValidation.class, eventString);
    if(StringUtils.isNotBlank(event.getEventOutcome()) && !EventOutcome.isValid(event.getEventOutcome())) {
      throw new IgnoreEventException("Invalid event outcome", event.getEventType(), event.getEventOutcome());
    }else if(StringUtils.isNotBlank(event.getEventType()) && !EventType.isValid(event.getEventType())) {
      throw new IgnoreEventException("Invalid event type", event.getEventType(), event.getEventOutcome());
    }
    return JsonUtil.getJsonObjectFromString(ChoreographedEvent.class, eventString);
  }
}
