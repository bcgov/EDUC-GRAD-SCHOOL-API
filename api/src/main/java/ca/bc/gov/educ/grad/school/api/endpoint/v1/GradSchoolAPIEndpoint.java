package ca.bc.gov.educ.grad.school.api.endpoint.v1;

import ca.bc.gov.educ.grad.school.api.constants.v1.URL;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchool;
import ca.bc.gov.educ.grad.school.api.struct.v1.GradSchoolHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping(URL.BASE_URL)
public interface GradSchoolAPIEndpoint {

  @GetMapping("/{gradSchoolID}")
  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Tag(name = "School Entity", description = "Endpoints for school entity.")
  @Schema(name = "School", implementation = GradSchool.class)
  GradSchool getGradSchool(@PathVariable("gradSchoolID")  UUID gradSchoolID);

  @GetMapping("/{gradSchoolID}/history")
  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Tag(name = "School History Entity", description = "Endpoints for school history entity.")
  @Schema(name = "SchoolHistory", implementation = GradSchoolHistory.class)
  List<GradSchoolHistory> getGradSchoolHistory(@PathVariable("gradSchoolID")  UUID gradSchoolID);


  @GetMapping("/search/{schoolID}")
  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Tag(name = "School Entity", description = "Endpoints for school entity.")
  @Schema(name = "School", implementation = GradSchool.class)
  GradSchool getGradSchoolBySchoolID(@PathVariable("schoolID")  UUID schoolID);

  @GetMapping("/search/{schoolID}/history")
  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Tag(name = "School History Entity", description = "Endpoints for school history entity.")
  @Schema(name = "SchoolHistory", implementation = GradSchoolHistory.class)
  List<GradSchoolHistory> getGradSchoolHistoryBySchoolID(@PathVariable("schoolID")  UUID schoolID);

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_WRITE_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "CREATED"), @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
  @Tag(name = "School Entity", description = "Endpoints for school entity.")
  @Schema(name = "School", implementation = GradSchool.class)
  @ResponseStatus(CREATED)
  GradSchool createGradSchool(@Validated @RequestBody GradSchool gradSchool) throws JsonProcessingException;

  @PutMapping("/{gradSchoolID}")
  @PreAuthorize("hasAuthority('SCOPE_WRITE_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "400", description = "BAD REQUEST"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Tag(name = "School Entity", description = "Endpoints for school entity.")
  @Schema(name = "School", implementation = GradSchool.class)
  GradSchool updateGradSchool(@PathVariable UUID gradSchoolID, @Validated @RequestBody GradSchool gradSchool) throws JsonProcessingException;

  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
  @Transactional(readOnly = true)
  @Tag(name = "GRAD School Entity", description = "Endpoints for grad school entity.")
  @Schema(name = "GradSchool", implementation = GradSchool.class)
  List<GradSchool> getAllGradSchools();

  @GetMapping("/history/paginated")
  @Async
  @PreAuthorize("hasAuthority('SCOPE_READ_GRAD_SCHOOL')")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  @Transactional(readOnly = true)
  @Tag(name = "Grad School History Entity", description = "Endpoints for grad school history entity.")
  CompletableFuture<Page<GradSchoolHistory>> schoolHistoryFindAll(@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                              @RequestParam(name = "sort", defaultValue = "") String sortCriteriaJson,
                                                              @RequestParam(name = "searchCriteriaList", required = false) String searchCriteriaListJson);
  
}
