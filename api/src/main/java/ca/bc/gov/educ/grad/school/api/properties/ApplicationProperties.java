package ca.bc.gov.educ.grad.school.api.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationProperties {

  public static final String GRAD_SCHOOL_API = "GRAD-SCHOOL-API";
  public static final String STREAM_NAME= "GRAD_SCHOOL_EVENTS";
  public static final String CORRELATION_ID = "correlationID";
  /**
   * The Stan url.
   */
  @Value("${nats.url}")
  String natsUrl;

  @Value("${nats.maxReconnect}")
  Integer natsMaxReconnect;

  @Value("${nats.server}")
  private String server;

  @Value("${nats.connectionName}")
  private String connectionName;

}
