package com.innova.asistec.domain.repository;

import com.innova.asistec.domain.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {

    Optional<Student> findById(String id);

    List<Student> findBySectionId(String sectionId);
}
