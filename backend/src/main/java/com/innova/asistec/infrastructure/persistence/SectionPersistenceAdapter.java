package com.innova.asistec.infrastructure.persistence;

import com.innova.asistec.domain.model.Section;
import com.innova.asistec.domain.repository.SectionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SectionPersistenceAdapter implements SectionRepository {

    private final SectionJpaRepository jpaRepository;

    public SectionPersistenceAdapter(SectionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Section> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Section> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }
}
