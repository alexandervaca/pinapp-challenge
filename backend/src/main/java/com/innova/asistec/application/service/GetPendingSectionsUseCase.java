package com.innova.asistec.application.service;

import java.time.LocalDate;
import java.util.List;

public interface GetPendingSectionsUseCase {

    List<String> getPendingSections(LocalDate date);
}
