package com.innova.asistec.application.service;

import com.innova.asistec.application.dto.AttendanceRecordDto;
import com.innova.asistec.application.dto.DailySummaryResponse;
import com.innova.asistec.application.dto.SectionAttendanceRecordResponse;
import com.innova.asistec.application.dto.StudentHistoryResponse;
import com.innova.asistec.domain.exception.InvalidDateException;
import com.innova.asistec.domain.exception.SectionNotFoundException;
import com.innova.asistec.domain.exception.StudentNotFoundException;
import com.innova.asistec.domain.exception.StudentNotInSectionException;
import com.innova.asistec.domain.model.AttendanceRecord;
import com.innova.asistec.domain.model.AttendanceStatus;
import com.innova.asistec.domain.model.Section;
import com.innova.asistec.domain.model.Student;
import com.innova.asistec.domain.repository.AttendanceRecordRepository;
import com.innova.asistec.domain.repository.SectionRepository;
import com.innova.asistec.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService implements SaveAttendanceUseCase,
        GetAttendanceSummaryUseCase,
        GetStudentHistoryUseCase,
        GetPendingSectionsUseCase,
        GetSectionAttendanceUseCase {

    private final SectionRepository sectionRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceService(SectionRepository sectionRepository,
                             StudentRepository studentRepository,
                             AttendanceRecordRepository attendanceRecordRepository) {
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Override
    public void saveAttendance(String sectionId, LocalDate date, List<AttendanceRecordDto> records) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new SectionNotFoundException(sectionId));

        if (!date.equals(LocalDate.now())) {
            throw new InvalidDateException();
        }

        List<AttendanceRecord> entities = new ArrayList<>();
        for (AttendanceRecordDto record : records) {
            Student student = studentRepository.findById(record.studentId())
                    .orElseThrow(() -> new StudentNotFoundException(record.studentId()));

            if (!student.getSection().getId().equals(sectionId)) {
                throw new StudentNotInSectionException(record.studentId(), sectionId);
            }

            Optional<AttendanceRecord> existing =
                    attendanceRecordRepository.findByStudentIdAndDate(record.studentId(), date);

            if (existing.isPresent()) {
                existing.get().setStatus(record.status());
                entities.add(existing.get());
            } else {
                entities.add(new AttendanceRecord(student, section, date, record.status()));
            }
        }

        attendanceRecordRepository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionAttendanceRecordResponse> getSectionAttendance(String sectionId, LocalDate date) {
        return attendanceRecordRepository.findBySectionIdAndDate(sectionId, date).stream()
                .map(r -> new SectionAttendanceRecordResponse(r.getStudent().getId(), r.getStatus()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailySummaryResponse> getSummary(LocalDate date) {
        return sectionRepository.findAll().stream()
                .map(section -> buildDailySummary(section, date))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentHistoryResponse> getHistory(String studentId, LocalDate from, LocalDate to) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        return attendanceRecordRepository.findByStudentIdAndDateBetween(studentId, from, to).stream()
                .sorted(Comparator.comparing(AttendanceRecord::getDate))
                .map(record -> new StudentHistoryResponse(
                        record.getDate(),
                        record.getSection().getId(),
                        record.getStatus()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPendingSections(LocalDate date) {
        List<String> recordedSectionIds = attendanceRecordRepository.findSectionIdsWithRecordsOnDate(date);

        return sectionRepository.findAll().stream()
                .map(Section::getId)
                .filter(sectionId -> !recordedSectionIds.contains(sectionId))
                .sorted()
                .toList();
    }

    private DailySummaryResponse buildDailySummary(Section section, LocalDate date) {
        Map<String, Long> counts = attendanceRecordRepository.countByStatusForSectionOnDate(section.getId(), date);

        int present = counts.getOrDefault(AttendanceStatus.PRESENT.name(), 0L).intValue();
        int absent = counts.getOrDefault(AttendanceStatus.ABSENT.name(), 0L).intValue();
        int late = counts.getOrDefault(AttendanceStatus.LATE.name(), 0L).intValue();
        boolean recorded = attendanceRecordRepository.hasRecordsForSectionOnDate(section.getId(), date);

        return new DailySummaryResponse(
                section.getId(),
                section.getName(),
                date,
                present,
                absent,
                late,
                recorded
        );
    }
}
