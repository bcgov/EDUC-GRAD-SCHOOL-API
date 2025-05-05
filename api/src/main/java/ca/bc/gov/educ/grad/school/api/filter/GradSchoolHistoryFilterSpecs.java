package ca.bc.gov.educ.grad.school.api.filter;

import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class GradSchoolHistoryFilterSpecs extends BaseFilterSpecs<GradSchoolHistoryEntity> {

  public GradSchoolHistoryFilterSpecs(FilterSpecifications<GradSchoolHistoryEntity, ChronoLocalDate> dateFilterSpecifications, FilterSpecifications<GradSchoolHistoryEntity, ChronoLocalDateTime<?>> dateTimeFilterSpecifications, FilterSpecifications<GradSchoolHistoryEntity, Integer> integerFilterSpecifications, FilterSpecifications<GradSchoolHistoryEntity, String> stringFilterSpecifications, FilterSpecifications<GradSchoolHistoryEntity, Long> longFilterSpecifications, FilterSpecifications<GradSchoolHistoryEntity, UUID> uuidFilterSpecifications, FilterSpecifications<GradSchoolHistoryEntity, Boolean> booleanFilterSpecifications, Converters converters) {
    super(dateFilterSpecifications, dateTimeFilterSpecifications, integerFilterSpecifications, stringFilterSpecifications, longFilterSpecifications, uuidFilterSpecifications, booleanFilterSpecifications, converters);
  }
}
