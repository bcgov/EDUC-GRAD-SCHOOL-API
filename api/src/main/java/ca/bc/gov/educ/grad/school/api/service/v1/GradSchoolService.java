package ca.bc.gov.educ.grad.school.api.service.v1;

import ca.bc.gov.educ.grad.school.api.constants.v1.EventOutcome;
import ca.bc.gov.educ.grad.school.api.exception.EntityNotFoundException;
import ca.bc.gov.educ.grad.school.api.exception.InvalidPayloadException;
import ca.bc.gov.educ.grad.school.api.exception.errors.ApiError;
import ca.bc.gov.educ.grad.school.api.mapper.v1.GradSchoolMapper;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import ca.bc.gov.educ.grad.school.api.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventType.UPDATE_GRAD_SCHOOL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class GradSchoolService {

  private static final String GRAD_SCHOOL_ID_ATTR = "gradSchoolID";

  private static final String SCHOOL_ID_ATTR = "schoolID";

  private static final String CREATE_DATE = "createDate";

  private static final String CREATE_USER = "createUser";

  private final GradSchoolEventRepository gradSchoolEventRepository;
  private final GradSchoolRepository gradSchoolRepository;
  private final GradSchoolHistoryService gradSchoolHistoryService;
  private static final GradSchoolMapper gradSchoolMapper = GradSchoolMapper.mapper;

  public GradSchoolService(GradSchoolEventRepository gradSchoolEventRepository, GradSchoolRepository gradSchoolRepository, GradSchoolHistoryService gradSchoolHistoryService) {
      this.gradSchoolEventRepository = gradSchoolEventRepository;
      this.gradSchoolRepository = gradSchoolRepository;
      this.gradSchoolHistoryService = gradSchoolHistoryService;
  }

  public List<GradSchoolEntity> getAllGradSchoolsList() {
    return gradSchoolRepository.findAll();
  }

  public GradSchoolEntity getGradSchool(UUID gradSchoolID) {
    return gradSchoolRepository.findById(gradSchoolID).orElseThrow(
            () -> new EntityNotFoundException(GradSchoolEntity.class, GRAD_SCHOOL_ID_ATTR, gradSchoolID.toString()));
  }

  public GradSchoolEntity getGradSchoolBySchoolID(UUID schoolID) {
    return gradSchoolRepository.findBySchoolID(schoolID).orElseThrow(
            () -> new EntityNotFoundException(GradSchoolEntity.class, SCHOOL_ID_ATTR, schoolID.toString()));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public GradSchoolEntity createGradSchool(GradSchool gradSchool) {
    var existingSchool = gradSchoolRepository.findBySchoolID(gradSchool.getSchoolID());

    if(existingSchool.isPresent()) {
      ApiError error = ApiError.builder().timestamp(LocalDateTime.now()).message("Payload contains invalid data.").status(BAD_REQUEST).build();
      final List<FieldError> apiValidationErrors = new ArrayList<>();
      apiValidationErrors.add(ValidationUtil.createFieldError("gradSchool", "schoolID", gradSchool.getSchoolID(), "School ID already exists as a GRAD school."));
      error.addValidationErrors(apiValidationErrors);
      throw new InvalidPayloadException(error);
    }

    RequestUtil.setAuditColumnsForCreate(gradSchool);
    var school = GradSchoolMapper.mapper.toModel(gradSchool);
    return saveGradSchoolWithHistory(school);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Pair<GradSchoolEntity, GradSchoolEventEntity> updateGradSchool(GradSchool gradSchool, UUID gradSchoolID) throws JsonProcessingException {
    RequestUtil.setAuditColumnsForUpdate(gradSchool);
    var school = GradSchoolMapper.mapper.toModel(gradSchool);
    if (gradSchoolID == null || !gradSchoolID.equals(school.getGradSchoolID())) {
      throw new EntityNotFoundException(GradSchoolEntity.class, GRAD_SCHOOL_ID_ATTR, String.valueOf(gradSchoolID));
    }

    var updatedSchool = updateGradSchoolHelper(school);

    final GradSchoolEventEntity gradSchoolEventEntity = EventUtil.createEvent(
            updatedSchool.getUpdateUser(), updatedSchool.getUpdateUser(),
            JsonUtil.getJsonStringFromObject(gradSchoolMapper.toStructure(updatedSchool)),
            UPDATE_GRAD_SCHOOL, EventOutcome.GRAD_SCHOOL_UPDATED);
    gradSchoolEventRepository.save(gradSchoolEventEntity);
    return Pair.of(updatedSchool, gradSchoolEventEntity);
  }

  private GradSchoolEntity updateGradSchoolHelper(GradSchoolEntity school) {
    GradSchoolEntity currentGradSchoolEntity = gradSchoolRepository.findById(school.getGradSchoolID()).orElseThrow(
            () -> new EntityNotFoundException(GradSchoolEntity.class, GRAD_SCHOOL_ID_ATTR, school.getGradSchoolID().toString()));

    BeanUtils.copyProperties(school, currentGradSchoolEntity, CREATE_DATE, CREATE_USER, "schoolID"); // update current student entity with incoming payload ignoring the fields.
    return saveGradSchoolWithHistory(currentGradSchoolEntity);
  }

  private GradSchoolEntity saveGradSchoolWithHistory(GradSchoolEntity currentGradSchoolEntity) {
    TransformUtil.uppercaseFields(currentGradSchoolEntity); // convert the input to upper case.
    GradSchoolEntity savedSchool = gradSchoolRepository.save(currentGradSchoolEntity);
    gradSchoolHistoryService.createSchoolHistory(savedSchool, savedSchool.getUpdateUser());

    return savedSchool;
  }

}
