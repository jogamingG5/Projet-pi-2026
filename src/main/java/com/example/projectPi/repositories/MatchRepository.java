package com.example.projectPi.repositories;



import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.projectPi.models.Match;

@Repository
public interface MatchRepository extends MongoRepository<Match, String> {

    // Exemples de requêtes dérivées utiles
    List<Match> findByStatus(Match.MatchStatus status);
    List<Match> findByTeam1IdOrTeam2Id(String team1Id, String team2Id);
}