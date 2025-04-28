package ca.bc.gov.educ.grad.school.api.choreographer;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.service.v1.EventService;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import ca.bc.gov.educ.grad.school.api.util.JsonUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * The type Choreograph event handler.
 */
@Component
@Slf4j
public class ChoreographEventHandler {
    private final Executor singleTaskExecutor = new EnhancedQueueExecutor.Builder()
            .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("task-executor-%d").build())
            .setCorePoolSize(2).setMaximumPoolSize(2).build();
    private final Map<String, EventService<?>> eventServiceMap;
    private final GradSchoolEventRepository gradSchoolEventRepository;

    public ChoreographEventHandler(final List<EventService<?>> eventServices, GradSchoolEventRepository gradSchoolEventRepository) {
        this.gradSchoolEventRepository = gradSchoolEventRepository;
        this.eventServiceMap = new HashMap<>();
        eventServices.forEach(eventService -> this.eventServiceMap.put(eventService.getEventType(), eventService));
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    public void handleEvent(@NonNull final GradSchoolEventEntity event) {
        //only one thread will process all the request. since RDB won't handle concurrent requests.
        this.singleTaskExecutor.execute(() -> {
            val eventFromDBOptional = this.gradSchoolEventRepository.findById(event.getEventId());
            if (eventFromDBOptional.isPresent()) {
                val eventFromDB = eventFromDBOptional.get();
                if (eventFromDB.getEventStatus().equals(EventStatus.DB_COMMITTED.toString())) {
                    log.info("Processing event with event ID :: {}", event.getEventId());
                    try {
                        switch (event.getEventType()) {
                            case "CREATE_SCHOOL":
                                val studentCreate = JsonUtil.getJsonObjectFromString(School.class, event.getEventPayload());
                                final EventService<School> studentCreateEventService = (EventService<School>) this.eventServiceMap.get(EventType.CREATE_SCHOOL.toString());
                                studentCreateEventService.processEvent(studentCreate, event);
                                break;
                            case "UPDATE_SCHOOL":
                                val studentUpdate = JsonUtil.getJsonObjectFromString(School.class, event.getEventPayload());
                                final EventService<School> studentUpdateEventService = (EventService<School>) this.eventServiceMap.get(EventType.UPDATE_SCHOOL.toString());
                                studentUpdateEventService.processEvent(studentUpdate, event);
                                break;
                            default:
                                log.warn("Silently ignoring event: {}", event);
                                this.gradSchoolEventRepository.findByEventId(event.getEventId()).ifPresent(existingEvent -> {
                                    existingEvent.setEventStatus(EventStatus.PROCESSED.toString());
                                    existingEvent.setUpdateDate(LocalDateTime.now());
                                    this.gradSchoolEventRepository.save(existingEvent);
                                });
                                break;
                        }
                        log.info("Event was processed, ID :: {}", event.getEventId());
                    } catch (final Exception exception) {
                        log.error("Exception while processing event :: {}", event, exception);
                    }
                }
            }

        });


    }
}
