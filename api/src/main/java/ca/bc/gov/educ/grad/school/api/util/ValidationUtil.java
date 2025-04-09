package ca.bc.gov.educ.grad.school.api.util;

import org.springframework.validation.FieldError;

public class ValidationUtil {

  private ValidationUtil(){
  }

  public static FieldError createFieldError(String objectName, String fieldName, Object rejectedValue, String message) {
    return new FieldError(objectName, fieldName, rejectedValue, false, null, null, message);
  }
}
