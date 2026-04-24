package com.example.projectPi.exception;

/**
 * Exception thrown when invalid input is provided
 */
public class InvalidInputException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
