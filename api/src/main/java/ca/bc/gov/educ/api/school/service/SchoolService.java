package ca.bc.gov.educ.api.school.service;


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.school.model.dto.District;
import ca.bc.gov.educ.api.school.model.dto.GradCountry;
import ca.bc.gov.educ.api.school.model.dto.GradProvince;
import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;
import ca.bc.gov.educ.api.school.model.transformer.DistrictTransformer;
import ca.bc.gov.educ.api.school.model.transformer.SchoolTransformer;
import ca.bc.gov.educ.api.school.repository.DistrictRepository;
import ca.bc.gov.educ.api.school.repository.SchoolCriteriaQueryRepository;
import ca.bc.gov.educ.api.school.repository.SchoolRepository;
import ca.bc.gov.educ.api.school.repository.criteria.CriteriaHelper;
import ca.bc.gov.educ.api.school.repository.criteria.GradCriteria.OperationEnum;
import ca.bc.gov.educ.api.school.util.EducSchoolApiConstants;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;  

    @Autowired
    private SchoolTransformer schoolTransformer;
    
    @Autowired
    private DistrictRepository districtRepository;  

    @Autowired
    private DistrictTransformer districtTransformer;
    
    @Autowired
    private SchoolCriteriaQueryRepository schoolCriteriaQueryRepository;
    
    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    WebClient webClient;
    
	@Autowired
	EducSchoolApiConstants educSchoolApiConstants;
    

    @SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(SchoolService.class);

     /**
     * Get all Schools in School DTO
     *
     * @return Course 
     * @throws java.lang.Exception
     */
    public List<School> getSchoolList() {
        List<School> schoolList  = schoolTransformer.transformToDTO(schoolRepository.findAll());  
    	schoolList.forEach(sL -> {
    		District dist = districtTransformer.transformToDTO(districtRepository.findById(sL.getMinCode().substring(0, 3)));
    		sL.setDistrictName(dist.getDistrictName());
    	});
        return schoolList;
    }

	public School getSchoolDetails(String minCode,String accessToken) {
		School school =  schoolTransformer.transformToDTO(schoolRepository.findById(minCode));
		if(school != null) {
			District dist = districtTransformer.transformToDTO(districtRepository.findById(school.getMinCode().substring(0, 3)));
			if(dist != null)
				school.setDistrictName(dist.getDistrictName());
			GradCountry country = webClient.get()
					.uri(String.format(educSchoolApiConstants.getCountryByCountryCodeUrl(), school.getCountryCode()))
					.headers(h -> h.setBearerAuth(accessToken))
					.retrieve()
					.bodyToMono(GradCountry.class).block();
	        if(country != null) {
	        	school.setCountryName(country.getCountryName());
			}
	        GradProvince province = webClient.get()
					.uri(String.format(educSchoolApiConstants.getProvinceByProvinceCodeUrl(), school.getProvCode()))
					.headers(h -> h.setBearerAuth(accessToken))
					.retrieve()
	        		.bodyToMono(GradProvince.class).block();
	        if(province != null) {
	        	school.setProvinceName(province.getProvName());
			}
		}
		return school;
	}

	public List<School> getSchoolsByParams(String schoolName, String minCode,String accessToken) {    	
		CriteriaHelper criteria = new CriteriaHelper();
        getSearchCriteria("minCode", minCode, criteria);
        getSearchCriteria("schoolName", schoolName, criteria);
		List<School> schoolList = schoolTransformer.transformToDTO(schoolCriteriaQueryRepository.findByCriteria(criteria, SchoolEntity.class));
    	schoolList.forEach(sL -> {
    		District dist = districtTransformer.transformToDTO(districtRepository.findById(sL.getMinCode().substring(0, 3)));
    		sL.setDistrictName(dist.getDistrictName());
    		GradCountry country = webClient.get()
					.uri(String.format(educSchoolApiConstants.getCountryByCountryCodeUrl(), sL.getCountryCode()))
					.headers(h -> h.setBearerAuth(accessToken))
					.retrieve()
					.bodyToMono(GradCountry.class).block();
	        if(country != null) {
	        	sL.setCountryName(country.getCountryName());
			}
	        GradProvince province = webClient.get()
					.uri(String.format(educSchoolApiConstants.getProvinceByProvinceCodeUrl(), sL.getProvCode()))
					.headers(h -> h.setBearerAuth(accessToken))
					.retrieve()
	        		.bodyToMono(GradProvince.class).block();
	        if(province != null) {
	        	sL.setProvinceName(province.getProvName());
			}
    	});
    	return schoolList;
	}
	public CriteriaHelper getSearchCriteria(String roolElement, String value, CriteriaHelper criteria) {
        if (StringUtils.isNotBlank(value)) {
            if (StringUtils.contains(value, "*")) {
                criteria.add(roolElement, OperationEnum.STARTS_WITH_IGNORE_CASE, StringUtils.strip(value.toUpperCase(), "*"));
            } else {
                criteria.add(roolElement, OperationEnum.EQUALS, value.toUpperCase());
            }
        }
        return criteria;
    }
}
