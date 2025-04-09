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
public class GradSchoolHistory extends BaseRequest implements Serializable {

  private UUID gradSchoolHistoryID;

  @NotNull
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
