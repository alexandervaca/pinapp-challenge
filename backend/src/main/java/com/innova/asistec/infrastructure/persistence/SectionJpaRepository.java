package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionJpaRepository extends JpaRepository<Section, String> {
}
