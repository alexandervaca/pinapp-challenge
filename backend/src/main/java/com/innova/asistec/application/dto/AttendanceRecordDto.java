package com.innova.asistec.application.dto;

import com.innova.asistec.domain.model.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AttendanceRecordDto(
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String studentId,

        @NotNull(message = "El estado de asistencia es obligatorio")
        AttendanceStatus status
) {
}
