package com.innova.asistec.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record SaveAttendanceRequest(
        @NotBlank(message = "El ID de la sección es obligatorio")
        String sectionId,

        @NotNull(message = "La fecha es obligatoria")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate date,

        @NotEmpty(message = "La lista de registros no puede estar vacía")
        @Valid
        List<AttendanceRecordDto> records
) {
}
