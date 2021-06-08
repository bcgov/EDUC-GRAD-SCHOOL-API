package ca.bc.gov.educ.api.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.school.model.dto.GradCountry;
import ca.bc.gov.educ.api.school.model.dto.GradProvince;
import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.entity.DistrictEntity;
import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;
import ca.bc.gov.educ.api.school.repository.DistrictRepository;
import ca.bc.gov.educ.api.school.repository.SchoolCriteriaQueryRepository;
import ca.bc.gov.educ.api.school.repository.SchoolRepository;
import ca.bc.gov.educ.api.school.util.EducSchoolApiConstants;
import ca.bc.gov.educ.api.school.util.criteria.CriteriaHelper;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SchoolServiceTest {

    @Autowired
    EducSchoolApiConstants educSchoolApiConstants;

    @Autowired
    private SchoolService schoolService;

    @MockBean
    private SchoolRepository schoolRepository;

    @MockBean
    private DistrictRepository districtRepository;

    @MockBean
    private SchoolCriteriaQueryRepository schoolCriteriaQueryRepository;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.RequestBodySpec requestBodyMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;

    @Before
    public void setUp() {
        openMocks(this);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetSchoolList() {
        // School data
        final List<SchoolEntity> gradSchoolList = new ArrayList<>();
        final SchoolEntity school1 = new SchoolEntity();
        school1.setMinCode("1234567");
        school1.setSchoolName("Test1 School");
        gradSchoolList.add(school1);

        final SchoolEntity school2 = new SchoolEntity();
        school2.setMinCode("7654321");
        school2.setSchoolName("Test2 School");
        gradSchoolList.add(school2);

        // District data
        final DistrictEntity district = new DistrictEntity();
        district.setDistrictNumber("123");
        district.setDistrictName("Test District");

        when(schoolRepository.findAll()).thenReturn(gradSchoolList);
        when(districtRepository.findById(eq("123"))).thenReturn(Optional.of(district));
        List<School> results = schoolService.getSchoolList();

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
        School responseSchool = results.get(0);
        assertThat(responseSchool.getSchoolName()).isEqualTo(school1.getSchoolName());
        assertThat(responseSchool.getDistrictName()).isEqualTo(district.getDistrictName());
    }

    @Test
    public void testGetSchoolDetails() {
        // School
        final SchoolEntity school = new SchoolEntity();
        school.setMinCode("1234567");
        school.setSchoolName("Test School");
        school.setCountryCode("CA");
        school.setProvCode("BC");

        // District
        final DistrictEntity district = new DistrictEntity();
        district.setDistrictNumber("123");
        district.setDistrictName("Test District");
        district.setCountryCode("CA");
        district.setProvCode("BC");

        // Country
        final GradCountry country = new GradCountry();
        country.setCountryCode("CA");
        country.setCountryName("Canada");

        // Province
        final GradProvince province = new GradProvince();
        province.setCountryCode("CA");
        province.setProvCode("BC");
        province.setProvName("British Columbia");

        when(schoolRepository.findById(eq("1234567"))).thenReturn(Optional.of(school));
        when(districtRepository.findById(eq("123"))).thenReturn(Optional.of(district));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(eq(String.format(educSchoolApiConstants.getCountryByCountryCodeUrl(), country.getCountryCode())))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradCountry.class)).thenReturn(Mono.just(country));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(eq(String.format(educSchoolApiConstants.getProvinceByProvinceCodeUrl(), province.getProvCode())))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradProvince.class)).thenReturn(Mono.just(province));

        var result = schoolService.getSchoolDetails("1234567", "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getMinCode()).isEqualTo("1234567");
        assertThat(result.getSchoolName()).isEqualTo("Test School");
    }

    @Test
    public void testGetSchoolsByParams() {
        // School
        final SchoolEntity school = new SchoolEntity();
        school.setMinCode("1234567");
        school.setSchoolName("Test School");
        school.setCountryCode("CA");
        school.setProvCode("BC");

        // District
        final DistrictEntity district = new DistrictEntity();
        district.setDistrictNumber("123");
        district.setDistrictName("Test District");
        district.setCountryCode("CA");
        district.setProvCode("BC");

        // Country
        final GradCountry country = new GradCountry();
        country.setCountryCode("CA");
        country.setCountryName("Canada");

        // Province
        final GradProvince province = new GradProvince();
        province.setCountryCode("CA");
        province.setProvCode("BC");
        province.setProvName("British Columbia");

        when(schoolCriteriaQueryRepository.findByCriteria(any(CriteriaHelper.class), eq(SchoolEntity.class))).thenReturn(Arrays.asList(school));
        when(districtRepository.findById(eq("123"))).thenReturn(Optional.of(district));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(eq(String.format(educSchoolApiConstants.getCountryByCountryCodeUrl(), country.getCountryCode())))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradCountry.class)).thenReturn(Mono.just(country));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(eq(String.format(educSchoolApiConstants.getProvinceByProvinceCodeUrl(), province.getProvCode())))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradProvince.class)).thenReturn(Mono.just(province));

        var result = schoolService.getSchoolsByParams("Test School", "1234567");
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMinCode()).isEqualTo("1234567");
        assertThat(result.get(0).getSchoolName()).isEqualTo("Test School");
    }
}
