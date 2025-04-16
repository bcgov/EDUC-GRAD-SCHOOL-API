package ca.bc.gov.educ.grad.school.api.mapper.v1;

import ca.bc.gov.educ.grad.school.api.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.grad.school.api.mapper.StringMapper;
import ca.bc.gov.educ.grad.school.api.model.v1.SubmissionModeCodeEntity;
import ca.bc.gov.educ.grad.school.api.struct.v1.SubmissionModeCode;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * The interface Student mapper.
 */
@Mapper(uses = {LocalDateTimeMapper.class, StringMapper.class})
@SuppressWarnings("squid:S1214")
public interface CodeTableMapper {

  CodeTableMapper mapper = Mappers.getMapper(CodeTableMapper.class);

  SubmissionModeCodeEntity toModel(SubmissionModeCode structure);

  SubmissionModeCode toStructure(SubmissionModeCodeEntity entity);

}
