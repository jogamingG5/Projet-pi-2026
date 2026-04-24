package com.example.projectPi.exception;

/**
 * Exception thrown when a resource already exists
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
