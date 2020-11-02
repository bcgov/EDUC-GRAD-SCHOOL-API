package ca.bc.gov.educ.api.school.util;

import java.util.Date;

public class EducSchoolApiConstants {

    //API end-point Mapping constants
    public static final String API_ROOT_MAPPING = "";
    public static final String API_VERSION = "v1";
    public static final String GRAD_SCHOOL_API_ROOT_MAPPING = "/api/" + API_VERSION + "/school";
    public static final String GET_SCHOOL_BY_CODE_MAPPING = "/{minCode}";

    
    //Default Attribute value constants
    public static final String DEFAULT_CREATED_BY = "SchoolAPI";
    public static final Date DEFAULT_CREATED_TIMESTAMP = new Date();
    public static final String DEFAULT_UPDATED_BY = "SchoolAPI";
    public static final Date DEFAULT_UPDATED_TIMESTAMP = new Date();

    //Default Date format constants
    public static final String DEFAULT_DATE_FORMAT = "dd-MMM-yyyy";
    
    public static final String TRAX_DATE_FORMAT = "yyyyMM";
}
