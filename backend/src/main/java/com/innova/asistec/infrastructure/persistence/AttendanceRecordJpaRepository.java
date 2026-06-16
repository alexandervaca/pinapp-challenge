package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordJpaRepository extends JpaRepository<AttendanceRecord, Long> {

    boolean existsByStudent_IdAndDate(String studentId, LocalDate date);

    Optional<AttendanceRecord> findByStudent_IdAndDate(String studentId, LocalDate date);

    List<AttendanceRecord> findByStudent_IdAndDateBetween(String studentId, LocalDate from, LocalDate to);

    List<AttendanceRecord> findBySection_IdAndDate(String sectionId, LocalDate date);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM AttendanceRecord r " +
           "WHERE r.section.id = :sectionId AND r.date = :date")
    boolean hasRecordsForSectionOnDate(@Param("sectionId") String sectionId, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT r.section.id FROM AttendanceRecord r WHERE r.date = :date")
    List<String> findSectionIdsWithRecordsOnDate(@Param("date") LocalDate date);

    @Query("SELECT r.status, COUNT(r) FROM AttendanceRecord r " +
           "WHERE r.section.id = :sectionId AND r.date = :date GROUP BY r.status")
    List<Object[]> countByStatusForSectionOnDate(@Param("sectionId") String sectionId, @Param("date") LocalDate date);
}
