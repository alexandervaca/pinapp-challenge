package com.innova.asistec.application.service;

import com.innova.asistec.application.dto.AttendanceRecordDto;

import java.time.LocalDate;
import java.util.List;

public interface SaveAttendanceUseCase {

    void saveAttendance(String sectionId, LocalDate date, List<AttendanceRecordDto> records);
}
