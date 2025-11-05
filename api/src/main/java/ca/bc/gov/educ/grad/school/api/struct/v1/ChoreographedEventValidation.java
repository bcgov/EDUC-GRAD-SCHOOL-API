package ca.bc.gov.educ.grad.school.api.struct.v1;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ChoreographedEventValidation {
  /**
   * The Event id.
   */
  UUID eventID; // the primary key of student event table.
  /**
   * The Event type.
   */
  String eventType;
  /**
   * The Event outcome.
   */
  String eventOutcome;
  /**
   * The Event payload.
   */
  String eventPayload;
  /**
   * The Create user.
   */
  String createUser;
  /**
   * The Update user.
   */
  String updateUser;
}
