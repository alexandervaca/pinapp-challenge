package com.innova.asistec.application.service;

import com.innova.asistec.application.dto.AttendanceRecordDto;
import com.innova.asistec.domain.exception.InvalidDateException;
import com.innova.asistec.domain.exception.SectionNotFoundException;
import com.innova.asistec.domain.model.AttendanceRecord;
import com.innova.asistec.domain.model.AttendanceStatus;
import com.innova.asistec.domain.model.Section;
import com.innova.asistec.domain.model.Student;
import com.innova.asistec.domain.repository.AttendanceRecordRepository;
import com.innova.asistec.domain.repository.SectionRepository;
import com.innova.asistec.domain.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Section section3A;
    private Student student;

    @BeforeEach
    void setUp() {
        section3A = new Section("3A", "3° A", null);
        student = new Student("S1", "Lucas Pérez", section3A);
    }

    @Test
    void saveAttendance_whenValidRequest_shouldSaveAllRecords() {
        LocalDate today = LocalDate.now();
        List<AttendanceRecordDto> records = List.of(new AttendanceRecordDto("S1", AttendanceStatus.PRESENT));

        when(sectionRepository.findById("3A")).thenReturn(Optional.of(section3A));
        when(studentRepository.findById("S1")).thenReturn(Optional.of(student));
        when(attendanceRecordRepository.findByStudentIdAndDate("S1", today)).thenReturn(Optional.empty());

        attendanceService.saveAttendance("3A", today, records);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(attendanceRecordRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
    }

    @Test
    void saveAttendance_whenRecordAlreadyExists_shouldUpdateStatus() {
        LocalDate today = LocalDate.now();
        AttendanceRecord existing = new AttendanceRecord(student, section3A, today, AttendanceStatus.PRESENT);
        List<AttendanceRecordDto> records = List.of(new AttendanceRecordDto("S1", AttendanceStatus.LATE));

        when(sectionRepository.findById("3A")).thenReturn(Optional.of(section3A));
        when(studentRepository.findById("S1")).thenReturn(Optional.of(student));
        when(attendanceRecordRepository.findByStudentIdAndDate("S1", today)).thenReturn(Optional.of(existing));

        attendanceService.saveAttendance("3A", today, records);

        assertThat(existing.getStatus()).isEqualTo(AttendanceStatus.LATE);
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(attendanceRecordRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).containsExactly(existing);
    }

    @Test
    void saveAttendance_whenSectionNotFound_shouldThrowSectionNotFoundException() {
        when(sectionRepository.findById("XX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.saveAttendance(
                "XX",
                LocalDate.now(),
                List.of(new AttendanceRecordDto("S1", AttendanceStatus.PRESENT))
        )).isInstanceOf(SectionNotFoundException.class);

        verify(attendanceRecordRepository, never()).saveAll(any());
    }

    @Test
    void saveAttendance_whenDateIsInThePast_shouldThrowInvalidDateException() {
        when(sectionRepository.findById("3A")).thenReturn(Optional.of(section3A));

        assertThatThrownBy(() -> attendanceService.saveAttendance(
                "3A",
                LocalDate.now().minusDays(1),
                List.of(new AttendanceRecordDto("S1", AttendanceStatus.PRESENT))
        )).isInstanceOf(InvalidDateException.class);

        verify(attendanceRecordRepository, never()).saveAll(any());
    }

    @Test
    void getPendingSections_shouldReturnOnlySectionsWithoutTodayRecord() {
        LocalDate today = LocalDate.now();
        Section section3B = new Section("3B", "3° B", null);
        Section section4A = new Section("4A", "4° A", null);
        Section section4B = new Section("4B", "4° B", null);

        when(sectionRepository.findAll()).thenReturn(List.of(section3A, section3B, section4A, section4B));
        when(attendanceRecordRepository.findSectionIdsWithRecordsOnDate(today)).thenReturn(List.of("3A"));

        List<String> pending = attendanceService.getPendingSections(today);

        assertThat(pending).containsExactly("3B", "4A", "4B");
    }
}
