package com.bda.mapreduce.exception;

public class LogParseException extends RuntimeException {

    public LogParseException(String logLine) {
        super("Error while parsing Line: " + logLine);
    }
}
