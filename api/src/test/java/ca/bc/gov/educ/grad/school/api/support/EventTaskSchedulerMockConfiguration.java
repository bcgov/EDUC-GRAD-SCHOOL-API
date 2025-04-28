package ca.bc.gov.educ.grad.school.api.support;

import ca.bc.gov.educ.grad.school.api.messaging.NatsConnection;
import ca.bc.gov.educ.grad.school.api.messaging.jetstream.Publisher;
import ca.bc.gov.educ.grad.school.api.messaging.jetstream.Subscriber;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Profile("test-event")
@Configuration
public class EventTaskSchedulerMockConfiguration {

  @Bean
  @Primary
  public NatsConnection natsConnection() {
    return Mockito.mock(NatsConnection.class);
  }

  @Bean
  @Primary
  public Publisher publisher() {
    return Mockito.mock(Publisher.class);
  }

  @Bean
  @Primary
  public Subscriber subscriber() {
    return Mockito.mock(Subscriber.class);
  }

  @Bean
  @Primary
  public WebClient webClient() {
    return Mockito.mock(WebClient.class);
  }
}
