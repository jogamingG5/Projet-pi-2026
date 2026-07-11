package com.example.projectPi.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.EventRequest;
import com.example.projectPi.dto.EventResponse;
import com.example.projectPi.exception.EventNotFoundException;
import com.example.projectPi.models.Event;
import com.example.projectPi.models.Match;
import com.example.projectPi.repositories.EventRepository;
import com.example.projectPi.repositories.MatchRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MatchRepository matchRepository;

    public EventService(EventRepository eventRepository, MatchRepository matchRepository) {
        this.eventRepository = eventRepository;
        this.matchRepository = matchRepository;
    }

    // ==================== CRUD BASIC ====================

    /**
     * Crée un nouvel événement
     */
    public Event createEvent(EventRequest request) {
        Event event = new Event();
        applyRequest(event, request);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        event.setStatus(Event.EventStatus.PLANNED);
        return eventRepository.save(event);
    }

    /**
     * Récupère tous les événements
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Liste paginée des événements, avec filtres optionnels (sport, type, recherche nom).
     * Tri par date de début décroissante. page est 1-based.
     */
    public com.example.projectPi.dto.PagedResponse<Event> getEventsPaged(
            int page, int size, String sportId, Event.EventType type, String search) {

        String query = search == null ? "" : search.trim().toLowerCase();

        List<Event> filtered = eventRepository.findAll().stream()
            .filter(e -> sportId == null || sportId.isEmpty() || sportId.equals(e.getSportId()))
            .filter(e -> type == null || e.getType() == type)
            .filter(e -> query.isEmpty()
                || (e.getNomEvenement() != null && e.getNomEvenement().toLowerCase().contains(query)))
            .sorted(java.util.Comparator.comparing(Event::getDateDebut,
                java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
            .collect(Collectors.toList());

        int safeSize = size <= 0 ? 9 : size;
        int safePage = page <= 0 ? 1 : page;
        int from = Math.min((safePage - 1) * safeSize, filtered.size());
        int to = Math.min(from + safeSize, filtered.size());
        List<Event> content = filtered.subList(from, to);

        return new com.example.projectPi.dto.PagedResponse<>(content, safePage, safeSize, filtered.size());
    }

    /**
     * Récupère un événement par ID
     */
    public Event getEventById(String id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new EventNotFoundException(id));
    }

    /**
     * Met à jour un événement
     */
    public Event updateEvent(String id, EventRequest request) {
        Event event = getEventById(id);
        applyRequest(event, request);
        event.setUpdatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Supprime un événement
     */
    public void deleteEvent(String id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    // ==================== FONCTIONS MÉTIER AVANCÉES ====================

    /**
     * Valide les doublesbookings : détecte les conflits d'équipes, terrains, arbitres
     * Retourne une map avec les conflits trouvés
     */
    public Map<String, Object> validateDoubleBookings(String eventId) {
        Event event = getEventById(eventId);
        Map<String, Object> conflicts = new HashMap<>();
        
        // Récupère tous les matches associés à cet événement
        List<Match> eventMatches = matchRepository.findAll()
            .stream()
            .filter(m -> m.getEventId() != null && m.getEventId().equals(eventId))
            .filter(m -> m.getStatus() == Match.MatchStatus.SCHEDULED)
            .collect(Collectors.toList());

        List<String> teamConflicts = new java.util.ArrayList<>();
        List<String> terrainConflicts = new java.util.ArrayList<>();
        List<String> arbitreConflicts = new java.util.ArrayList<>();

        // Vérifie les conflits de calendrier pour chaque match
        for (Match match : eventMatches) {
            // Conflit d'équipes (même équipe dans 2+ matches à la même date/heure)
            List<Match> sameTeamMatches = eventMatches.stream()
                .filter(m -> !m.getId().equals(match.getId()))
                .filter(m -> m.getDateDebut().equals(match.getDateDebut()))
                .filter(m -> m.getHeure().equals(match.getHeure()))
                .filter(m -> m.getTeam1Id().equals(match.getTeam1Id()) || m.getTeam1Id().equals(match.getTeam2Id())
                        || m.getTeam2Id().equals(match.getTeam1Id()) || m.getTeam2Id().equals(match.getTeam2Id()))
                .collect(Collectors.toList());
            
            if (!sameTeamMatches.isEmpty()) {
                teamConflicts.add("Match " + match.getId() + " a conflit d'équipe");
            }

            // Conflit de terrain
            List<Match> sameGroundMatches = eventMatches.stream()
                .filter(m -> !m.getId().equals(match.getId()))
                .filter(m -> m.getDateDebut().equals(match.getDateDebut()))
                .filter(m -> m.getTerrainId().equals(match.getTerrainId()))
                .collect(Collectors.toList());
            
            if (!sameGroundMatches.isEmpty()) {
                terrainConflicts.add("Match " + match.getId() + " a conflit de terrain");
            }

            // Conflit d'arbitre
            if (match.getArbitreId() != null) {
                List<Match> sameRefereeMatches = eventMatches.stream()
                    .filter(m -> !m.getId().equals(match.getId()))
                    .filter(m -> m.getDateDebut().equals(match.getDateDebut()))
                    .filter(m -> m.getArbitreId() != null && m.getArbitreId().equals(match.getArbitreId()))
                    .collect(Collectors.toList());
                
                if (!sameRefereeMatches.isEmpty()) {
                    arbitreConflicts.add("Arbitre " + match.getArbitreId() + " a conflit de calendrier");
                }
            }
        }

        conflicts.put("teamConflicts", teamConflicts);
        conflicts.put("terrainConflicts", terrainConflicts);
        conflicts.put("arbitreConflicts", arbitreConflicts);
        conflicts.put("hasConflicts", !teamConflicts.isEmpty() || !terrainConflicts.isEmpty() || !arbitreConflicts.isEmpty());

        return conflicts;
    }

    /**
     * Récupère la progression de l'événement (% de matches joués)
     */
    public Map<String, Object> getEventProgress(String eventId) {
        Event event = getEventById(eventId);
        
        List<Match> eventMatches = matchRepository.findAll()
            .stream()
            .filter(m -> m.getEventId() != null && m.getEventId().equals(eventId))
            .collect(Collectors.toList());

        int totalMatches = eventMatches.size();
        int completedMatches = (int) eventMatches.stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED)
            .count();
        
        int ongoingMatches = (int) eventMatches.stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.ONGOING)
            .count();
        
        int scheduledMatches = (int) eventMatches.stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.SCHEDULED)
            .count();

        double progressPercentage = totalMatches == 0 ? 0 : (double) completedMatches / totalMatches * 100;

        Map<String, Object> progress = new HashMap<>();
        progress.put("eventId", eventId);
        progress.put("eventName", event.getNomEvenement());
        progress.put("totalMatches", totalMatches);
        progress.put("completedMatches", completedMatches);
        progress.put("ongoingMatches", ongoingMatches);
        progress.put("scheduledMatches", scheduledMatches);
        progress.put("progressPercentage", String.format("%.2f", progressPercentage) + "%");
        progress.put("status", event.getStatus());

        return progress;
    }

    /**
     * Gère les équipes participants : ajoute/retire une équipe
     */
    public Event addTeamToEvent(String eventId, String teamId) {
        Event event = getEventById(eventId);
        if (event.getTeamsIds() == null) {
            event.setTeamsIds(new java.util.ArrayList<>());
        }
        if (!event.getTeamsIds().contains(teamId)) {
            event.getTeamsIds().add(teamId);
            event.setUpdatedAt(LocalDateTime.now());
            return eventRepository.save(event);
        }
        return event;
    }

    /**
     * Retire une équipe d'un événement
     */
    public Event removeTeamFromEvent(String eventId, String teamId) {
        Event event = getEventById(eventId);
        if (event.getTeamsIds() != null && event.getTeamsIds().contains(teamId)) {
            event.getTeamsIds().remove(teamId);
            event.setUpdatedAt(LocalDateTime.now());
            return eventRepository.save(event);
        }
        return event;
    }

    /**
     * Génère un rapport/export d'événement (JSON)
     */
    public EventResponse generateEventReport(String eventId) {
        Event event = getEventById(eventId);
        EventResponse response = new EventResponse(event);

        // Calcule les stats de progression
        Map<String, Object> progress = getEventProgress(eventId);
        response.setCompletedMatches((Integer) progress.get("completedMatches"));
        response.setProgressPercentage(Double.parseDouble(
            ((String) progress.get("progressPercentage")).replace("%", "")));

        return response;
    }

    /**
     * Marque un événement comme terminé et met à jour son statut
     */
    public Event finishEvent(String eventId) {
        Event event = getEventById(eventId);
        event.setStatus(Event.EventStatus.FINISHED);
        event.setUpdatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    /**
     * Met un événement en cours
     */
    public Event startEvent(String eventId) {
        Event event = getEventById(eventId);
        event.setStatus(Event.EventStatus.ONGOING);
        event.setUpdatedAt(LocalDateTime.now());
        return eventRepository.save(event);
    }

    // ==================== RECHERCHE & FILTRAGE AVANCÉS ====================

    /**
     * Recherche avancée avec critères multiples
     */
    public List<Event> searchEvents(String sportId, Event.EventType type, 
                                    LocalDate startDate, LocalDate endDate) {
        List<Event> events = eventRepository.findAll();

        if (sportId != null && !sportId.isEmpty()) {
            events = events.stream()
                .filter(e -> e.getSportId().equals(sportId))
                .collect(Collectors.toList());
        }

        if (type != null) {
            events = events.stream()
                .filter(e -> e.getType() == type)
                .collect(Collectors.toList());
        }

        if (startDate != null && endDate != null) {
            events = events.stream()
                .filter(e -> !e.getDateDebut().isBefore(startDate) && !e.getDateFin().isAfter(endDate))
                .collect(Collectors.toList());
        }

        return events;
    }

    /**
     * Récupère les événements actifs pour un sport
     */
    public List<Event> getActiveEventsBySport(String sportId) {
        return eventRepository.findBySportIdAndStatus(sportId, Event.EventStatus.ONGOING);
    }

    /**
     * Récupère les événements par équipe participant
     */
    public List<Event> getEventsByTeam(String teamId) {
        return eventRepository.findByTeamsIdsContains(teamId);
    }

    /**
     * Récupère les événements dans une plage de dates
     */
    public List<Event> getEventsByDateRange(LocalDate start, LocalDate end) {
        return eventRepository.findByDateDebutBetween(start, end);
    }

    // ==================== MÉTHODE UTILITAIRE ====================

    private void applyRequest(Event event, EventRequest req) {
        event.setNomEvenement(req.getNomEvenement());
        event.setDescription(req.getDescription());
        event.setDateDebut(req.getDateDebut());
        event.setDateFin(req.getDateFin());
        event.setType(req.getType());
        event.setSportId(req.getSportId());
        event.setTeamsIds(req.getTeamsIds());
        event.setLocationId(req.getLocationId());
        event.setExpectedMatches(req.getExpectedMatches());
    }
}
