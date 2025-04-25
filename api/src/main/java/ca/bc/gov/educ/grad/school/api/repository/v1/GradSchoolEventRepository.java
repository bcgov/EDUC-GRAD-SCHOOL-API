package ca.bc.gov.educ.grad.school.api.repository.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradSchoolEventRepository extends JpaRepository<GradSchoolEventEntity, UUID>, JpaSpecificationExecutor<GradSchoolEventEntity> {
    Optional<GradSchoolEventEntity> findByEventId(UUID eventId);

    @Query(value = "select event.* from GRAD_SCHOOL_EVENT event where event.EVENT_STATUS = :eventStatus " +
            "AND event.CREATE_DATE < :createDate " +
            "ORDER BY event.CREATE_DATE asc " +
            "FETCH FIRST :limit ROWS ONLY", nativeQuery=true)
    List<GradSchoolEventEntity> findAllByEventStatusAndCreateDateBeforeOrderByCreateDate(String eventStatus, LocalDateTime createDate, int limit);
}
