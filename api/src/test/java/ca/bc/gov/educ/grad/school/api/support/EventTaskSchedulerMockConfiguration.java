package ca.bc.gov.educ.grad.school.api.support;

import ca.bc.gov.educ.grad.school.api.messaging.NatsConnection;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test-event")
@Configuration
public class EventTaskSchedulerMockConfiguration {

  @Bean
  @Primary
  public NatsConnection natsConnection() {
    return Mockito.mock(NatsConnection.class);
  }
}
