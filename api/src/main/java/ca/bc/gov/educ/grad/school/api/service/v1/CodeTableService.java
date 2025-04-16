package ca.bc.gov.educ.grad.school.api.service.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.SubmissionModeCodeEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.SubmissionModeCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CodeTableService {

  private final SubmissionModeCodeRepository submissionModeCodeRepository;

  public CodeTableService(SubmissionModeCodeRepository submissionModeCodeRepository) {
      this.submissionModeCodeRepository = submissionModeCodeRepository;
  }


  public List<SubmissionModeCodeEntity> getAllSubmissionModeCodes() {
    return submissionModeCodeRepository.findAll();
  }

}
