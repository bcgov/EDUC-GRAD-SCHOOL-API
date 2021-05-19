package ca.bc.gov.educ.api.school.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin
@RestController
@EnableResourceServer
@RequestMapping(EducSchoolApiConstants.GRAD_SCHOOL_API_ROOT_MAPPING)
@OpenAPIDefinition(info = @Info(title = "API for School Data.", description = "This Read API is for Reading school data.", version = "1"),
		security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_SCHOOL_DATA"})})
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
    @Operation(summary = "Find All Schools", description = "Get All Schools", tags = { "School" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public List<School> getAllSchools() { 
    	logger.debug("getAllSchools : ");
        return schoolService.getSchoolList();
    }
    
    
    @GetMapping(EducSchoolApiConstants.GET_SCHOOL_BY_CODE_MAPPING)
    @PreAuthorize(PermissionsContants.READ_SCHOOL_DATA)
    @Operation(summary = "Find a School by Mincode", description = "Get a School by Mincode", tags = { "School" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "204", description = "NO CONTENT")})
    public ResponseEntity<School> getSchoolDetails(@PathVariable String minCode) { 
    	logger.debug("getSchoolDetails : ");
    	OAuth2AuthenticationDetails auth =
				(OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
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
    @Operation(summary = "Search for a school", description = "Search for a School", tags = { "School" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    public ResponseEntity<List<School>> getSchoolsByParams(
    		@RequestParam(value = "schoolName", required = false) String schoolName,
    		@RequestParam(value = "mincode", required = false) String mincode) {
    	OAuth2AuthenticationDetails auth =
				(OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
    	String accessToken = auth.getTokenValue();
		return response.GET(schoolService.getSchoolsByParams(schoolName,mincode,accessToken));
    }
}
