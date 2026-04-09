package com.example.projectPi.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String id) {
        super("Événement introuvable avec l'identifiant : " + id);
    }
}
