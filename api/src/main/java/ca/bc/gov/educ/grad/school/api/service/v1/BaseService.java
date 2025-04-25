package ca.bc.gov.educ.grad.school.api.service.v1;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


/**
 * The type Base service.
 *
 * @param <T> the type parameter
 */
@Slf4j
public abstract class BaseService<T> implements EventService<T> {
    private final GradSchoolEventRepository gradSchoolEventRepository;

    protected BaseService(GradSchoolEventRepository gradSchoolEventRepository) {
        this.gradSchoolEventRepository = gradSchoolEventRepository;
    }

    protected void updateEvent(final GradSchoolEventEntity event) {
        this.gradSchoolEventRepository.findByEventId(event.getEventId()).ifPresent(existingEvent -> {
            existingEvent.setEventStatus(EventStatus.PROCESSED.toString());
            existingEvent.setUpdateDate(LocalDateTime.now());
            this.gradSchoolEventRepository.save(existingEvent);
        });
    }

}
