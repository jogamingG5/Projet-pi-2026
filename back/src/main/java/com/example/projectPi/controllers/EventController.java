package com.example.projectPi.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;

import com.example.projectPi.dto.ApiResponseDTO;
import com.example.projectPi.dto.EventRequest;
import com.example.projectPi.dto.EventResponse;
import com.example.projectPi.models.Event;
import com.example.projectPi.services.EventService;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Gestion des événements sportifs (tournois, championnats, matchs)")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ==================== SPECIALIZED SEARCH ENDPOINTS (before /{id}) ====================

    @GetMapping("/search")
    @Operation(summary = "Recherche avancée d'événements", 
               description = "Recherche les événements selon les critères de filtrage (sport, type, dates)")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<List<Event>> searchEvents(
            @Parameter(description = "ID du sport (optionnel)")
            @RequestParam(required = false) String sportId,
            @Parameter(description = "Type d'événement: LEAGUE, FRIENDLY, TOURNAMENT (optionnel)")
            @RequestParam(required = false) Event.EventType type,
            @Parameter(description = "Date de début (optionnel)")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Date de fin (optionnel)")
            @RequestParam(required = false) LocalDate endDate) {
        List<Event> results = eventService.searchEvents(sportId, type, startDate, endDate);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/sport/{sportId}/active")
    @Operation(summary = "Événements actifs par sport", 
               description = "Retourne les événements en cours (ONGOING) pour un sport donné")
    @ApiResponse(responseCode = "200", description = "Liste des événements actifs")
    public ResponseEntity<List<Event>> getActiveEventsBySport(
            @Parameter(description = "ID du sport")
            @PathVariable String sportId) {
        List<Event> events = eventService.getActiveEventsBySport(sportId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Événements par équipe", 
               description = "Retourne les événements auxquels une équipe participe")
    @ApiResponse(responseCode = "200", description = "Liste des événements")
    public ResponseEntity<List<Event>> getEventsByTeam(
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        List<Event> events = eventService.getEventsByTeam(teamId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Événements par plage de dates", 
               description = "Retourne les événements qui commencent dans la plage de dates spécifiée")
    @ApiResponse(responseCode = "200", description = "Liste des événements")
    public ResponseEntity<List<Event>> getEventsByDateRange(
            @Parameter(description = "Date de début", example = "2026-01-01")
            @RequestParam LocalDate start,
            @Parameter(description = "Date de fin", example = "2026-12-31")
            @RequestParam LocalDate end) {
        List<Event> events = eventService.getEventsByDateRange(start, end);
        return ResponseEntity.ok(events);
    }

    // ==================== ID-BASED SPECIALIZED ENDPOINTS (/{id}/...) ====================

    @GetMapping("/{id}/validate-double-bookings")
    @Operation(summary = "Valider les doublesbookings", 
               description = "Détecte les conflits de calendrier (équipes, terrains, arbitres dans cet événement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validation effectuée"),
        @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    })
    public ResponseEntity<Map<String, Object>> validateDoubleBookings(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id) {
        Map<String, Object> conflicts = eventService.validateDoubleBookings(id);
        return ResponseEntity.ok(conflicts);
    }

    @GetMapping("/{id}/progress")
    @Operation(summary = "Récupérer la progression de l'événement", 
               description = "Retourne le pourcentage de matches joués et les statistiques")
    @ApiResponse(responseCode = "200", description = "Statistiques de progression")
    public ResponseEntity<Map<String, Object>> getEventProgress(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id) {
        Map<String, Object> progress = eventService.getEventProgress(id);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/{id}/add-team/{teamId}")
    @Operation(summary = "Ajouter une équipe à l'événement", 
               description = "Ajoute une équipe à la liste des participants")
    @ApiResponse(responseCode = "200", description = "Équipe ajoutée")
    public ResponseEntity<Event> addTeamToEvent(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id,
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        Event updated = eventService.addTeamToEvent(id, teamId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/remove-team/{teamId}")
    @Operation(summary = "Retirer une équipe de l'événement", 
               description = "Supprime une équipe de la liste des participants")
    @ApiResponse(responseCode = "200", description = "Équipe retirée")
    public ResponseEntity<Event> removeTeamFromEvent(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id,
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        Event updated = eventService.removeTeamFromEvent(id, teamId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/report")
    @Operation(summary = "Générer un rapport d'événement", 
               description = "Exporte les statistiques et informations complètes de l'événement")
    @ApiResponse(responseCode = "200", description = "Rapport généré")
    public ResponseEntity<EventResponse> generateEventReport(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id) {
        EventResponse report = eventService.generateEventReport(id);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/{id}/start")
    @Operation(summary = "Démarrer un événement", 
               description = "Change le statut de l'événement à ONGOING")
    @ApiResponse(responseCode = "200", description = "Événement démarré")
    public ResponseEntity<Event> startEvent(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id) {
        Event started = eventService.startEvent(id);
        return ResponseEntity.ok(started);
    }

    @PutMapping("/{id}/finish")
    @Operation(summary = "Terminer un événement", 
               description = "Change le statut de l'événement à FINISHED")
    @ApiResponse(responseCode = "200", description = "Événement terminé")
    public ResponseEntity<Event> finishEvent(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id) {
        Event finished = eventService.finishEvent(id);
        return ResponseEntity.ok(finished);
    }

    // ==================== GENERIC ID ENDPOINTS (/{id} - MUST BE LAST) ====================

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un événement par ID", 
               description = "Retourne les détails d'un événement spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Événement trouvé"),
        @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    })
    public ResponseEntity<Event> getEventById(
            @Parameter(description = "ID de l'événement", example = "507f1f77bcf86cd799439011")
            @PathVariable String id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un événement", 
               description = "Modifie les informations d'un événement existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Événement modifié"),
        @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    })
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id,
            @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un événement", 
               description = "Supprime complètement un événement et ses données associées")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Événement supprimé"),
        @ApiResponse(responseCode = "404", description = "Événement non trouvé")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID de l'événement")
            @PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CRUD BASE ENDPOINTS ====================

    @PostMapping
    @Operation(summary = "Créer un nouvel événement", 
               description = "Crée un nouvel événement sportif (tournoi, championnat, match amical)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Événement créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponseDTO<Event>> createEvent(
            @Valid @RequestBody EventRequest request) {
        Event created = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.created(created, "Event created successfully"));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les événements", 
               description = "Retourne la liste complète de tous les événements")
    @ApiResponse(responseCode = "200", description = "Liste des événements")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }
}
