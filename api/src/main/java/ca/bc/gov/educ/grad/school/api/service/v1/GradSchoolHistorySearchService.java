package ca.bc.gov.educ.grad.school.api.service.v1;

import ca.bc.gov.educ.grad.school.api.exception.InvalidParameterException;
import ca.bc.gov.educ.grad.school.api.exception.SchoolAPIRuntimeException;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolHistoryEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolHistoryRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.Condition;
import ca.bc.gov.educ.grad.school.api.struct.v1.Search;
import ca.bc.gov.educ.grad.school.api.struct.v1.SearchCriteria;
import ca.bc.gov.educ.grad.school.api.struct.v1.ValueType;
import ca.bc.gov.educ.grad.school.api.util.RequestUtil;
import ca.bc.gov.educ.grad.school.api.util.TransformUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ca.bc.gov.educ.grad.school.api.filter.FilterOperation;
import ca.bc.gov.educ.grad.school.api.filter.GradSchoolHistoryFilterSpecs;
import org.apache.commons.lang3.StringUtils;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * The type School History search service.
 */
@Service
public class GradSchoolHistorySearchService {
  private final GradSchoolHistoryFilterSpecs schoolHistoryFilterSpecs;

  private final GradSchoolHistoryRepository schoolHistoryRepository;

  private final Executor paginatedQueryExecutor = new EnhancedQueueExecutor.Builder()
    .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("async-pagination-query-executor-%d").build())
    .setCorePoolSize(2).setMaximumPoolSize(10).setKeepAliveTime(Duration.ofSeconds(60)).build();

  /**
   * Instantiates a new School History search service.
   *
   * @param schoolHistoryFilterSpecs the school filter specs
   * @param schoolHistoryRepository the school history repository
   */
  public GradSchoolHistorySearchService(GradSchoolHistoryFilterSpecs schoolHistoryFilterSpecs, GradSchoolHistoryRepository schoolHistoryRepository) {
    this.schoolHistoryFilterSpecs = schoolHistoryFilterSpecs;
    this.schoolHistoryRepository = schoolHistoryRepository;
  }

  /**
   * Gets specifications.
   *
   * @param schoolHistorySpecs school history specs
   * @param i            the
   * @param search       the search
   * @return the specifications
   */
  public Specification<GradSchoolHistoryEntity> getSpecifications(Specification<GradSchoolHistoryEntity> schoolHistorySpecs, int i, Search search) {
    if (i == 0) {
      schoolHistorySpecs = getSchoolHistoryEntitySpecification(search.getSearchCriteriaList());
    } else {
      if (search.getCondition() == Condition.AND) {
        schoolHistorySpecs = schoolHistorySpecs.and(getSchoolHistoryEntitySpecification(search.getSearchCriteriaList()));
      } else {
        schoolHistorySpecs = schoolHistorySpecs.or(getSchoolHistoryEntitySpecification(search.getSearchCriteriaList()));
      }
    }
    return schoolHistorySpecs;
  }

  private Specification<GradSchoolHistoryEntity> getSchoolHistoryEntitySpecification(List<SearchCriteria> criteriaList) {
    Specification<GradSchoolHistoryEntity> schoolHistorySpecs = null;
    if (!criteriaList.isEmpty()) {
      int i = 0;
      for (SearchCriteria criteria : criteriaList) {
        if (criteria.getKey() != null && criteria.getOperation() != null && criteria.getValueType() != null) {
          var criteriaValue = criteria.getValue();
          if(StringUtils.isNotBlank(criteria.getValue()) && TransformUtil.isUppercaseField(GradSchoolHistoryEntity.class, criteria.getKey())) {
            criteriaValue = criteriaValue.toUpperCase();
          }
          Specification<GradSchoolHistoryEntity> typeSpecification = getTypeSpecification(criteria.getKey(), criteria.getOperation(), criteriaValue, criteria.getValueType());
          schoolHistorySpecs = getSpecificationPerGroup(schoolHistorySpecs, i, criteria, typeSpecification);
          i++;
        } else {
          throw new InvalidParameterException("Search Criteria can not contain null values for key, value and operation type");
        }
      }
    }
    return schoolHistorySpecs;
  }

  /**
   * Gets specification per group.
   *
   * @param schoolHistoryEntitySpecification the pen request batch entity specification
   * @param i                          the
   * @param criteria                   the criteria
   * @param typeSpecification          the type specification
   * @return the specification per group
   */
  private Specification<GradSchoolHistoryEntity> getSpecificationPerGroup(Specification<GradSchoolHistoryEntity> schoolHistoryEntitySpecification, int i, SearchCriteria criteria, Specification<GradSchoolHistoryEntity> typeSpecification) {
    if (i == 0) {
      schoolHistoryEntitySpecification = Specification.where(typeSpecification);
    } else {
      if (criteria.getCondition() == Condition.AND) {
        schoolHistoryEntitySpecification = schoolHistoryEntitySpecification.and(typeSpecification);
      } else {
        schoolHistoryEntitySpecification = schoolHistoryEntitySpecification.or(typeSpecification);
      }
    }
    return schoolHistoryEntitySpecification;
  }

  private Specification<GradSchoolHistoryEntity> getTypeSpecification(String key, FilterOperation filterOperation, String value, ValueType valueType) {
    Specification<GradSchoolHistoryEntity> schoolHistoryEntitySpecification = null;
    switch (valueType) {
      case STRING:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getStringTypeSpecification(key, value, filterOperation);
        break;
      case DATE_TIME:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getDateTimeTypeSpecification(key, value, filterOperation);
        break;
      case LONG:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getLongTypeSpecification(key, value, filterOperation);
        break;
      case INTEGER:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getIntegerTypeSpecification(key, value, filterOperation);
        break;
      case DATE:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getDateTypeSpecification(key, value, filterOperation);
        break;
      case UUID:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getUUIDTypeSpecification(key, value, filterOperation);
        break;
      case BOOLEAN:
        schoolHistoryEntitySpecification = schoolHistoryFilterSpecs.getBooleanTypeSpecification(key, value, filterOperation);
        break;
      default:
        break;
    }
    return schoolHistoryEntitySpecification;
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public CompletableFuture<Page<GradSchoolHistoryEntity>> findAll(Specification<GradSchoolHistoryEntity> schoolHistorySpecs, final Integer pageNumber, final Integer pageSize, final List<Sort.Order> sorts) {
    return CompletableFuture.supplyAsync(() -> {
      Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sorts));
      try {
        return this.schoolHistoryRepository.findAll(schoolHistorySpecs, paging);
      } catch (final Exception ex) {
        throw new CompletionException(ex);
      }
    }, paginatedQueryExecutor);

  }

  /**
   * Sets specification and sort criteria.
   *
   * @param sortCriteriaJson       the sort criteria json
   * @param searchCriteriaListJson the search criteria list json
   * @param objectMapper           the object mapper
   * @param sorts                  the sorts
   * @return the specification and sort criteria
   */
  public Specification<GradSchoolHistoryEntity> setSpecificationAndSortCriteria(String sortCriteriaJson, String searchCriteriaListJson, ObjectMapper objectMapper, List<Sort.Order> sorts) {
    Specification<GradSchoolHistoryEntity> schoolHistorySpecs = null;
    try {
      RequestUtil.getSortCriteria(sortCriteriaJson, objectMapper, sorts);
      if (StringUtils.isNotBlank(searchCriteriaListJson)) {
        List<Search> searches = objectMapper.readValue(searchCriteriaListJson, new TypeReference<>() {
        });
        int i = 0;
        for (var search : searches) {
          schoolHistorySpecs = getSpecifications(schoolHistorySpecs, i, search);
          i++;
        }
      }
    } catch (JsonProcessingException e) {
      throw new SchoolAPIRuntimeException(e.getMessage());
    }
    return schoolHistorySpecs;
  }
}
