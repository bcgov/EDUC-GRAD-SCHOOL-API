package ca.bc.gov.educ.grad.school.api.messaging.jetstream;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventOutcome;
import ca.bc.gov.educ.grad.school.api.constants.v1.EventType;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.properties.ApplicationProperties;
import ca.bc.gov.educ.grad.school.api.struct.v1.ChoreographedEvent;
import ca.bc.gov.educ.grad.school.api.util.JsonUtil;
import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.StreamConfiguration;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static ca.bc.gov.educ.grad.school.api.constants.v1.Topics.GRAD_SCHOOL_EVENTS_TOPIC;

/**
 * The type Publisher.
 */
@Component("publisher")
@Slf4j
public class Publisher {
  private final JetStream jetStream;

  /**
   * Instantiates a new Publisher.
   *
   * @param natsConnection the nats connection
   * @throws IOException           the io exception
   * @throws JetStreamApiException the jet stream api exception
   */
  @Autowired
  public Publisher(final Connection natsConnection) throws IOException, JetStreamApiException {
    this.jetStream = natsConnection.jetStream();
    this.createOrUpdateGradSchoolEventStream(natsConnection);
  }

  /**
   * here only name and replicas and max messages are set, rest all are library default.
   *
   * @param natsConnection the nats connection
   * @throws IOException           the io exception
   * @throws JetStreamApiException the jet stream api exception
   */
  private void createOrUpdateGradSchoolEventStream(final Connection natsConnection) throws IOException, JetStreamApiException {
    val streamConfiguration = StreamConfiguration.builder().name(ApplicationProperties.STREAM_NAME).replicas(1).maxMessages(10000).addSubjects(GRAD_SCHOOL_EVENTS_TOPIC.toString()).build();
    try {
      natsConnection.jetStreamManagement().updateStream(streamConfiguration);
    } catch (final JetStreamApiException exception) {
      if (exception.getErrorCode() == 404) { // the stream does not exist , lets create it.
        natsConnection.jetStreamManagement().addStream(streamConfiguration);
      } else {
        log.info("exception", exception);
      }
    }

  }


  /**
   * Dispatch choreography event.
   *
   * @param event the event
   */
  public void dispatchChoreographyEvent(final GradSchoolEventEntity event) {
    if (event != null && event.getEventId() != null) {
      val choreographedEvent = new ChoreographedEvent();
      choreographedEvent.setEventType(EventType.valueOf(event.getEventType()));
      choreographedEvent.setEventOutcome(EventOutcome.valueOf(event.getEventOutcome()));
      choreographedEvent.setEventPayload(event.getEventPayload());
      choreographedEvent.setEventID(event.getEventId());
      choreographedEvent.setCreateUser(event.getCreateUser());
      choreographedEvent.setUpdateUser(event.getUpdateUser());
      try {
        log.info("Broadcasting event :: {}", choreographedEvent);
        val pub = this.jetStream.publishAsync(GRAD_SCHOOL_EVENTS_TOPIC.toString(), JsonUtil.getJsonBytesFromObject(choreographedEvent));
        pub.thenAcceptAsync(result -> log.info("Event ID :: {} Published to JetStream :: {}", event.getEventId(), result.getSeqno()));
      } catch (IOException e) {
        log.error("exception while broadcasting message to JetStream", e);
      }
    }
  }
}
