package com.innova.asistec.infrastructure.api;

import com.innova.asistec.application.dto.SectionAttendanceRecordResponse;
import com.innova.asistec.application.dto.SaveAttendanceRequest;
import com.innova.asistec.application.service.GetSectionAttendanceUseCase;
import com.innova.asistec.application.service.SaveAttendanceUseCase;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final SaveAttendanceUseCase saveAttendanceUseCase;
    private final GetSectionAttendanceUseCase getSectionAttendanceUseCase;

    public AttendanceController(SaveAttendanceUseCase saveAttendanceUseCase,
                                GetSectionAttendanceUseCase getSectionAttendanceUseCase) {
        this.saveAttendanceUseCase = saveAttendanceUseCase;
        this.getSectionAttendanceUseCase = getSectionAttendanceUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> saveAttendance(@Valid @RequestBody SaveAttendanceRequest request) {
        saveAttendanceUseCase.saveAttendance(
                request.sectionId(),
                request.date(),
                request.records()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<SectionAttendanceRecordResponse>> getSectionAttendance(
            @RequestParam String sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(getSectionAttendanceUseCase.getSectionAttendance(sectionId, date));
    }
}
