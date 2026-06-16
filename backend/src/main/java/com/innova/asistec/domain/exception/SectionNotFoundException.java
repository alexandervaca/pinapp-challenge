package com.innova.asistec.domain.exception;

public class SectionNotFoundException extends RuntimeException {

    public SectionNotFoundException(String sectionId) {
        super("Section not found: " + sectionId);
    }
}
