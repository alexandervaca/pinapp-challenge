package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.Grade;
import com.innova.asistec.domain.repository.GradeRepository;
import org.springframework.stereotype.Repository;

@Repository
public class GradePersistenceAdapter implements GradeRepository {

    private final GradeJpaRepository jpaRepository;

    public GradePersistenceAdapter(GradeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Grade save(Grade grade) {
        return jpaRepository.save(grade);
    }
}
