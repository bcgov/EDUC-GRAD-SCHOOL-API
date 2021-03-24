package ca.bc.gov.educ.api.school.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.service.SchoolService;
import ca.bc.gov.educ.api.school.util.EducSchoolApiConstants;
import ca.bc.gov.educ.api.school.util.GradValidation;
import ca.bc.gov.educ.api.school.util.PermissionsContants;
import ca.bc.gov.educ.api.school.util.ResponseHelper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin
@RestController
@EnableResourceServer
@RequestMapping(EducSchoolApiConstants.GRAD_SCHOOL_API_ROOT_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for School Data.", description = "This Read API is for Reading school data.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_SCHOOL_DATA"})})
public class SchoolController {

    private static Logger logger = LoggerFactory.getLogger(SchoolController.class);

    @Autowired
    SchoolService schoolService;
    
    @Autowired
    GradValidation validation;
    
    @Autowired
	ResponseHelper response;

    @GetMapping
    @PreAuthorize(PermissionsContants.READ_SCHOOL_DATA)
    public List<School> getAllSchools(
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "50") Integer pageSize) { 
    	logger.debug("getAllSchools : ");
    	OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails(); 
    	String accessToken = auth.getTokenValue();
        return schoolService.getSchoolList(pageNo,pageSize,accessToken);
    }
    
    
    @GetMapping(EducSchoolApiConstants.GET_SCHOOL_BY_CODE_MAPPING)
    @PreAuthorize(PermissionsContants.READ_SCHOOL_DATA)
    public ResponseEntity<School> getSchoolDetails(@PathVariable String minCode) { 
    	logger.debug("getSchoolDetails : ");
    	OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails(); 
    	String accessToken = auth.getTokenValue();
    	School schoolResponse = schoolService.getSchoolDetails(minCode,accessToken);
    	if(schoolResponse != null) {
    		return response.GET(schoolResponse);
    	}else {
    		return response.NO_CONTENT();
    	}
    }
    
    @GetMapping(EducSchoolApiConstants.GET_SCHOOL_SEARCH_MAPPING)
    @PreAuthorize(PermissionsContants.READ_SCHOOL_DATA)
    public ResponseEntity<List<School>> getSchoolsByParams(
    		@RequestParam(value = "schoolName", required = false) String schoolName,
    		@RequestParam(value = "districtName", required = false) String districtName,
    		@RequestParam(value = "city", required = false) String city,
    		@RequestParam(value = "pageNo", required = false,defaultValue = "0") Integer pageNo, 
            @RequestParam(value = "pageSize", required = false,defaultValue = "20") Integer pageSize) {
		OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails(); 
    	String accessToken = auth.getTokenValue();
    	if((StringUtils.isNotBlank(schoolName) && schoolName.length() < 3) || (StringUtils.isNotBlank(districtName) && districtName.length() < 3) || (StringUtils.isNotBlank(city) && city.length() < 3)) {
    		validation.addError("Error in School Search");
    	}
    	if(validation.hasErrors()) {
    		validation.stopOnErrors();
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
		return response.GET(schoolService.getSchoolsByParams(schoolName,districtName,city,pageNo,pageSize,accessToken));
	}
}
