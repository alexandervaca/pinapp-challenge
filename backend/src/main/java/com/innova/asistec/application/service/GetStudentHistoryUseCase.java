package com.innova.asistec.application.service;

import com.innova.asistec.application.dto.StudentHistoryResponse;

import java.time.LocalDate;
import java.util.List;

public interface GetStudentHistoryUseCase {

    List<StudentHistoryResponse> getHistory(String studentId, LocalDate from, LocalDate to);
}
