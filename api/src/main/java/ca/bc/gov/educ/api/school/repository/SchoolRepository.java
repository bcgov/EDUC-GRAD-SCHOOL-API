package ca.bc.gov.educ.api.school.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;

@Repository
public interface SchoolRepository extends JpaRepository<SchoolEntity, String> {

    List<SchoolEntity> findAll();

}
