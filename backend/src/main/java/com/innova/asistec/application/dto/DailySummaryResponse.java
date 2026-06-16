package com.innova.asistec.application.dto;

import java.time.LocalDate;

public record DailySummaryResponse(
        String sectionId,
        String sectionName,
        LocalDate date,
        int present,
        int absent,
        int late,
        boolean recorded
) {
}
