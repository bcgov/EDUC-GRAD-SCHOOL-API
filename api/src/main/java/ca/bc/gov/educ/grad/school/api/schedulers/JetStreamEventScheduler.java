package ca.bc.gov.educ.grad.school.api.schedulers;

import ca.bc.gov.educ.grad.school.api.choreographer.ChoreographEventHandler;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.messaging.jetstream.Publisher;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus.DB_COMMITTED;


/**
 * This class is responsible to check the PEN_MATCH_EVENT table periodically and publish messages to Jet Stream, if some them are not yet published
 * this is a very edge case scenario which will occur.
 */
@Component
@Slf4j
public class JetStreamEventScheduler {

  /**
   * The Event repository.
   */
  private final GradSchoolEventRepository eventRepository;
  private final Publisher publisher;
  private final ChoreographEventHandler choreographer;

  /**
   * Instantiates a new Stan event scheduler.
   *
   * @param eventRepository the event repository
   * @param choreographer   the choreographer
   */
  public JetStreamEventScheduler(final GradSchoolEventRepository eventRepository, Publisher publisher, final ChoreographEventHandler choreographer) {
    this.eventRepository = eventRepository;
    this.publisher = publisher;
    this.choreographer = choreographer;
  }

  /**
   * Find and process events.
   */
  @Scheduled(cron = "${cron.scheduled.process.events.stan}") // every 5 minutes
  @SchedulerLock(name = "PROCESS_CHOREOGRAPHED_EVENTS_FROM_JET_STREAM", lockAtLeastFor = "${cron.scheduled.process.events.stan.lockAtLeastFor}", lockAtMostFor = "${cron.scheduled.process.events.stan.lockAtMostFor}")
  public void findAndProcessEvents() {
    LockAssert.assertLocked();
    log.info("Fired jet stream choreography scheduler");
    var gradSchoolEventTypes = Arrays.asList(EventType.UPDATE_GRAD_SCHOOL.toString());
    var results = eventRepository.findByEventStatusAndEventTypeIn(DB_COMMITTED.toString(), gradSchoolEventTypes);
    if (!results.isEmpty()) {
      results.forEach(el -> {
        if (el.getUpdateDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
          try {
            publisher.dispatchChoreographyEvent(el);
          } catch (final Exception ex) {
            log.error("Exception while trying to publish message", ex);
          }
        }
      });
    }

    log.info("Checking for events that have not yet been processed");
    final var resultsForIncoming = this.eventRepository.findAllByEventStatusAndCreateDateBeforeAndEventTypeNotInOrderByCreateDate(DB_COMMITTED.toString(), LocalDateTime.now().minusMinutes(1), 500, gradSchoolEventTypes);
    log.info("Number of events not yet processed: " + resultsForIncoming.size());
    if (!results.isEmpty()) {
      log.info("Found {} grad school choreographed events which needs to be processed.", resultsForIncoming.size());
      resultsForIncoming.forEach(this.choreographer::handleEvent);
    }

  }

}
