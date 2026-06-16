package com.innova.asistec.infrastructure.api;

import com.innova.asistec.application.dto.DailySummaryResponse;
import com.innova.asistec.application.dto.SectionResponse;
import com.innova.asistec.application.dto.StudentHistoryResponse;
import com.innova.asistec.application.dto.StudentSummaryDto;
import com.innova.asistec.application.service.GetAttendanceSummaryUseCase;
import com.innova.asistec.application.service.GetPendingSectionsUseCase;
import com.innova.asistec.application.service.GetStudentHistoryUseCase;
import com.innova.asistec.domain.model.Section;
import com.innova.asistec.domain.model.Student;
import com.innova.asistec.domain.repository.SectionRepository;
import com.innova.asistec.domain.repository.StudentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final GetAttendanceSummaryUseCase getAttendanceSummaryUseCase;
    private final GetStudentHistoryUseCase getStudentHistoryUseCase;
    private final GetPendingSectionsUseCase getPendingSectionsUseCase;
    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;

    public ReportController(GetAttendanceSummaryUseCase getAttendanceSummaryUseCase,
                            GetStudentHistoryUseCase getStudentHistoryUseCase,
                            GetPendingSectionsUseCase getPendingSectionsUseCase,
                            SectionRepository sectionRepository,
                            StudentRepository studentRepository) {
        this.getAttendanceSummaryUseCase = getAttendanceSummaryUseCase;
        this.getStudentHistoryUseCase = getStudentHistoryUseCase;
        this.getPendingSectionsUseCase = getPendingSectionsUseCase;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/attendance/summary")
    public List<DailySummaryResponse> getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return getAttendanceSummaryUseCase.getSummary(date);
    }

    @GetMapping("/attendance/history")
    public List<StudentHistoryResponse> getHistory(
            @RequestParam String studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date range: from must be before or equal to to");
        }
        return getStudentHistoryUseCase.getHistory(studentId, from, to);
    }

    @GetMapping("/attendance/pending")
    public List<String> getPendingSections(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return getPendingSectionsUseCase.getPendingSections(date);
    }

    @GetMapping("/sections")
    public List<SectionResponse> getSections() {
        return sectionRepository.findAll().stream()
                .sorted(Comparator.comparing(Section::getId))
                .map(this::toSectionResponse)
                .toList();
    }

    private SectionResponse toSectionResponse(Section section) {
        List<StudentSummaryDto> students = studentRepository.findBySectionId(section.getId()).stream()
                .sorted(Comparator.comparing(Student::getId))
                .map(student -> new StudentSummaryDto(student.getId(), student.getFullName()))
                .toList();

        return new SectionResponse(
                section.getId(),
                section.getName(),
                section.getGrade().getId(),
                section.getGrade().getName(),
                students
        );
    }
}
