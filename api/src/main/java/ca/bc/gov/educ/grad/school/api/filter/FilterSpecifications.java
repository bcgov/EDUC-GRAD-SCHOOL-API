package ca.bc.gov.educ.grad.school.api.filter;

import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.function.Function;

/**
 * The type Filter specifications.
 *
 * @param <E> the type parameter
 * @param <T> the type parameter
 */
@Service
public class FilterSpecifications<E, T extends Comparable<T>> {

  private EnumMap<FilterOperation, Function<FilterCriteria<T>, Specification<E>>> map;

  /**
   * Instantiates a new Filter specifications.
   */
  public FilterSpecifications() {
    initSpecifications();
  }

  /**
   * Gets specification.
   *
   * @param operation the operation
   * @return the specification
   */
  public Function<FilterCriteria<T>, Specification<E>> getSpecification(FilterOperation operation) {
    return map.get(operation);
  }

  /**
   * Init specifications.
   */
  @PostConstruct
  public void initSpecifications() {

    map = new EnumMap<>(FilterOperation.class);

    // Equal
    map.put(FilterOperation.EQUAL, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> {
      if (filterCriteria.getFieldName().contains(".")) {
        String[] splits = filterCriteria.getFieldName().split("\\.");
        if(splits.length == 2) {
          return criteriaBuilder.equal(root.join(splits[0]).get(splits[1]), filterCriteria.getConvertedSingleValue());
        } else {
          return criteriaBuilder.equal(root.join(splits[0]).get(splits[1]).get(splits[2]), filterCriteria.getConvertedSingleValue());
        }

      } else if(filterCriteria.getConvertedSingleValue() == null){
        return criteriaBuilder.isNull(root.get(filterCriteria.getFieldName()));
      }
      return criteriaBuilder
              .equal(root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue());
    });

    map.put(FilterOperation.NOT_EQUAL, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> {
      if (filterCriteria.getFieldName().contains(".")) {
        String[] splits = filterCriteria.getFieldName().split("\\.");
        return criteriaBuilder.notEqual(root.join(splits[0]).get(splits[1]), filterCriteria.getConvertedSingleValue());
      } else if(filterCriteria.getConvertedSingleValue() == null){
        return criteriaBuilder.isNotNull(root.get(filterCriteria.getFieldName()));
      }
      return criteriaBuilder
              .notEqual(root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue());
    });

    map.put(FilterOperation.GREATER_THAN,
        filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThan(
            root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.GREATER_THAN_OR_EQUAL_TO,
        filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
            root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.LESS_THAN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .lessThan(root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.LESS_THAN_OR_EQUAL_TO,
        filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(
            root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.IN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> {
      criteriaQuery.distinct(true);
      if (filterCriteria.getFieldName().contains(".")) {
        String[] splits = filterCriteria.getFieldName().split("\\.");
        return root.join(splits[0]).get(splits[1]).in(filterCriteria.getConvertedValues());
      }
      return root.get(filterCriteria.getFieldName()).in(filterCriteria.getConvertedValues());
    });

    map.put(FilterOperation.NOT_IN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .not(root.get(filterCriteria.getFieldName()).in(filterCriteria.getConvertedValues())));

    map.put(FilterOperation.BETWEEN,
        filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.between(
            root.get(filterCriteria.getFieldName()), filterCriteria.getMinValue(),
            filterCriteria.getMaxValue()));

    map.put(FilterOperation.CONTAINS, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .like(root.get(filterCriteria.getFieldName()), "%" + filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.STARTS_WITH, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .like(root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.NOT_STARTS_WITH, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
            .notLike(root.get(filterCriteria.getFieldName()), filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.ENDS_WITH, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .like(root.get(filterCriteria.getFieldName()), "%" + filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.CONTAINS_IGNORE_CASE, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .like(criteriaBuilder.lower(root.get(filterCriteria.getFieldName())), "%" + filterCriteria.getConvertedSingleValue().toString().toLowerCase() + "%"));

    map.put(FilterOperation.STARTS_WITH_IGNORE_CASE, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
        .like(criteriaBuilder.lower(root.get(filterCriteria.getFieldName())), filterCriteria.getConvertedSingleValue().toString().toLowerCase() + "%"));
  }
}
