package ca.bc.gov.educ.api.school.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.service.SchoolService;
import ca.bc.gov.educ.api.school.util.EducSchoolApiConstants;

@CrossOrigin
@RestController
@RequestMapping(EducSchoolApiConstants.GRAD_SCHOOL_API_ROOT_MAPPING)
public class SchoolController {

    private static Logger logger = LoggerFactory.getLogger(SchoolController.class);

    @Autowired
    SchoolService schoolService;

    @GetMapping
    public List<School> getAllSchools() { 
    	logger.debug("getAllSchools : ");
        return schoolService.getSchoolList();
    }
}
