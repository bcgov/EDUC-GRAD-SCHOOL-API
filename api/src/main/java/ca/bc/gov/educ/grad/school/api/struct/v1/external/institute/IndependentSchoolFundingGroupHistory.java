package ca.bc.gov.educ.grad.school.api.struct.v1.external.institute;

import ca.bc.gov.educ.grad.school.api.struct.v1.BaseRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuppressWarnings("squid:S1700")
public class IndependentSchoolFundingGroupHistory extends BaseRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private String schoolFundingGroupHistoryID;

  @NotNull(message = "schoolID cannot be null")
  private String schoolID;

  @NotNull(message = "schoolGradeCode cannot be null")
  private String schoolGradeCode;

  @NotNull(message = "schoolFundingGroupCode cannot be null")
  private String schoolFundingGroupCode;
}
