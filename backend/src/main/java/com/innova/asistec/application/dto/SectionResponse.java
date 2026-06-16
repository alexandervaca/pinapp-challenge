package com.innova.asistec.application.dto;

import java.util.List;

public record SectionResponse(
        String id,
        String name,
        Long gradeId,
        String gradeName,
        List<StudentSummaryDto> students
) {
}
