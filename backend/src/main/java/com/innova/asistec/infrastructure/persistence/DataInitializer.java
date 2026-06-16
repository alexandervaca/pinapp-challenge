package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.AttendanceRecord;
import com.innova.asistec.domain.model.AttendanceStatus;
import com.innova.asistec.domain.model.Grade;
import com.innova.asistec.domain.model.Section;
import com.innova.asistec.domain.model.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataInitializer implements CommandLineRunner {

    private final GradeJpaRepository gradeRepository;
    private final SectionJpaRepository sectionRepository;
    private final StudentJpaRepository studentRepository;
    private final AttendanceRecordJpaRepository attendanceRecordRepository;

    public DataInitializer(GradeJpaRepository gradeRepository,
                           SectionJpaRepository sectionRepository,
                           StudentJpaRepository studentRepository,
                           AttendanceRecordJpaRepository attendanceRecordRepository) {
        this.gradeRepository = gradeRepository;
        this.sectionRepository = sectionRepository;
        this.studentRepository = studentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (gradeRepository.count() > 0) {
            return;
        }

        Grade grade3 = gradeRepository.save(new Grade("3°"));
        Grade grade4 = gradeRepository.save(new Grade("4°"));

        Section section3A = sectionRepository.save(new Section("3A", "3° A", grade3));
        Section section3B = sectionRepository.save(new Section("3B", "3° B", grade3));
        Section section4A = sectionRepository.save(new Section("4A", "4° A", grade4));
        Section section4B = sectionRepository.save(new Section("4B", "4° B", grade4));

        saveStudents(section3A, List.of(
                student("S1", "Lucas Pérez", section3A),
                student("S2", "María García", section3A),
                student("S3", "Juan López", section3A),
                student("S4", "Ana Martínez", section3A),
                student("S5", "Carlos Ruiz", section3A),
                student("S6", "Laura Sánchez", section3A)
        ));

        saveStudents(section3B, List.of(
                student("S7", "Pedro Torres", section3B),
                student("S8", "Sofía Díaz", section3B),
                student("S9", "Diego Hernández", section3B),
                student("S10", "Valentina Flores", section3B),
                student("S11", "Matías Romero", section3B),
                student("S12", "Camila Jiménez", section3B)
        ));

        saveStudents(section4A, List.of(
                student("S13", "Andrés Morales", section4A),
                student("S14", "Isabella Castro", section4A),
                student("S15", "Sebastián Vargas", section4A),
                student("S16", "Lucía Mendoza", section4A),
                student("S17", "Tomás Ramos", section4A),
                student("S18", "Paula Silva", section4A)
        ));

        saveStudents(section4B, List.of(
                student("S19", "Nicolás Guerrero", section4B),
                student("S20", "Martina Ortega", section4B),
                student("S21", "Felipe Navarro", section4B),
                student("S22", "Catalina Medina", section4B),
                student("S23", "Rodrigo Aguilar", section4B),
                student("S24", "Valeria Reyes", section4B)
        ));

        List<LocalDate> workingDays = getLastWorkingDays(5);
        seedHistoricalAttendance(section3A, workingDays);
        seedHistoricalAttendance(section4B, workingDays);
    }

    private void saveStudents(Section section, List<Student> students) {
        studentRepository.saveAll(students);
    }

    private Student student(String id, String fullName, Section section) {
        return new Student(id, fullName, section);
    }

    private void seedHistoricalAttendance(Section section, List<LocalDate> workingDays) {
        List<Student> students = studentRepository.findBySection_Id(section.getId());
        AttendanceStatus[] statuses = AttendanceStatus.values();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (LocalDate date : workingDays) {
            for (Student student : students) {
                AttendanceStatus status = statuses[random.nextInt(statuses.length)];
                attendanceRecordRepository.save(new AttendanceRecord(student, section, date, status));
            }
        }
    }

    private List<LocalDate> getLastWorkingDays(int count) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate date = LocalDate.now().minusDays(1);
        while (days.size() < count) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY
                    && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                days.add(date);
            }
            date = date.minusDays(1);
        }
        return days;
    }
}
