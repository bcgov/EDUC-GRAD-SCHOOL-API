package ca.bc.gov.educ.grad.school.api.repository.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradSchoolEventRepository extends JpaRepository<GradSchoolEventEntity, UUID>, JpaSpecificationExecutor<GradSchoolEventEntity> {
    Optional<GradSchoolEventEntity> findByEventId(UUID eventId);
}
