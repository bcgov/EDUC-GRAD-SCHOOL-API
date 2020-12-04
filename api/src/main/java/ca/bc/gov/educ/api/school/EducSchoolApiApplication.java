package ca.bc.gov.educ.api.school;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;

@SpringBootApplication
public class EducSchoolApiApplication {

	private static Logger logger = LoggerFactory.getLogger(EducSchoolApiApplication.class);

	@Value("${spring.security.user.name}")
	 private String uName;

    @Value("${spring.security.user.password}")
    private String pass;

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
		return builder.basicAuthentication(uName, pass).build();
	}
}