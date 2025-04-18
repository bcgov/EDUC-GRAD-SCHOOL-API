package ca.bc.gov.educ.grad.school.api.mapper;

import org.apache.commons.lang3.StringUtils;

/**
 * The type String mapper.
 */
public class StringMapper {

  /**
   * Map string.
   *
   * @param value the value
   * @return the string
   */
  public String map(String value) {
    if (StringUtils.isNotEmpty(value)) {
      return value.trim();
    }
    return value;
  }
}
