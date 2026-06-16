package com.innova.asistec.application.service;

import com.innova.asistec.application.dto.SectionAttendanceRecordResponse;

import java.time.LocalDate;
import java.util.List;

public interface GetSectionAttendanceUseCase {
    List<SectionAttendanceRecordResponse> getSectionAttendance(String sectionId, LocalDate date);
}
