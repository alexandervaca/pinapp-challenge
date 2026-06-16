package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.AttendanceRecord;
import com.innova.asistec.domain.model.AttendanceStatus;
import com.innova.asistec.domain.repository.AttendanceRecordRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AttendanceRecordPersistenceAdapter implements AttendanceRecordRepository {

    private final AttendanceRecordJpaRepository jpaRepository;

    public AttendanceRecordPersistenceAdapter(AttendanceRecordJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByStudentIdAndDate(String studentId, LocalDate date) {
        return jpaRepository.existsByStudent_IdAndDate(studentId, date);
    }

    @Override
    public Optional<AttendanceRecord> findByStudentIdAndDate(String studentId, LocalDate date) {
        return jpaRepository.findByStudent_IdAndDate(studentId, date);
    }

    @Override
    public void saveAll(List<AttendanceRecord> records) {
        jpaRepository.saveAll(records);
    }

    @Override
    public List<AttendanceRecord> findByStudentIdAndDateBetween(String studentId, LocalDate from, LocalDate to) {
        return jpaRepository.findByStudent_IdAndDateBetween(studentId, from, to);
    }

    @Override
    public List<AttendanceRecord> findBySectionIdAndDate(String sectionId, LocalDate date) {
        return jpaRepository.findBySection_IdAndDate(sectionId, date);
    }

    @Override
    public Map<String, Long> countByStatusForSectionOnDate(String sectionId, LocalDate date) {
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : jpaRepository.countByStatusForSectionOnDate(sectionId, date)) {
            AttendanceStatus status = (AttendanceStatus) row[0];
            Long count = (Long) row[1];
            counts.put(status.name(), count);
        }
        return counts;
    }

    @Override
    public boolean hasRecordsForSectionOnDate(String sectionId, LocalDate date) {
        return jpaRepository.hasRecordsForSectionOnDate(sectionId, date);
    }

    @Override
    public List<String> findSectionIdsWithRecordsOnDate(LocalDate date) {
        return jpaRepository.findSectionIdsWithRecordsOnDate(date);
    }
}
