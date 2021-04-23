package ca.bc.gov.educ.api.school.service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.school.model.dto.District;
import ca.bc.gov.educ.api.school.model.dto.GradCountry;
import ca.bc.gov.educ.api.school.model.dto.GradProvince;
import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.entity.DistrictEntity;
import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;
import ca.bc.gov.educ.api.school.model.transformer.DistrictTransformer;
import ca.bc.gov.educ.api.school.model.transformer.SchoolTransformer;
import ca.bc.gov.educ.api.school.repository.DistrictRepository;
import ca.bc.gov.educ.api.school.repository.SchoolRepository;
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
    RestTemplate restTemplate;
    
    @Autowired
    WebClient webClient;
    
    @Value(EducSchoolApiConstants.ENDPOINT_COUNTRY_BY_COUNTRY_CODE_URL)
    private String getCountryByCountryCodeURL;
    
    @Value(EducSchoolApiConstants.ENDPOINT_PROVINCE_BY_PROV_CODE_URL)
    private String getProvinceByProvCodeURL;
    

    private static Logger logger = LoggerFactory.getLogger(SchoolService.class);

     /**
     * Get all Schools in School DTO
     * @param pageSize 
     * @param pageNo 
     * @param accessToken 
     *
     * @return Course 
     * @throws java.lang.Exception
     */
    public List<School> getSchoolList(Integer pageNo, Integer pageSize, String accessToken) {
        List<School> schoolList  = new ArrayList<School>();

        try {
//        	Pageable paging = PageRequest.of(pageNo, pageSize);        	 
//          Page<SchoolEntity> pagedResult = schoolRepository.findAll(paging);
        	schoolList = schoolTransformer.transformToDTO(schoolRepository.findAll());  
        	schoolList.forEach(sL -> {
        		District dist = districtTransformer.transformToDTO(districtRepository.findById(sL.getMinCode().substring(0, 3)));
        		sL.setDistrictName(dist.getDistrictName());
//        		GradCountry country = webClient.get().uri(String.format(getCountryByCountryCodeURL, sL.getCountryCode())).headers(h -> h.setBearerAuth(accessToken)).retrieve()
//    					.bodyToMono(GradCountry.class).block();
//        		if(country != null) {
//                	sL.setCountryName(country.getCountryName());
//        		}
//                GradProvince province = webClient.get().uri(String.format(getProvinceByProvCodeURL, sL.getProvCode())).headers(h -> h.setBearerAuth(accessToken)).retrieve()
//    	        		.bodyToMono(GradProvince.class).block();
//                if(province != null) {
//                	sL.setProvinceName(province.getProvName());
//        		}
        	});
        } catch (Exception e) {
            logger.debug("Exception:" + e);
        }

        return schoolList;
    }

	public School getSchoolDetails(String minCode,String accessToken) {
		School school =  schoolTransformer.transformToDTO(schoolRepository.findById(minCode));
		if(school != null) {
			District dist = districtTransformer.transformToDTO(districtRepository.findById(school.getMinCode().substring(0, 3)));
			if(dist != null)
				school.setDistrictName(dist.getDistrictName());
			GradCountry country = webClient.get().uri(String.format(getCountryByCountryCodeURL, school.getCountryCode())).headers(h -> h.setBearerAuth(accessToken)).retrieve()
					.bodyToMono(GradCountry.class).block();
	        if(country != null) {
	        	school.setCountryName(country.getCountryName());
			}
	        GradProvince province = webClient.get().uri(String.format(getProvinceByProvCodeURL, school.getProvCode())).headers(h -> h.setBearerAuth(accessToken)).retrieve()
	        		.bodyToMono(GradProvince.class).block();
	        if(province != null) {
	        	school.setProvinceName(province.getProvName());
			}
		}
		return school;
	}

	public List<School> getSchoolsByParams(String schoolName, String districtName, String city,
			Integer pageNo, Integer pageSize, String accessToken) {
		Set<String> minCodeList = new HashSet<>();		
    	String isMincodeListIncluded = null;    	
    	if(StringUtils.isNotBlank(districtName)) {
    		List<DistrictEntity> districtList = null;
    		districtList = districtRepository.findByDistrictNameContaining(StringUtils.toRootUpperCase(districtName));
	    	if(!districtList.isEmpty()) {
	    		isMincodeListIncluded = "Yes";			  
	    		districtList.forEach(sty -> {
	    			List<SchoolEntity> schoolList = schoolRepository.findByMinCodeStartsWith(sty.getDistrictNumber());
	    			schoolList.forEach(school -> {
	    				minCodeList.add(school.getMinCode());
	    			});
	    						
				});	
	    	}
    	}
    	List<School> schoolList = schoolTransformer.transformToDTO(schoolRepository.searchForSchool(StringUtils.toRootUpperCase(StringUtils.strip(schoolName, "*")),isMincodeListIncluded,minCodeList,StringUtils.toRootUpperCase(StringUtils.strip(city, "*"))));
    	schoolList.forEach(sL -> {
    		District dist = districtTransformer.transformToDTO(districtRepository.findById(sL.getMinCode().substring(0, 3)));
    		sL.setDistrictName(dist.getDistrictName());
    		
    		GradCountry country = webClient.get().uri(String.format(getCountryByCountryCodeURL, sL.getCountryCode())).headers(h -> h.setBearerAuth(accessToken)).retrieve()
					.bodyToMono(GradCountry.class).block();
    		if(country != null) {
            	sL.setCountryName(country.getCountryName());
    		}
            GradProvince province = webClient.get().uri(String.format(getProvinceByProvCodeURL, sL.getProvCode())).headers(h -> h.setBearerAuth(accessToken)).retrieve()
	        		.bodyToMono(GradProvince.class).block();
            if(province != null) {
            	sL.setProvinceName(province.getProvName());
    		}
    	});
    	return schoolList;
	}
}
