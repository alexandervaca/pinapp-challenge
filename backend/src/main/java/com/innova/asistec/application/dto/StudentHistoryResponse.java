package com.innova.asistec.application.dto;

import com.innova.asistec.domain.model.AttendanceStatus;

import java.time.LocalDate;

public record StudentHistoryResponse(
        LocalDate date,
        String sectionId,
        AttendanceStatus status
) {
}
