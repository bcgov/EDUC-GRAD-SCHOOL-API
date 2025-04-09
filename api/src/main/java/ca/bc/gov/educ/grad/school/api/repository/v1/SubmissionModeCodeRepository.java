package ca.bc.gov.educ.grad.school.api.repository.v1;

import ca.bc.gov.educ.grad.school.api.model.v1.SubmissionModeCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionModeCodeRepository extends JpaRepository<SubmissionModeCodeEntity, String> {

}
