package ca.bc.gov.educ.api.school.util;

public interface PermissionsContants {
	String _PREFIX = "#oauth2.hasAnyScope('";
	String _SUFFIX = "')";

	String READ_SCHOOL_DATA = _PREFIX + "READ_GRAD_SCHOOL_DATA" + _SUFFIX;
}
