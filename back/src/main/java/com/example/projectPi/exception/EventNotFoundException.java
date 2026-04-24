package com.example.projectPi.exception;

/**
 * Exception thrown when an Event resource is not found
 */
public class EventNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public EventNotFoundException(String id) {
        super("Event not found with ID: " + id);
    }
    
    public EventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
