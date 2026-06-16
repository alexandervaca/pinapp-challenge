package com.innova.asistec.domain.repository;

import com.innova.asistec.domain.model.Grade;

public interface GradeRepository {

    long count();

    Grade save(Grade grade);
}
