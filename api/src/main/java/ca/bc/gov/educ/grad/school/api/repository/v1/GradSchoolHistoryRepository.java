package ca.bc.gov.educ.grad.school.api.repository.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradSchoolHistoryRepository extends JpaRepository<GradSchoolHistoryEntity, UUID>, JpaSpecificationExecutor<GradSchoolHistoryEntity> {
    List<GradSchoolHistoryEntity> findAllBySchoolID(UUID schoolID);

    List<GradSchoolHistoryEntity> findAllByGradSchoolID(UUID gradSchoolID);
}
