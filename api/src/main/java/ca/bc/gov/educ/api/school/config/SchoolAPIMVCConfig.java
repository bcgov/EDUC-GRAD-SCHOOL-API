package ca.bc.gov.educ.api.school.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The type Pen demog mvc config.
 */
@Configuration
public class SchoolAPIMVCConfig implements WebMvcConfigurer {

    @Getter(AccessLevel.PRIVATE)
    private final SchoolAPIRequestInterceptor schoolAPIRequestInterceptor;

  /**
   * Instantiates a new School demog mvc config.
   *
   * @param SchoolAPIRequestInterceptor the pen demog request interceptor
   */
  @Autowired
    public SchoolAPIMVCConfig(final SchoolAPIRequestInterceptor penDemogRequestInterceptor){
        this.schoolAPIRequestInterceptor = penDemogRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(schoolAPIRequestInterceptor).addPathPatterns("/**/**/");
    }
}
