package com.innova.asistec.domain.repository;

import com.innova.asistec.domain.model.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceRecordRepository {

    boolean existsByStudentIdAndDate(String studentId, LocalDate date);

    Optional<AttendanceRecord> findByStudentIdAndDate(String studentId, LocalDate date);

    void saveAll(List<AttendanceRecord> records);

    List<AttendanceRecord> findByStudentIdAndDateBetween(String studentId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findBySectionIdAndDate(String sectionId, LocalDate date);

    Map<String, Long> countByStatusForSectionOnDate(String sectionId, LocalDate date);

    boolean hasRecordsForSectionOnDate(String sectionId, LocalDate date);

    List<String> findSectionIdsWithRecordsOnDate(LocalDate date);
}
