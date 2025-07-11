package ca.bc.gov.educ.grad.school.api.struct.v1.external.institute;

import ca.bc.gov.educ.grad.school.api.struct.v1.BaseRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolGradeSchoolHistory extends BaseRequest implements Serializable {


    private static final long serialVersionUID = -8816066998462169068L;

    private String schoolGradeHistoryId;

    private String schoolHistoryId;

    @Size(max = 10)
    @NotNull(message = "school Grade Code cannot be null")
    private String schoolGradeCode;

}
