package ca.bc.gov.educ.grad.school.api.model.v1;

import ca.bc.gov.educ.grad.school.api.util.UpperCase;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "GRAD_SCHOOL")
public class GradSchoolEntity {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator", parameters = {
    @org.hibernate.annotations.Parameter(name = "uuid_gen_strategy_class", value = "org.hibernate.id.uuid.CustomVersionOneStrategy")})
  @Column(name = "GRAD_SCHOOL_ID", unique = true, updatable = false, columnDefinition = "BINARY(16)")
  private UUID gradSchoolID;

  @Basic
  @Column(name = "SCHOOL_ID", columnDefinition = "BINARY(16)")
  private UUID schoolID;

  @Basic
  @Column(name = "SUBMISSION_MODE_CODE")
  private String submissionModeCode;

  @Column(name = "CAN_ISSUE_TRANSCRIPTS")
  @UpperCase
  private Boolean canIssueTranscripts;

  @Column(name = "CAN_ISSUE_CERTIFICATES")
  @UpperCase
  private Boolean canIssueCertificates;

  @Column(name = "CREATE_USER", updatable = false)
  String createUser;

  @PastOrPresent
  @Column(name = "CREATE_DATE", updatable = false)
  LocalDateTime createDate;

  @Column(name = "update_user")
  String updateUser;

  @PastOrPresent
  @Column(name = "update_date")
  LocalDateTime updateDate;

}
