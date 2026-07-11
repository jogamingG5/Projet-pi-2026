package com.example.projectPi.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

import com.example.projectPi.dto.MatchRequest;
import com.example.projectPi.dto.TeamRanking;
import com.example.projectPi.dto.TeamStats;
import com.example.projectPi.models.Match;
import com.example.projectPi.services.MatchService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/matchs")
@Tag(name = "Matches", description = "Gestion des matchs sportifs (CRUD, validation, recherche avancée)")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // ==================== SEARCH & ADVANCED ENDPOINTS (Specific paths FIRST) ====================

    @GetMapping("/search")
    @Operation(summary = "Recherche avancée", 
               description = "Recherche les matchs selon les critères de filtrage multi-critères")
    public ResponseEntity<List<Match>> searchMatches(
            @Parameter(description = "ID de l'équipe (optionnel)")
            @RequestParam(required = false) String teamId,
            @Parameter(description = "ID du sport (optionnel)")
            @RequestParam(required = false) String sportId,
            @Parameter(description = "Date de début (optionnel)")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "Date de fin (optionnel)")
            @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Statut du match (optionnel)")
            @RequestParam(required = false) Match.MatchStatus status) {
        List<Match> results = matchService.searchMatches(teamId, sportId, startDate, endDate, status);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/stats/team/{teamId}")
    @Operation(summary = "Statistiques d'une équipe", description = "Récupère les statistiques complètes d'une équipe")
    public ResponseEntity<TeamStats> getTeamStats(@PathVariable String teamId) {
        TeamStats stats = matchService.getTeamStats(teamId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/status-count")
    @Operation(summary = "Comptage par statut", description = "Compte les matchs par statut (SCHEDULED, ONGOING, COMPLETED, CANCELLED)")
    public ResponseEntity<Map<String, Integer>> getMatchCountByStatus() {
        Map<String, Integer> statusCount = matchService.getMatchCountByStatus();
        return ResponseEntity.ok(statusCount);
    }

    @GetMapping("/rankings/{sportId}")
    @Operation(summary = "Classement par sport", description = "Récupère le classement complet pour un sport")
    public ResponseEntity<List<TeamRanking>> getTeamRankings(@PathVariable String sportId) {
        List<TeamRanking> rankings = matchService.getTeamRankings(sportId);
        return ResponseEntity.ok(rankings);
    }

    @GetMapping("/upcoming/{teamId}")
    @Operation(summary = "Matchs à venir d'une équipe", description = "Récupère les matchs futurs planifiés")
    public ResponseEntity<List<Match>> getUpcomingMatches(@PathVariable String teamId) {
        List<Match> matches = matchService.getUpcomingMatches(teamId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/history/{teamId}")
    @Operation(summary = "Historique d'une équipe", description = "Récupère tous les matchs complétés d'une équipe")
    public ResponseEntity<List<Match>> getTeamHistory(@PathVariable String teamId) {
        List<Match> history = matchService.getTeamHistory(teamId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/referees/active")
    @Operation(summary = "Arbitres les plus actifs", description = "Lisle des arbitres triés par nombre de matches arbitrés")
    public ResponseEntity<List<Map<String, Object>>> getMostActiveReferees() {
        List<Map<String, Object>> referees = matchService.getMostActiveReferees();
        return ResponseEntity.ok(referees);
    }

    @GetMapping("/event/{eventId}/completed")
    @Operation(summary = "Matchs complétés d'un événement", 
               description = "Récupère l'historique complet des matchs joués pour un événement")
    public ResponseEntity<List<Match>> getCompletedMatchesByEvent(@PathVariable String eventId) {
        List<Match> matches = matchService.getCompletedMatchesByEvent(eventId);
        return ResponseEntity.ok(matches);
    }

    // ==================== ID-BASED ENDPOINTS (/{id} & /{id}/* patterns) ====================

    @GetMapping("/{id}/detect-conflicts")
    @Operation(summary = "Détecter les conflits de calendrier", 
               description = "Identifie les conflits d'équipes, terrains ou arbitres pour ce match")
    public ResponseEntity<Map<String, Object>> detectCalendarConflicts(@PathVariable String id) {
        Map<String, Object> conflicts = matchService.detectCalendarConflicts(id);
        return ResponseEntity.ok(conflicts);
    }

    @GetMapping("/{id}/can-modify")
    @Operation(summary = "Vérifier si modifiable", 
               description = "Vérifie si un match peut être modifié (n'est pas verrouillé si complété)")
    public ResponseEntity<Map<String, Boolean>> canModifyMatch(@PathVariable String id) {
        boolean canModify = matchService.canModifyMatch(id);
        Map<String, Boolean> result = new java.util.HashMap<>();
        result.put("canModify", canModify);
        result.put("isCompleted", !canModify);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Valider les données du match", 
               description = "Valide que le match respecte les règles métier")
    public ResponseEntity<Map<String, Object>> validateMatchData(@PathVariable String id) {
        Match match = matchService.getMatchById(id);
        Map<String, Object> validation = matchService.validateMatchData(match);
        return ResponseEntity.ok(validation);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un match par ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Match trouvé"),
        @ApiResponse(responseCode = "404", description = "Match non trouvé")
    })
    public ResponseEntity<Match> getMatchById(
            @Parameter(description = "ID du match")
            @PathVariable String id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un match")
    @ApiResponse(responseCode = "200", description = "Match modifié")
    public ResponseEntity<Match> updateMatch(
            @Parameter(description = "ID du match")
            @PathVariable String id,
            @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.updateMatch(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un match")
    @ApiResponse(responseCode = "204", description = "Match supprimé")
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "ID du match")
            @PathVariable String id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CRUD BASE ENDPOINTS ====================

    @GetMapping
    @Operation(summary = "Récupérer tous les matches")
    @ApiResponse(responseCode = "200", description = "Liste des matches")
    public ResponseEntity<List<Match>> getAllMatchs() {
        return ResponseEntity.ok(matchService.getAllMatchs());
    }

    @GetMapping("/paged")
    @Operation(summary = "Matchs paginés",
               description = "Liste paginée des matchs (page 1-based) avec filtres optionnels sport/statut/date")
    public ResponseEntity<com.example.projectPi.dto.PagedResponse<Match>> getMatchsPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sportId,
            @RequestParam(required = false) Match.MatchStatus status,
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(matchService.getMatchsPaged(page, size, sportId, status, date));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau match")
    @ApiResponse(responseCode = "201", description = "Match créé")
    public ResponseEntity<Match> createMatch(@Valid @RequestBody MatchRequest request) {
        Match created = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}