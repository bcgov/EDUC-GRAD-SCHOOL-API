package ca.bc.gov.educ.api.school.service;

import ca.bc.gov.educ.api.school.model.dto.District;
import ca.bc.gov.educ.api.school.model.dto.GradCountry;
import ca.bc.gov.educ.api.school.model.dto.GradProvince;
import ca.bc.gov.educ.api.school.model.dto.School;
import ca.bc.gov.educ.api.school.model.entity.SchoolEntity;
import ca.bc.gov.educ.api.school.model.transformer.DistrictTransformer;
import ca.bc.gov.educ.api.school.model.transformer.SchoolTransformer;
import ca.bc.gov.educ.api.school.repository.DistrictRepository;
import ca.bc.gov.educ.api.school.repository.SchoolCriteriaQueryRepository;
import ca.bc.gov.educ.api.school.repository.SchoolRepository;
import ca.bc.gov.educ.api.school.repository.criteria.CriteriaHelper;
import ca.bc.gov.educ.api.school.util.EducSchoolApiConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

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
    private SchoolTransformer schoolTransformer;

    @MockBean
    private DistrictTransformer districtTransformer;

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
        List<School> gradSchoolList = new ArrayList<>();
        School school1 = new School();
        school1.setMinCode("1234567");
        school1.setSchoolName("Test1 School");
        school1.setDistrictName("Test1 District");
        gradSchoolList.add(school1);

        School school2 = new School();
        school2.setMinCode("7654321");
        school2.setSchoolName("Test2 School");
        school2.setDistrictName("Test2 District");
        gradSchoolList.add(school2);

        // District data
        District district = new District();
        district.setDistrictNumber("123");
        district.setDistrictName("Test District");

        when(schoolTransformer.transformToDTO(schoolRepository.findAll())).thenReturn(gradSchoolList);
        when(districtTransformer.transformToDTO(districtRepository.findById(any()))).thenReturn(district);
        List<School> results = schoolService.getSchoolList();
        verify(schoolTransformer).transformToDTO(schoolRepository.findAll());

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(2);
    }

    @Test
    public void testGetSchoolDetails() {
        // School
        School school = new School();
        school.setMinCode("1234567");
        school.setSchoolName("Test School");
        school.setDistrictName("Test District");
        school.setCountryCode("CA");
        school.setProvCode("BC");

        // District
        District district = new District();
        district.setDistrictNumber("123");
        district.setDistrictName("Test District");

        // Country
        GradCountry country = new GradCountry();
        country.setCountryCode("CA");
        country.setCountryName("Canada");

        // Provice
        GradProvince province = new GradProvince();
        province.setCountryCode("CA");
        province.setProvCode("BC");
        province.setProvName("British Columbia");

        when(schoolTransformer.transformToDTO(schoolRepository.findById("1234567"))).thenReturn(school);
        when(districtTransformer.transformToDTO(districtRepository.findById("123"))).thenReturn(district);

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
        School school = new School();
        school.setMinCode("1234567");
        school.setSchoolName("Test School");
        school.setDistrictName("Test District");
        school.setCountryCode("CA");
        school.setProvCode("BC");

        // District
        District district = new District();
        district.setDistrictNumber("123");
        district.setDistrictName("Test District");

        // Country
        GradCountry country = new GradCountry();
        country.setCountryCode("CA");
        country.setCountryName("Canada");

        // Provice
        GradProvince province = new GradProvince();
        province.setCountryCode("CA");
        province.setProvCode("BC");
        province.setProvName("British Columbia");

        CriteriaHelper criteria = new CriteriaHelper();
        schoolService.getSearchCriteria("minCode", "1234567", criteria);
        schoolService.getSearchCriteria("schoolName", "Test School", criteria);

        when(schoolTransformer.transformToDTO(schoolCriteriaQueryRepository.findByCriteria(criteria, SchoolEntity.class))).thenReturn(Arrays.asList(school));
        when(districtTransformer.transformToDTO(districtRepository.findById("123"))).thenReturn(district);

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

        var result = schoolService.getSchoolsByParams("Test School", "1234567", "accessToken");
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMinCode()).isEqualTo("1234567");
        assertThat(result.get(0).getSchoolName()).isEqualTo("Test School");
    }
}
