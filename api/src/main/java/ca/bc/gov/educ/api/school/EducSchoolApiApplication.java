package ca.bc.gov.educ.api.school;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
public class EducSchoolApiApplication {

	private static Logger logger = LoggerFactory.getLogger(EducSchoolApiApplication.class);
    
	public static void main(String[] args) {
		logger.debug("########Starting API");
		SpringApplication.run(EducSchoolApiApplication.class, args);
		logger.debug("########Started API");
	}

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(SchoolEntity.class, School.class);
		modelMapper.typeMap(School.class, SchoolEntity.class);
		return modelMapper;
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}	
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("https://educ-grad-admin-master-wbmfsf-dev.pathfinder.gov.bc.ca");
			}
		};
	}
  
}