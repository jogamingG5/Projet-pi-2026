package com.example.projectPi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.projectPi.models.Statistiques;

@Repository
public interface StatistiquesRepository extends MongoRepository<Statistiques, String> {

    // Trouver les statistiques d'une équipe pour un sport
    Optional<Statistiques> findByTeamIdAndSportId(String teamId, String sportId);

    // Trouver toutes les statistiques d'une équipe
    List<Statistiques> findByTeamId(String teamId);

    // Trouver toutes les statistiques d'un sport
    List<Statistiques> findBySportId(String sportId);

    // Trouver toutes les statistiques
    List<Statistiques> findAll();
}
