package com.innova.asistec.domain.exception;

public class InvalidDateException extends RuntimeException {

    public InvalidDateException() {
        super("Attendance can only be recorded for today's date");
    }
}
