package ca.bc.gov.educ.grad.school.api.controller.v1;

import ca.bc.gov.educ.grad.school.api.endpoint.v1.GradSchoolAPIEndpoint;
import ca.bc.gov.educ.grad.school.api.mapper.v1.GradSchoolMapper;
import ca.bc.gov.educ.grad.school.api.messaging.jetstream.Publisher;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import ca.bc.gov.educ.grad.school.api.service.v1.GradSchoolHistorySearchService;
import ca.bc.gov.educ.grad.school.api.service.v1.GradSchoolHistoryService;
import ca.bc.gov.educ.grad.school.api.service.v1.GradSchoolService;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchoolHistory;
import ca.bc.gov.educ.grad.school.api.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
public class GradSchoolAPIController implements GradSchoolAPIEndpoint {

  private final Publisher publisher;

  private static final GradSchoolMapper mapper = GradSchoolMapper.mapper;

  private final GradSchoolService gradSchoolService;

  private final GradSchoolHistoryService gradSchoolHistoryService;

  private final GradSchoolHistorySearchService gradSchoolHistorySearchService;

  public GradSchoolAPIController(Publisher publisher, GradSchoolService gradSchoolService, GradSchoolHistoryService gradSchoolHistoryService, GradSchoolHistorySearchService gradSchoolHistorySearchService) {
      this.publisher = publisher;
      this.gradSchoolService = gradSchoolService;
      this.gradSchoolHistoryService = gradSchoolHistoryService;
      this.gradSchoolHistorySearchService = gradSchoolHistorySearchService;
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

  @Override
  public CompletableFuture<Page<GradSchoolHistory>> schoolHistoryFindAll(Integer pageNumber, Integer pageSize, String sortCriteriaJson, String searchCriteriaListJson) {
    final List<Sort.Order> sorts = new ArrayList<>();
    Specification<GradSchoolHistoryEntity> schoolHistorySpecs = gradSchoolHistorySearchService.setSpecificationAndSortCriteria(sortCriteriaJson, searchCriteriaListJson, JsonUtil.mapper, sorts);
    return this.gradSchoolHistorySearchService.findAll(schoolHistorySpecs, pageNumber, pageSize, sorts).thenApplyAsync(schoolHistoryEntities -> schoolHistoryEntities.map(mapper::toStructure));

  }

}
