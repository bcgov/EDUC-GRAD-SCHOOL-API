package ca.bc.gov.educ.grad.school.api.util;

import ca.bc.gov.educ.grad.school.api.properties.ApplicationProperties;
import ca.bc.gov.educ.grad.school.api.struct.v1.BaseRequest;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * The type Request util.
 */
public class RequestUtil {
  private RequestUtil() {
  }

  /**
   * set audit data to the object.
   *
   * @param baseRequest The object which will be persisted.
   */
  public static void setAuditColumnsForCreate(@NotNull BaseRequest baseRequest) {
    if (StringUtils.isBlank(baseRequest.getCreateUser())) {
      baseRequest.setCreateUser(ApplicationProperties.GRAD_SCHOOL_API);
    }
    baseRequest.setCreateDate(LocalDateTime.now().toString());
    setAuditColumnsForUpdate(baseRequest);
  }

  /**
   * set audit data to the object.
   *
   * @param baseRequest The object which will be persisted.
   */
  public static void setAuditColumnsForUpdate(@NotNull BaseRequest baseRequest) {
    if (StringUtils.isBlank(baseRequest.getUpdateUser())) {
      baseRequest.setUpdateUser(ApplicationProperties.GRAD_SCHOOL_API);
    }
    baseRequest.setUpdateDate(LocalDateTime.now().toString());
  }

}
