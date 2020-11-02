package ca.bc.gov.educ.api.school.service;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.transformer.SchoolTransformer;
import ca.bc.gov.educ.api.school.repository.SchoolRepository;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;  

    @Autowired
    private SchoolTransformer schoolTransformer;

    private static Logger logger = LoggerFactory.getLogger(SchoolService.class);

     /**
     * Get all Schools in School DTO
     *
     * @return Course 
     * @throws java.lang.Exception
     */
    public List<School> getSchoolList() {
        List<School> schoolList  = new ArrayList<School>();

        try {
        	schoolList = schoolTransformer.transformToDTO(schoolRepository.findAll());            
        } catch (Exception e) {
            logger.debug("Exception:" + e);
        }

        return schoolList;
    }
}
