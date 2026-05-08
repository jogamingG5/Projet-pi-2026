package com.example.projectPi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.projectPi.models.Classement;

@Repository
public interface ClassementRepository extends MongoRepository<Classement, String> {

    // Trouver le classement d'un événement
    Optional<Classement> findByEventId(String eventId);

    // Trouver le classement d'un sport
    Optional<Classement> findBySportId(String sportId);

    // Trouver le classement d'un événement et sport
    Optional<Classement> findByEventIdAndSportId(String eventId, String sportId);

    // Trouver tous les classements
    List<Classement> findAll();

    // Trouver tous les classements par sport
    List<Classement> findBySportIdOrderByUpdatedAtDesc(String sportId);
}
