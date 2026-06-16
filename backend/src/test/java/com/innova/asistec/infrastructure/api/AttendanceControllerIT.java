package com.innova.asistec.infrastructure.api;

import com.innova.asistec.application.dto.AttendanceRecordDto;
import com.innova.asistec.domain.model.AttendanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class AttendanceControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void attendanceFlow_endToEnd() {
        String baseUrl = "http://localhost:" + port;
        LocalDate today = LocalDate.now();

        ResponseEntity<List<Map<String, Object>>> sectionsResponse = restTemplate.exchange(
                baseUrl + "/api/sections",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(sectionsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sectionsResponse.getBody()).hasSize(4);
        sectionsResponse.getBody().forEach(section ->
                assertThat((List<?>) section.get("students")).isNotEmpty()
        );

        List<AttendanceRecordDto> allPresent = List.of(
                new AttendanceRecordDto("S1", AttendanceStatus.PRESENT),
                new AttendanceRecordDto("S2", AttendanceStatus.PRESENT),
                new AttendanceRecordDto("S3", AttendanceStatus.PRESENT),
                new AttendanceRecordDto("S4", AttendanceStatus.PRESENT),
                new AttendanceRecordDto("S5", AttendanceStatus.PRESENT),
                new AttendanceRecordDto("S6", AttendanceStatus.PRESENT)
        );

        Map<String, Object> saveRequest = Map.of(
                "sectionId", "3A",
                "date", today.toString(),
                "records", allPresent
        );

        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/attendance",
                saveRequest,
                Void.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> duplicateResponse = restTemplate.postForEntity(
                baseUrl + "/api/attendance",
                saveRequest,
                Void.class
        );
        assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<List<Map<String, Object>>> summaryResponse = restTemplate.exchange(
                baseUrl + "/api/attendance/summary?date=" + today,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(summaryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> section3ASummary = summaryResponse.getBody().stream()
                .filter(item -> "3A".equals(item.get("sectionId")))
                .findFirst()
                .orElseThrow();
        assertThat(section3ASummary.get("recorded")).isEqualTo(true);

        ResponseEntity<List<String>> pendingResponse = restTemplate.exchange(
                baseUrl + "/api/attendance/pending?date=" + today,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(pendingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pendingResponse.getBody()).doesNotContain("3A");
    }
}
