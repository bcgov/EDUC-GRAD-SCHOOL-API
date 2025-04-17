package ca.bc.gov.educ.grad.school.api.service.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GradSchoolHistoryService {

  private final GradSchoolHistoryRepository gradSchoolHistoryRepository;

  @Autowired
  public GradSchoolHistoryService(GradSchoolHistoryRepository gradSchoolHistoryRepository) {
    this.gradSchoolHistoryRepository = gradSchoolHistoryRepository;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void createSchoolHistory(GradSchoolEntity curGradSchoolEntity, String updateUser) {
    final GradSchoolHistoryEntity gradSchoolHistoryEntity = new GradSchoolHistoryEntity();
    BeanUtils.copyProperties(curGradSchoolEntity, gradSchoolHistoryEntity);
    gradSchoolHistoryEntity.setCreateUser(updateUser);
    gradSchoolHistoryEntity.setCreateDate(LocalDateTime.now());
    gradSchoolHistoryEntity.setUpdateUser(updateUser);
    gradSchoolHistoryEntity.setUpdateDate(LocalDateTime.now());
    gradSchoolHistoryRepository.save(gradSchoolHistoryEntity);
  }

  public List<GradSchoolHistoryEntity> getAllGradSchoolHistoryList(UUID gradSchoolID) {
    return gradSchoolHistoryRepository.findAllByGradSchoolID(gradSchoolID);
  }

  public List<GradSchoolHistoryEntity> getAllGradSchoolHistoryListBySchoolID(UUID schoolID) {
    return gradSchoolHistoryRepository.findAllBySchoolID(schoolID);
  }
}
