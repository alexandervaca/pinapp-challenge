package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentJpaRepository extends JpaRepository<Student, String> {

    List<Student> findBySection_Id(String sectionId);
}
