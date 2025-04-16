package ca.bc.gov.educ.grad.school.api.controller.v1;

import ca.bc.gov.educ.grad.school.api.endpoint.v1.CodeTableAPIEndpoint;
import ca.bc.gov.educ.grad.school.api.mapper.v1.CodeTableMapper;
import ca.bc.gov.educ.grad.school.api.service.v1.CodeTableService;
import ca.bc.gov.educ.grad.school.api.struct.v1.SubmissionModeCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class CodeTableAPIController implements CodeTableAPIEndpoint {

  private static final CodeTableMapper mapper = CodeTableMapper.mapper;
  @Getter(AccessLevel.PRIVATE)
  private final CodeTableService service;

  @Autowired
  public CodeTableAPIController(CodeTableService service) {
    this.service = service;
  }

  public List<SubmissionModeCode> getSubmissionModeCodes() {
    return getService().getAllSubmissionModeCodes().stream().map(mapper::toStructure).collect(Collectors.toList());
  }

}
