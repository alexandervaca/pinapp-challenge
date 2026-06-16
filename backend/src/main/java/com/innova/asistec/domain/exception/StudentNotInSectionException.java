package com.innova.asistec.domain.exception;

public class StudentNotInSectionException extends RuntimeException {

    public StudentNotInSectionException(String studentId, String sectionId) {
        super("Student " + studentId + " does not belong to section " + sectionId);
    }
}
