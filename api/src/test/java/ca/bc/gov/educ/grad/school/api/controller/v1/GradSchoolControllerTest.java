package ca.bc.gov.educ.grad.school.api.controller.v1;

import ca.bc.gov.educ.grad.school.api.GradSchoolApiResourceApplication;
import ca.bc.gov.educ.grad.school.api.constants.v1.URL;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.SubmissionModeCodeEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolHistoryRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.SubmissionModeCodeRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import ca.bc.gov.educ.grad.school.api.struct.v1.Search;
import ca.bc.gov.educ.grad.school.api.struct.v1.SearchCriteria;
import ca.bc.gov.educ.grad.school.api.struct.v1.ValueType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ca.bc.gov.educ.grad.school.api.filter.FilterOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { GradSchoolApiResourceApplication.class })
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GradSchoolControllerTest {
  protected final static ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  GradSchoolRepository gradSchoolRepository;

  @Autowired
  SubmissionModeCodeRepository submissionModeCodeRepository;
  @Autowired
  GradSchoolHistoryRepository gradSchoolHistoryRepository;

  @BeforeEach
  public void before(){
    MockitoAnnotations.openMocks(this);
    this.submissionModeCodeRepository.save(this.createSubmissionCodeData());
  }

  /**
   * need to delete the records to make it working in unit tests assertion, else the records will keep growing and assertions will fail.
   */
  @AfterEach
  public void after() {
    this.gradSchoolRepository.deleteAll();
    this.submissionModeCodeRepository.deleteAll();
    gradSchoolHistoryRepository.deleteAll();
  }

  @Test
  void testCreateSchool_GivenValidPayload_ShouldReturnStatusOK() throws Exception {
    GradSchool gradSchool = new GradSchool();
    gradSchool.setSchoolID(UUID.randomUUID());
    gradSchool.setSubmissionModeCode("REPLACE");
    gradSchool.setCanIssueTranscripts("N");
    gradSchool.setCanIssueCertificates("N");
    gradSchool.setCreateUser("ABC");
    gradSchool.setUpdateUser("ABC");

    this.mockMvc.perform(post(URL.BASE_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(asJsonString(gradSchool))
        .with(jwt().jwt(jwt -> jwt.claim("scope", "WRITE_GRAD_SCHOOL"))))
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(MockMvcResultMatchers.jsonPath("$.schoolID").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.submissionModeCode").value("REPLACE"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.canIssueCertificates").value("N"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.canIssueTranscripts").value("N"));
  }

  public static String asJsonString(final Object obj) {
    try {
      ObjectMapper om = new ObjectMapper();
      om.registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      return om.writeValueAsString(obj);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private SubmissionModeCodeEntity createSubmissionCodeData() {
    return SubmissionModeCodeEntity.builder().submissionModeCode("REPLACE").description("Replace")
            .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("Replace").createDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }

  @Test
  void testReadSchoolHistoryPaginated_givenValueNull_ShouldReturnStatusOk() throws Exception {
    final GrantedAuthority grantedAuthority = () -> "SCOPE_READ_GRAD_SCHOOL";
    final var mockAuthority = oidcLogin().authorities(grantedAuthority);

    final GradSchoolEntity entity = this.gradSchoolRepository.save(createGradSchoolData());
    this.gradSchoolHistoryRepository.save(createHistorySchoolData(entity.getSchoolID()));
    final SearchCriteria criteria = SearchCriteria.builder().key("canIssueTranscripts").operation(FilterOperation.EQUAL).value("N").valueType(ValueType.STRING).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final List<Search> searches = new LinkedList<>();
    searches.add(Search.builder().searchCriteriaList(criteriaList).build());
    final String criteriaJSON = objectMapper.writeValueAsString(searches);
    final MvcResult result = this.mockMvc.perform(get(URL.BASE_URL + "/history/paginated").with(mockAuthority).param("searchCriteriaList", criteriaJSON)
            .contentType(APPLICATION_JSON)).andReturn();
    this.mockMvc.perform(asyncDispatch(result)).andDo(print()).andExpect(status().isOk());
  }

  @Test
  void testReadSchoolHistoryPaginated_givenWrongRole_ShouldReturnStatusForbidden() throws Exception {
    final GrantedAuthority grantedAuthority = () -> "UNAUTHORIZED";
    final var mockAuthority = oidcLogin().authorities(grantedAuthority);

    final GradSchoolEntity entity = this.gradSchoolRepository.save(createGradSchoolData());
    this.gradSchoolHistoryRepository.save(createHistorySchoolData(entity.getSchoolID()));
    final SearchCriteria criteria = SearchCriteria.builder().key("canIssueTranscripts").operation(FilterOperation.EQUAL).value("N").valueType(ValueType.STRING).build();
    final List<SearchCriteria> criteriaList = new ArrayList<>();
    criteriaList.add(criteria);
    final List<Search> searches = new LinkedList<>();
    searches.add(Search.builder().searchCriteriaList(criteriaList).build());
    final String criteriaJSON = objectMapper.writeValueAsString(searches);
    final ResultActions result =  this.mockMvc.perform(get(URL.BASE_URL + "/history/paginated").with(mockAuthority).param("searchCriteriaList", criteriaJSON)
            .contentType(APPLICATION_JSON)).andDo(print());
    result.andExpect(status().isForbidden());
  }

  private GradSchoolEntity createGradSchoolData() {
    return GradSchoolEntity.builder()
            .schoolID(UUID.randomUUID())
            .submissionModeCode("REPLACE")
            .canIssueTranscripts("N")
            .canIssueCertificates("N")
            .createUser("ABC")
            .updateUser("ABC")
            .build();
  }

  private GradSchoolHistoryEntity createHistorySchoolData(UUID schoolId) {
    return GradSchoolHistoryEntity.builder()
            .schoolID(schoolId)
            .submissionModeCode("REPLACE")
            .canIssueTranscripts("N")
            .canIssueCertificates("N")
            .createUser("ABC")
            .updateUser("ABC")
            .build();
  }

}


