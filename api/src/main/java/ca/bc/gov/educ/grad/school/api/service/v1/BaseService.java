package ca.bc.gov.educ.grad.school.api.service.v1;


import ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolEventRepository;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * The type Base service.
 *
 * @param <T> the type parameter
 */
@Slf4j
public abstract class BaseService<T> implements EventService<T> {
    private final GradSchoolEventRepository gradSchoolEventRepository;

    protected BaseService(GradSchoolEventRepository gradSchoolEventRepository) {
        this.gradSchoolEventRepository = gradSchoolEventRepository;
    }

    protected void setTranscriptAndCertificateFlags(School school, GradSchoolEntity gradSchool) {
        Set<String> gradesArray = new HashSet<>(Arrays.asList("GRADE10", "GRADE11", "GRADE12","SECUNGR"));
        Set<String> groupsArray = new HashSet<>(Arrays.asList("GROUP1","GROUP2","GROUP4"));
        Set<String> summerShortPRPArray = new HashSet<>(Arrays.asList("SHORT_PRP","SUMMER"));
        var canIssueTranscripts = false;
        var canIssueCertificates = false;

        var grade10toSUFundingCodes = school.getSchoolFundingGroups().stream().filter(group -> gradesArray.contains(group.getSchoolGradeCode()));
        var schoolHas10toSUGrades = school.getGrades().stream().anyMatch(grade -> gradesArray.contains(grade.getSchoolGradeCode()));
        var hasGroup1or2or4 = grade10toSUFundingCodes.anyMatch(group -> groupsArray.contains(group.getSchoolFundingGroupCode()));

        switch(school.getSchoolCategoryCode()) {
            case "PUBLIC":
                if(summerShortPRPArray.stream().noneMatch(prp -> prp.equalsIgnoreCase(school.getFacilityTypeCode())) && schoolHas10toSUGrades){
                    canIssueTranscripts = true;
                    canIssueCertificates = true;
                }
                break;
            case "INDEPEND":
            case "INDP_FNS":
                if(schoolHas10toSUGrades && hasGroup1or2or4){
                    canIssueTranscripts = true;
                    canIssueCertificates = true;
                }
                break;
            case "YUKON":
                if(schoolHas10toSUGrades){
                    canIssueTranscripts = true;
                }
                break;
            case "OFFSHORE":
                if(schoolHas10toSUGrades){
                    canIssueTranscripts = true;
                    canIssueCertificates = true;
                }
                break;
        }

        gradSchool.setCanIssueTranscripts(canIssueTranscripts ? "Y" : "N");
        gradSchool.setCanIssueCertificates(canIssueCertificates ? "Y" : "N");
    }

    protected void updateEvent(final GradSchoolEventEntity event) {
        this.gradSchoolEventRepository.findByEventId(event.getEventId()).ifPresent(existingEvent -> {
            existingEvent.setEventStatus(EventStatus.PROCESSED.toString());
            existingEvent.setUpdateDate(LocalDateTime.now());
            this.gradSchoolEventRepository.save(existingEvent);
        });
    }

}
