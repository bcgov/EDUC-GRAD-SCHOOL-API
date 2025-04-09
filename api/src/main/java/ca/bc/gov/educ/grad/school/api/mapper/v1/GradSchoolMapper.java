package ca.bc.gov.educ.grad.school.api.mapper.v1;

import ca.bc.gov.educ.grad.school.api.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.grad.school.api.mapper.StringMapper;
import ca.bc.gov.educ.grad.school.api.mapper.UUIDMapper;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchoolHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class})
@SuppressWarnings("squid:S1214")
public interface GradSchoolMapper {

  GradSchoolMapper mapper = Mappers.getMapper(GradSchoolMapper.class);

  GradSchoolEntity toModel(GradSchool structure);

  GradSchool toStructure(GradSchoolEntity entity);

  GradSchoolHistory toStructure(GradSchoolHistoryEntity entity);

}
