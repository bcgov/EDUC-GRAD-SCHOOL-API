package ca.bc.gov.educ.grad.school.api.endpoint.v1;

import ca.bc.gov.educ.grad.school.api.constants.v1.URL;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(URL.BASE_URL)
@OpenAPIDefinition(info = @Info(title = "API to GRAD School CRUD.", description = "This API is related to GRAD school data.", version = "1"),
  security = {@SecurityRequirement(name = "OAUTH2", scopes = {"READ_GRAD_SCHOOL_CODES"})})
public interface CodeTableAPIEndpoint {

  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL_CODES')")
  @GetMapping(URL.PROVINCE_CODES)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Transactional(readOnly = true)
  @Tag(name = "GRAD School Codes", description = "Endpoints to get grad school submission mode codes.")
  @Schema(name = "SubmissionModeCode", implementation = SubmissionModeCode.class)
  List<SubmissionModeCode> getSubmissionModeCodes();

}
