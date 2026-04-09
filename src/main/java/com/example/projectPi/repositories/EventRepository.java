package com.example.projectPi.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.projectPi.models.Event;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    
    // Rechercher par status
    List<Event> findByStatus(Event.EventStatus status);
    
    // Rechercher par type
    List<Event> findByType(Event.EventType type);
    
    // Rechercher par sport
    List<Event> findBySportId(String sportId);
    
    // Rechercher par plage de dates
    List<Event> findByDateDebutBetween(LocalDate start, LocalDate end);
    
    // Rechercher par équipe participant
    List<Event> findByTeamsIdsContains(String teamId);
    
    // Rechercher par nom
    List<Event> findByNomEvenementContainingIgnoreCase(String nom);
    
    // Combinaisons
    List<Event> findBySportIdAndStatus(String sportId, Event.EventStatus status);
    
    List<Event> findBySportIdAndType(String sportId, Event.EventType type);
}
