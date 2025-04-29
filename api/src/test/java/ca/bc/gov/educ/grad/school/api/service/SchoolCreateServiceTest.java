package ca.bc.gov.educ.grad.school.api.service;

import ca.bc.gov.educ.grad.school.api.GradSchoolApiResourceApplication;
import ca.bc.gov.educ.grad.school.api.model.v1.GradSchoolEventEntity;
import ca.bc.gov.educ.grad.school.api.repository.v1.GradSchoolRepository;
import ca.bc.gov.educ.grad.school.api.rest.RestUtils;
import ca.bc.gov.educ.grad.school.api.service.v1.SchoolCreateService;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.School;
import ca.bc.gov.educ.grad.school.api.struct.v1.external.institute.SchoolGrade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static ca.bc.gov.educ.grad.school.api.constants.v1.EventStatus.DB_COMMITTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {GradSchoolApiResourceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class SchoolCreateServiceTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    RestUtils restUtils;
    @Autowired
    SchoolCreateService schoolCreateService;
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
    void testCreateSchool_success() {
        var school = new School();
        school.setSchoolId(UUID.randomUUID().toString());
        SchoolGrade grade1 = new SchoolGrade();
        grade1.setSchoolGradeCode("GRADE01");
        SchoolGrade grade2 = new SchoolGrade();
        grade2.setSchoolGradeCode("GRADE02");
        school.setSchoolId(UUID.randomUUID().toString());
        school.setGrades(Arrays.asList(grade2, grade1));
        school.setSchoolCategoryCode("ABC");

        var gradSchoolEventEntity = new GradSchoolEventEntity();
        gradSchoolEventEntity.setEventStatus(DB_COMMITTED.toString());
        gradSchoolEventEntity.setEventId(UUID.randomUUID());

        schoolCreateService.processEvent(school, gradSchoolEventEntity);

        var allSchools = gradSchoolRepository.findAll();
        assertEquals(false, allSchools.isEmpty());
        assertEquals(1, allSchools.size());
    }

}
