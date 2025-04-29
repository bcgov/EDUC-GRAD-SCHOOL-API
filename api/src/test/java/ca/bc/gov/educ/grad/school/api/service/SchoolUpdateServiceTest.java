package ca.bc.gov.educ.grad.school.api.service;

import ca.bc.gov.educ.grad.school.api.GradSchoolApiResourceApplication;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEntity;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.rest.RestUtils;
import ca.bc.gov.educ.grad.school.api.service.v1.SchoolUpdateService;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus.DB_COMMITTED;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {GradSchoolApiResourceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class SchoolUpdateServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    RestUtils restUtils;
    @Autowired
    SchoolUpdateService schoolUpdateService;
    @Autowired
    GradSchoolRepository gradSchoolRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void after() {
        this.gradSchoolRepository.deleteAll();
    }

    @Test
    void testUpdateSchoolWithSameGrades_success() {
        var school = new School();
        school.setSchoolId(UUID.randomUUID().toString());
        SchoolGrade grade1 = new SchoolGrade();
        grade1.setSchoolGradeCode("GRADE01");
        SchoolGrade grade2 = new SchoolGrade();
        grade2.setSchoolGradeCode("GRADE02");
        school.setSchoolId(UUID.randomUUID().toString());
        school.setGrades(Arrays.asList(grade2, grade1));
        school.setSchoolCategoryCode("ABC");

        GradSchoolEntity gradSchool = new GradSchoolEntity();
        gradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
        gradSchool.setSubmissionModeCode("REPLACE");
        gradSchool.setCanIssueTranscripts("N");
        gradSchool.setCanIssueCertificates("N");
        gradSchool.setCreateUser("ABC");
        gradSchool.setUpdateUser("ABC");
        gradSchool.setUpdateDate(LocalDateTime.now());
        gradSchool.setCreateDate(LocalDateTime.now());

        gradSchoolRepository.save(gradSchool);

        var gradSchoolEventEntity = new GradSchoolEventEntity();
        gradSchoolEventEntity.setEventStatus(DB_COMMITTED.toString());
        gradSchoolEventEntity.setEventId(UUID.randomUUID());

        SchoolHistory hist1 = new SchoolHistory();

        SchoolGradeSchoolHistory gradeSH1 = new SchoolGradeSchoolHistory();
        gradeSH1.setSchoolGradeCode("GRADE01");
        SchoolGradeSchoolHistory gradeSH2 = new SchoolGradeSchoolHistory();
        gradeSH2.setSchoolGradeCode("GRADE02");
        hist1.setSchoolId(UUID.randomUUID().toString());
        hist1.setSchoolGrades(Arrays.asList(gradeSH1, gradeSH2));

        SchoolHistory hist2 = new SchoolHistory();

        SchoolGradeSchoolHistory gradeSH3 = new SchoolGradeSchoolHistory();
        gradeSH3.setSchoolGradeCode("GRADE01");
        SchoolGradeSchoolHistory gradeSH4 = new SchoolGradeSchoolHistory();
        gradeSH4.setSchoolGradeCode("GRADE02");
        hist2.setSchoolId(UUID.randomUUID().toString());
        hist2.setSchoolGrades(Arrays.asList(gradeSH3, gradeSH4));

        var data = Arrays.asList(hist1, hist2);

        Page<SchoolHistory> page = new PageImpl<>(data);

        when(this.restUtils.getSchoolHistoryPaginatedFromInstituteApi(anyString())).thenReturn(page);

        schoolUpdateService.processEvent(school, gradSchoolEventEntity);

        var allSchools = gradSchoolRepository.findAll();
        assertEquals(false, allSchools.isEmpty());
        assertEquals(1, allSchools.size());
        assertEquals(gradSchool.getUpdateDate().toString().substring(0,19), allSchools.get(0).getUpdateDate().toString().substring(0,19));
    }

    @Test
    void testUpdateSchoolWithDifferentGrades_success() {
        var school = new School();
        school.setSchoolId(UUID.randomUUID().toString());
        SchoolGrade grade1 = new SchoolGrade();
        grade1.setSchoolGradeCode("GRADE01");
        SchoolGrade grade3 = new SchoolGrade();
        grade3.setSchoolGradeCode("GRADE03");
        school.setSchoolId(UUID.randomUUID().toString());
        school.setGrades(Arrays.asList(grade3, grade1));
        school.setSchoolCategoryCode("ABC");

        GradSchoolEntity gradSchool = new GradSchoolEntity();
        gradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
        gradSchool.setSubmissionModeCode("REPLACE");
        gradSchool.setCanIssueTranscripts("N");
        gradSchool.setCanIssueCertificates("N");
        gradSchool.setCreateUser("ABC");
        gradSchool.setUpdateUser("ABC");
        gradSchool.setUpdateDate(LocalDateTime.now().minusDays(1));
        gradSchool.setCreateDate(LocalDateTime.now().minusDays(1));

        gradSchoolRepository.save(gradSchool);

        var gradSchoolEventEntity = new GradSchoolEventEntity();
        gradSchoolEventEntity.setEventStatus(DB_COMMITTED.toString());
        gradSchoolEventEntity.setEventId(UUID.randomUUID());

        SchoolHistory hist1 = new SchoolHistory();

        SchoolGradeSchoolHistory gradeSH1 = new SchoolGradeSchoolHistory();
        gradeSH1.setSchoolGradeCode("GRADE01");
        SchoolGradeSchoolHistory gradeSH2 = new SchoolGradeSchoolHistory();
        gradeSH2.setSchoolGradeCode("GRADE02");
        hist1.setSchoolId(UUID.randomUUID().toString());
        hist1.setSchoolGrades(Arrays.asList(gradeSH1, gradeSH2));

        SchoolHistory hist2 = new SchoolHistory();

        SchoolGradeSchoolHistory gradeSH3 = new SchoolGradeSchoolHistory();
        gradeSH3.setSchoolGradeCode("GRADE01");
        SchoolGradeSchoolHistory gradeSH4 = new SchoolGradeSchoolHistory();
        gradeSH4.setSchoolGradeCode("GRADE02");
        hist2.setSchoolId(UUID.randomUUID().toString());
        hist2.setSchoolGrades(Arrays.asList(gradeSH3, gradeSH4));

        var data = Arrays.asList(hist1, hist2);

        Page<SchoolHistory> page = new PageImpl<>(data);

        when(this.restUtils.getSchoolHistoryPaginatedFromInstituteApi(anyString())).thenReturn(page);

        schoolUpdateService.processEvent(school, gradSchoolEventEntity);

        var allSchools = gradSchoolRepository.findAll();
        assertEquals(false, allSchools.isEmpty());
        assertEquals(1, allSchools.size());
        assertNotEquals(gradSchool.getUpdateDate().toString().substring(0,19), allSchools.get(0).getUpdateDate().toString().substring(0,19));
    }

    @Test
    void testUpdateSchoolWithDifferentGrades_FlagsUpdated_success() {
        var school = new School();
        school.setSchoolId(UUID.randomUUID().toString());
        SchoolGrade grade10 = new SchoolGrade();
        grade10.setSchoolGradeCode("GRADE10");
        SchoolGrade grade3 = new SchoolGrade();
        grade3.setSchoolGradeCode("GRADE03");
        school.setSchoolId(UUID.randomUUID().toString());
        school.setGrades(Arrays.asList(grade3, grade10));
        school.setSchoolCategoryCode("PUBLIC");

        IndependentSchoolFundingGroup group1 = new IndependentSchoolFundingGroup();
        group1.setSchoolGradeCode("GRADE10");
        group1.setSchoolFundingGroupCode("GROUP1");
        school.setSchoolFundingGroups(Arrays.asList(group1));

        GradSchoolEntity gradSchool = new GradSchoolEntity();
        gradSchool.setSchoolID(UUID.fromString(school.getSchoolId()));
        gradSchool.setSubmissionModeCode("REPLACE");
        gradSchool.setCanIssueTranscripts("N");
        gradSchool.setCanIssueCertificates("N");
        gradSchool.setCreateUser("ABC");
        gradSchool.setUpdateUser("ABC");
        gradSchool.setUpdateDate(LocalDateTime.now().minusDays(1));
        gradSchool.setCreateDate(LocalDateTime.now().minusDays(1));

        gradSchoolRepository.save(gradSchool);

        var gradSchoolEventEntity = new GradSchoolEventEntity();
        gradSchoolEventEntity.setEventStatus(DB_COMMITTED.toString());
        gradSchoolEventEntity.setEventId(UUID.randomUUID());

        SchoolHistory hist1 = new SchoolHistory();

        SchoolGradeSchoolHistory gradeSH1 = new SchoolGradeSchoolHistory();
        gradeSH1.setSchoolGradeCode("GRADE01");
        SchoolGradeSchoolHistory gradeSH2 = new SchoolGradeSchoolHistory();
        gradeSH2.setSchoolGradeCode("GRADE02");
        hist1.setSchoolId(UUID.randomUUID().toString());
        hist1.setSchoolGrades(Arrays.asList(gradeSH1, gradeSH2));

        SchoolHistory hist2 = new SchoolHistory();

        SchoolGradeSchoolHistory gradeSH3 = new SchoolGradeSchoolHistory();
        gradeSH3.setSchoolGradeCode("GRADE01");
        SchoolGradeSchoolHistory gradeSH4 = new SchoolGradeSchoolHistory();
        gradeSH4.setSchoolGradeCode("GRADE02");
        hist2.setSchoolId(UUID.randomUUID().toString());
        hist2.setSchoolGrades(Arrays.asList(gradeSH3, gradeSH4));

        var data = Arrays.asList(hist1, hist2);

        Page<SchoolHistory> page = new PageImpl<>(data);

        when(this.restUtils.getSchoolHistoryPaginatedFromInstituteApi(anyString())).thenReturn(page);

        schoolUpdateService.processEvent(school, gradSchoolEventEntity);

        var allSchools = gradSchoolRepository.findAll();
        assertEquals(false, allSchools.isEmpty());
        assertEquals(1, allSchools.size());
        assertNotEquals(gradSchool.getUpdateDate().toString().substring(0,19), allSchools.get(0).getUpdateDate().toString().substring(0,19));
    }

}
