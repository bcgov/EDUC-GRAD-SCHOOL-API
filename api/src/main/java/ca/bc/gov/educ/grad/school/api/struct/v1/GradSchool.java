package ca.bc.gov.educ.grad.school.api.struct.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GradSchool extends BaseRequest implements Serializable {
  /**
   * The constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  private UUID gradSchoolID;

  @NotNull
  private UUID schoolID;

  @NotNull
  private String submissionModeCode;

  @NotNull
  private Boolean canIssueTranscripts;

  @NotNull
  private Boolean canIssueCertificates;


}
