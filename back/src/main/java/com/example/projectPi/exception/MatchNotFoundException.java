package com.example.projectPi.exception;

/**
 * Exception thrown when a Match resource is not found
 */
public class MatchNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public MatchNotFoundException(String id) {
        super("Match not found with ID: " + id);
    }
    
    public MatchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}