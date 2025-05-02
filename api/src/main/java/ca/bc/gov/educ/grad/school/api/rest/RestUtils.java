package ca.bc.gov.educ.grad.school.api.rest;


import ca.bc.gov.educ.grad.school.api.exception.GradSchoolAPIRuntimeException;
import ca.bc.gov.educ.grad.school.api.properties.ApplicationProperties;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolHistory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * This class is used for REST calls
 *
 * @author Marco Villeneuve
 */
@Component
@Slf4j
public class RestUtils {
  private static final String CONTENT_TYPE = "Content-Type";
  public static final String PAGE_SIZE_VALUE = "5";
  private final WebClient webClient;
  @Getter
  private final ApplicationProperties props;

  @Autowired
  public RestUtils(WebClient webClient, final ApplicationProperties props) {
    this.webClient = webClient;
    this.props = props;
  }

  public Page<SchoolHistory> getSchoolHistoryPaginatedFromInstituteApi(String schoolID) {
    try {
      String criterion = "[{\"condition\":null,\"searchCriteriaList\":[" +
              "{\"key\":\"schoolId\",\"operation\":\"eq\",\"value\":\"" + schoolID + "\",\"valueType\":\"UUID\",\"condition\":\"AND\"}" +
              "]}]";
      return webClient.get()
              .uri(getSchoolHistoryURI(criterion))
              .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<RestResponsePage<SchoolHistory>>() {})
              .block();
    } catch (Exception ex) {
      log.error("Error fetching school history on page {} {}", 0, ex);
      throw new GradSchoolAPIRuntimeException("Error fetching school history on page 0  " + ex.getMessage());
    }
  }

  private URI getSchoolHistoryURI(String criterion){
    return UriComponentsBuilder.fromHttpUrl(this.props.getInstituteApiURL() + "/school/history/paginated")
            .queryParam("pageNumber", "0")
            .queryParam("pageSize", PAGE_SIZE_VALUE)
            .queryParam("sort", "{\"createDate\":\"DESC\"}")
            .queryParam("searchCriteriaList", criterion).build().toUri();
  }
}
