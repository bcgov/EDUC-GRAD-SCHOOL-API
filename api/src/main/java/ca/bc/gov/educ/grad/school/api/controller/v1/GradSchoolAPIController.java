package ca.bc.gov.educ.grad.school.api.controller.v1;

import ca.bc.gov.educ.grad.school.api.endpoint.v1.GradSchoolAPIEndpoint;
import ca.bc.gov.educ.grad.school.api.mapper.v1.GradSchoolMapper;
import ca.bc.gov.educ.grad.school.api.messaging.jetstream.Publisher;
import ca.bc.gov.educ.grad.school.api.service.v1.GradSchoolHistoryService;
import ca.bc.gov.educ.grad.school.api.service.v1.GradSchoolService;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchoolHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class GradSchoolAPIController implements GradSchoolAPIEndpoint {

  private final Publisher publisher;

  private static final GradSchoolMapper mapper = GradSchoolMapper.mapper;

  private final GradSchoolService gradSchoolService;

  private final GradSchoolHistoryService gradSchoolHistoryService;

  public GradSchoolAPIController(Publisher publisher, GradSchoolService gradSchoolService, GradSchoolHistoryService gradSchoolHistoryService) {
      this.publisher = publisher;
      this.gradSchoolService = gradSchoolService;
      this.gradSchoolHistoryService = gradSchoolHistoryService;
  }

  @Override
  public GradSchool getGradSchool(UUID gradSchoolID) {
    return mapper.toStructure(gradSchoolService.getGradSchool(gradSchoolID));
  }

  @Override
  public List<GradSchoolHistory> getGradSchoolHistory(UUID gradSchoolID) {
    return gradSchoolHistoryService.getAllGradSchoolHistoryList(gradSchoolID).stream().map(mapper::toStructure).toList();
  }

  @Override
  public GradSchool getGradSchoolBySchoolID(UUID schoolID) {
    return mapper.toStructure(gradSchoolService.getGradSchoolBySchoolID(schoolID));
  }

  @Override
  public List<GradSchoolHistory> getGradSchoolHistoryBySchoolID(UUID schoolID) {
    return gradSchoolHistoryService.getAllGradSchoolHistoryListBySchoolID(schoolID).stream().map(mapper::toStructure).toList();
  }

  @Override
  public GradSchool createGradSchool(GradSchool gradSchool) {
    return mapper.toStructure(gradSchoolService.createGradSchool(gradSchool));
  }

  @Override
  public GradSchool updateGradSchool(UUID gradSchoolID, GradSchool gradSchool) throws JsonProcessingException {
    var updatePair = gradSchoolService.updateGradSchool(gradSchool, gradSchoolID);
    publisher.dispatchChoreographyEvent(updatePair.getRight());
    return mapper.toStructure(updatePair.getLeft());
  }

  @Override
  public List<GradSchool> getAllGradSchools() {
    return gradSchoolService.getAllGradSchoolsList().stream().map(mapper::toStructure).toList();
  }

}
