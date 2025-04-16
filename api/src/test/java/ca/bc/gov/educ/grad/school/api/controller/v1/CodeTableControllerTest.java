package ca.bc.gov.educ.grad.school.api.controller.v1;

import ca.bc.gov.educ.grad.school.api.GradSchoolApiResourceApplication;
import ca.bc.gov.educ.grad.school.api.constants.v1.URL;
import ca.bc.gov.educ.grad.school.api.mapper.v1.CodeTableMapper;
import ca.bc.gov.educ.grad.school.api.model.v1.SubmissionModeCodeEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.SubmissionModeCodeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = GradSchoolApiResourceApplication.class)
@AutoConfigureMockMvc
public class CodeTableControllerTest {

  private static final CodeTableMapper mapper = CodeTableMapper.mapper;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  SubmissionModeCodeRepository submissionModeCodeRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    this.submissionModeCodeRepository.save(this.createSubmissionCodeData());
  }

  /**
   * need to delete the records to make it working in unit tests assertion, else the records will keep growing and assertions will fail.
   */
  @After
  public void after() {
    this.submissionModeCodeRepository.deleteAll();
  }

  private SubmissionModeCodeEntity createSubmissionCodeData() {
    return SubmissionModeCodeEntity.builder().submissionModeCode("REPLACE").description("Replace")
        .effectiveDate(LocalDateTime.now()).expiryDate(LocalDateTime.MAX).displayOrder(1).label("Replace").createDate(LocalDateTime.now())
        .updateDate(LocalDateTime.now()).createUser("TEST").updateUser("TEST").build();
  }

  @Test
  public void testGetSubmissionTypeCodes_ShouldReturnCodes() throws Exception {
    final GrantedAuthority grantedAuthority = () -> "SCOPE_READ_GRAD_SCHOOL_CODES";
    final var mockAuthority = oidcLogin().authorities(grantedAuthority);
    this.mockMvc.perform(get(URL.BASE_URL_CODES + URL.SUBMISSION_MODE_CODES).with(mockAuthority)).andDo(print()).andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].submissionModeCode").value("REPLACE"));
  }

}


