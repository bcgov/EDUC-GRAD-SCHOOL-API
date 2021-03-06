package ca.bc.gov.educ.api.school.model.transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.bc.gov.educ.api.school.model.dto.District;
import ca.bc.gov.educ.api.school.model.entity.DistrictEntity;

@Component
public class DistrictTransformer {

    @Autowired
    ModelMapper modelMapper;

    public District transformToDTO (DistrictEntity districtEntity) {
        District district = modelMapper.map(districtEntity, District.class);
        return district;
    }

    public District transformToDTO ( Optional<DistrictEntity> districtEntity ) {
        DistrictEntity cae = new DistrictEntity();

        if (districtEntity.isPresent()) {
            cae = districtEntity.get();
	        District district = modelMapper.map(cae, District.class);
	        return district;
        }
        return null;
    }

	public List<District> transformToDTO (Iterable<DistrictEntity> districtEntities ) {

        List<District> districtList = new ArrayList<District>();

        for (DistrictEntity districtEntity : districtEntities) {
            District district = new District();
            district = modelMapper.map(districtEntity, District.class);            
            districtList.add(district);
        }

        return districtList;
    }

    public DistrictEntity transformToEntity(District district) {
        DistrictEntity districtEntity = modelMapper.map(district, DistrictEntity.class);
        return districtEntity;
    }
}
