package com.example.projectPi.exception;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(String id) {
        super("Match introuvable avec l'identifiant : " + id);
    }
}