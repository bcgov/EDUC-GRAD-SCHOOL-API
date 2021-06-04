package ca.bc.gov.educ.api.school.repository;

import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;
import ca.bc.gov.educ.api.school.util.criteria.CriteriaQueryRepositoryImpl;

@Repository
public class SchoolCriteriaQueryRepositoryImpl extends CriteriaQueryRepositoryImpl<SchoolEntity> implements SchoolCriteriaQueryRepository {

}
