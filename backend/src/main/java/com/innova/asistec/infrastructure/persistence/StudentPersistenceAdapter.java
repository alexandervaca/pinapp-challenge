package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.Student;
import com.innova.asistec.domain.repository.StudentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentPersistenceAdapter implements StudentRepository {

    private final StudentJpaRepository jpaRepository;

    public StudentPersistenceAdapter(StudentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Student> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Student> findBySectionId(String sectionId) {
        return jpaRepository.findBySection_Id(sectionId);
    }
}
