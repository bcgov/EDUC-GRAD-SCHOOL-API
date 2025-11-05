package ca.bc.gov.educ.grad.school.api.constants.v1;

/**
 * The enum Event outcome.
 */
public enum EventOutcome {

  SCHOOL_UPDATED,

  SCHOOL_CREATED,

  DISTRICT_UPDATED,

  DISTRICT_CREATED,

  AUTHORITY_UPDATED,

  AUTHORITY_CREATED,

  AUTHORITY_FOUND,

  AUTHORITY_NOT_FOUND,

  SCHOOL_NOT_FOUND,

  SCHOOL_MOVED,

  SCHOOL_CONTACT_CREATED,

  SCHOOL_CONTACT_UPDATED,

  SCHOOL_CONTACT_DELETED,

  DISTRICT_CONTACT_CREATED,

  DISTRICT_CONTACT_UPDATED,

  DISTRICT_CONTACT_DELETED,

  AUTHORITY_CONTACT_CREATED,

  AUTHORITY_CONTACT_UPDATED,

  AUTHORITY_CONTACT_DELETED,

  GRAD_SCHOOL_UPDATED;

  public static boolean isValid(String value) {
    if (value == null) {
      return false;
    }
    try {
      EventOutcome.valueOf(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

}
