package com.ivanov.jdloader.exceptions;

public class JDParseException extends Exception {
    public JDParseException() {
    }

    public JDParseException(String message) {
        super(message);
    }

    public JDParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
