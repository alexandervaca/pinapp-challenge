package com.innova.asistec.domain.repository;

import com.innova.asistec.domain.model.Section;

import java.util.List;
import java.util.Optional;

public interface SectionRepository {

    Optional<Section> findById(String id);

    List<Section> findAll();

    boolean existsById(String id);
}
