package ca.bc.gov.educ.api.school.repository;

import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;
import ca.bc.gov.educ.api.school.util.criteria.CriteriaQueryRepository;

@Repository
public interface SchoolCriteriaQueryRepository extends CriteriaQueryRepository<SchoolEntity> {

}
