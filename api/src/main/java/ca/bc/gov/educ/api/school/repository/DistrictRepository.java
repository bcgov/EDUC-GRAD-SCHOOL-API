package ca.bc.gov.educ.api.school.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.school.model.entity.DistrictEntity;

@Repository
public interface DistrictRepository extends JpaRepository<DistrictEntity, String> {

    List<DistrictEntity> findAll();
    List<DistrictEntity> findByDistrictNameContaining(String districtName);

}
