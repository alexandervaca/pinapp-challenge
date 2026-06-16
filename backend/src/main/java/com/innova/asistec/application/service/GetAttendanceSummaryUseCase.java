package com.innova.asistec.application.service;

import com.innova.asistec.application.dto.DailySummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface GetAttendanceSummaryUseCase {

    List<DailySummaryResponse> getSummary(LocalDate date);
}
