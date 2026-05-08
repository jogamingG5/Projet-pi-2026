package com.example.projectPi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.projectPi.models.FeuillesDeMatch;

@Repository
public interface FeuillesDeMatchRepository extends MongoRepository<FeuillesDeMatch, String> {

    // Trouver une feuille de match par match ID
    Optional<FeuillesDeMatch> findByMatchId(String matchId);

    // Trouver toutes les feuilles de match
    List<FeuillesDeMatch> findAll();
}
