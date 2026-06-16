package com.innova.asistec.application.dto;

import com.innova.asistec.domain.model.AttendanceStatus;

public record SectionAttendanceRecordResponse(String studentId, AttendanceStatus status) {
}
