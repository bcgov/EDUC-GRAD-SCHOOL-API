package ca.bc.gov.educ.grad.school.api.controller.v1;

import ca.bc.gov.educ.grad.school.api.GradSchoolApiResourceApplication;
import ca.bc.gov.educ.grad.school.api.constants.v1.URL;
import ca.bc.gov.educ.grad.school.api.model.v1.SubmissionModeCodeEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolHistoryRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.SubmissionModeCodeRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
  }

  @Test
  void testCreateSchool_GivenValidPayload_ShouldReturnStatusOK() throws Exception {
    GradSchool gradSchool = new GradSchool();
    gradSchool.setSchoolID(UUID.randomUUID());
    gradSchool.setSubmissionModeCode("REPLACE");
    gradSchool.setCanIssueTranscripts(false);
    gradSchool.setCanIssueCertificates(false);
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
      .andExpect(MockMvcResultMatchers.jsonPath("$.canIssueCertificates").value(false))
      .andExpect(MockMvcResultMatchers.jsonPath("$.canIssueTranscripts").value(false));
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

}


