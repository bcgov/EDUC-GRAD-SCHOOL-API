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
    int pageSize = Integer.parseInt(PAGE_SIZE_VALUE);
    try {
      String fullUrl = this.props.getInstituteApiURL()
              + "/school/history/paginated"
              + "?pageNumber=" + 0
              + "&pageSize=" + pageSize
              + "&sort={\"createDate\":\"DESC\"}"
              + "&searchCriteriaList=[{\"condition\":null,\"searchCriteriaList\":[{\"key\":\"schoolId\",\"value\":\"" + schoolID + "\",\"operation\":\"eq\",\"valueType\":\"UUID\"}]";
      return webClient.get()
              .uri(fullUrl)
              .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .retrieve()
              .bodyToMono(new ParameterizedTypeReference<Page<SchoolHistory>>() {})
              .block();
    } catch (Exception ex) {
      log.error("Error fetching school history on page {} {}", 0, ex);
      throw new GradSchoolAPIRuntimeException("Error fetching school history on page 0  " + ex.getMessage());
    }
  }
}
