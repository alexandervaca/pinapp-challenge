package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeJpaRepository extends JpaRepository<Grade, Long> {
}
