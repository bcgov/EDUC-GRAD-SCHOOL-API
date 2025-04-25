package ca.bc.gov.educ.grad.school.api.service.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;

/**
 * The interface Event service.
 *
 * @param <T> the type parameter
 */
public interface EventService<T> {

  /**
   * Process event.
   *
   * @param request the request
   * @param event   the event
   */
  void processEvent(T request, GradSchoolEventEntity event);

  /**
   * Gets event type.
   *
   * @return the event type
   */
  String getEventType();
}
