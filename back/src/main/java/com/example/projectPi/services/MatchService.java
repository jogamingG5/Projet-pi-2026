package com.example.projectPi.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.MatchRequest;
import com.example.projectPi.dto.TeamRanking;
import com.example.projectPi.dto.TeamStats;
import com.example.projectPi.exception.EventNotFoundException;
import com.example.projectPi.models.Event;
import com.example.projectPi.exception.MatchNotFoundException;
import com.example.projectPi.models.Match;
import com.example.projectPi.repositories.EventRepository;
import com.example.projectPi.repositories.MatchRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final EventRepository eventRepository;
    private final MatchResultProcessor matchResultProcessor;

    public MatchService(MatchRepository matchRepository,
                        EventRepository eventRepository,
                        MatchResultProcessor matchResultProcessor) {
        this.matchRepository = matchRepository;
        this.eventRepository = eventRepository;
        this.matchResultProcessor = matchResultProcessor;
    }

    public Match createMatch(MatchRequest request) {
        Event event = requireEvent(request.getEventId());
        Match match = new Match();
        applyRequest(match, request, event);
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());
        Match saved = matchRepository.save(match);
        if (saved.getStatus() == Match.MatchStatus.COMPLETED) {
            matchResultProcessor.processCompletedMatch(saved);
        }
        return saved;
    }

    public List<Match> getAllMatchs() {
        return matchRepository.findAll();
    }

    /**
     * Liste paginée des matchs, avec filtres optionnels (sport, statut, date).
     * Tri par date décroissante. page est 1-based.
     */
    public com.example.projectPi.dto.PagedResponse<Match> getMatchsPaged(
            int page, int size, String sportId, Match.MatchStatus status, java.time.LocalDate date) {

        List<Match> filtered = matchRepository.findAll().stream()
            .filter(m -> sportId == null || sportId.isEmpty() || sportId.equals(m.getSportId()))
            .filter(m -> status == null || m.getStatus() == status)
            .filter(m -> date == null || date.equals(m.getDateDebut()))
            .sorted(Comparator.comparing(Match::getDateDebut,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());

        int safeSize = size <= 0 ? 10 : size;
        int safePage = page <= 0 ? 1 : page;
        int from = Math.min((safePage - 1) * safeSize, filtered.size());
        int to = Math.min(from + safeSize, filtered.size());
        List<Match> content = filtered.subList(from, to);

        return new com.example.projectPi.dto.PagedResponse<>(content, safePage, safeSize, filtered.size());
    }

    public Match getMatchById(String id) {
        return matchRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException(id));
    }

    public Match updateMatch(String id, MatchRequest request) {
        Match match = getMatchById(id); // lance l'exception si absent
        Event event = requireEvent(request.getEventId());
        applyRequest(match, request, event);
        match.setUpdatedAt(LocalDateTime.now());
        Match saved = matchRepository.save(match);
        // Toujours recalculer les stats des deux équipes : si un match est repassé
        // hors COMPLETED, sa contribution est retirée (recalcul filtré sur COMPLETED).
        matchResultProcessor.reprocessTeams(saved);
        if (saved.getStatus() == Match.MatchStatus.COMPLETED) {
            matchResultProcessor.processCompletedMatch(saved);
        }
        return saved;
    }

    /**
     * Vérifie que l'événement référencé existe (FK obligatoire).
     */
    private Event requireEvent(String eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public void deleteMatch(String id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchNotFoundException(id);
        }
        matchRepository.deleteById(id);
    }

    // ==================== FONCTIONNALITÉS AVANCÉES ====================

    /**
     * Récupère les statistiques d'une équipe (tous les matchs complétés)
     */
    public TeamStats getTeamStats(String teamId) {
        List<Match> allMatches = matchRepository.findAll()
            .stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED)
            .filter(m -> m.getTeam1Id().equals(teamId) || m.getTeam2Id().equals(teamId))
            .collect(Collectors.toList());

        int played = allMatches.size();
        int wins = 0, draws = 0, losses = 0;
        int goalsFor = 0, goalsAgainst = 0;

        for (Match match : allMatches) {
            boolean isTeam1 = match.getTeam1Id().equals(teamId);
            int scoreFor = isTeam1 ? match.getScoreTeam1() : match.getScoreTeam2();
            int scoreAgainst = isTeam1 ? match.getScoreTeam2() : match.getScoreTeam1();

            goalsFor += scoreFor;
            goalsAgainst += scoreAgainst;

            if (scoreFor > scoreAgainst) wins++;
            else if (scoreFor == scoreAgainst) draws++;
            else losses++;
        }

        int points = (wins * 3) + draws;
        return new TeamStats(teamId, played, wins, draws, losses, goalsFor, goalsAgainst, points);
    }

    /**
     * Récupère le classement complet de toutes les équipes (par sport)
     */
    public List<TeamRanking> getTeamRankings(String sportId) {
        // Récupère tous les matchs complétés du sport
        List<Match> completedMatches = matchRepository.findAll()
            .stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED && m.getSportId().equals(sportId))
            .collect(Collectors.toList());

        // Récupère tous les IDs d'équipes uniques
        Map<String, TeamStats> statsMap = new HashMap<>();
        for (Match match : completedMatches) {
            String team1Id = match.getTeam1Id();
            String team2Id = match.getTeam2Id();

            statsMap.putIfAbsent(team1Id, getTeamStats(team1Id));
            statsMap.putIfAbsent(team2Id, getTeamStats(team2Id));
        }

        // Trie par points (décroissant), puis par différence de buts
        List<TeamRanking> rankings = statsMap.values()
            .stream()
            .sorted(Comparator
                .comparingInt(TeamStats::getPoints).reversed()          // Points desc
                .thenComparingDouble(TeamStats::getGoalDifference).reversed()) // Diff buts desc
            .map((stats) -> new TeamRanking(0, stats))
            .collect(Collectors.toList());

        // Assigne les positions
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRank(i + 1);
        }

        return rankings;
    }

    /**
     * Récupère les matchs à venir (SCHEDULED) d'une équipe
     */
    public List<Match> getUpcomingMatches(String teamId) {
        return matchRepository.findAll()
            .stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.SCHEDULED)
            .filter(m -> m.getTeam1Id().equals(teamId) || m.getTeam2Id().equals(teamId))
            .sorted(Comparator.comparing(Match::getDateDebut))
            .collect(Collectors.toList());
    }

    /**
     * Récupère l'historique complet d'une équipe (tous les matchs terminés)
     */
    public List<Match> getTeamHistory(String teamId) {
        return matchRepository.findAll()
            .stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED)
            .filter(m -> m.getTeam1Id().equals(teamId) || m.getTeam2Id().equals(teamId))
            .sorted(Comparator.comparing(Match::getDateDebut).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Récupère les arbitres les plus actifs
     */
    public List<Map<String, Object>> getMostActiveReferees() {
        Map<String, Integer> refereeCount = new HashMap<>();

        matchRepository.findAll()
            .stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED)
            .forEach(m -> refereeCount.put(m.getArbitreId(), refereeCount.getOrDefault(m.getArbitreId(), 0) + 1));

        return refereeCount.entrySet()
            .stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .map(entry -> {
                Map<String, Object> result = new HashMap<>();
                result.put("arbitreId", entry.getKey());
                result.put("matchCount", entry.getValue());
                return result;
            })
            .collect(Collectors.toList());
    }

    /**
     * Compte les matchs par statut
     */
    public Map<String, Integer> getMatchCountByStatus() {
        Map<String, Integer> statusCount = new HashMap<>();
        statusCount.put("SCHEDULED", 0);
        statusCount.put("ONGOING", 0);
        statusCount.put("COMPLETED", 0);
        statusCount.put("CANCELLED", 0);

        matchRepository.findAll()
            .forEach(m -> statusCount.put(m.getStatus().toString(), statusCount.get(m.getStatus().toString()) + 1));

        return statusCount;
    }

    // ==================== FONCTIONS AVANCÉES POUR LES MATCHES ====================

    /**
     * Détecte les conflits de calendrier pour un match
     * Retourne true s'il y a un conflit
     */
    public Map<String, Object> detectCalendarConflicts(String matchId) {
        Match match = getMatchById(matchId);
        Map<String, Object> conflicts = new HashMap<>();
        
        List<String> conflictList = new ArrayList<>();
        boolean hasConflicts = false;

        // Vérifie les conflits d'équipes (même équipe dans 2+ matches à la même date/heure)
        List<Match> sameTeamMatches = matchRepository.findAll()
            .stream()
            .filter(m -> !m.getId().equals(matchId))
            .filter(m -> m.getDateDebut().equals(match.getDateDebut()))
            .filter(m -> m.getHeure().equals(match.getHeure()))
            .filter(m -> m.getTeam1Id().equals(match.getTeam1Id()) || m.getTeam1Id().equals(match.getTeam2Id())
                    || m.getTeam2Id().equals(match.getTeam1Id()) || m.getTeam2Id().equals(match.getTeam2Id()))
            .collect(Collectors.toList());
        
        if (!sameTeamMatches.isEmpty()) {
            conflictList.add("Conflit d'équipe détecté");
            hasConflicts = true;
        }

        // Vérifie les conflits de terrain
        List<Match> sameGroundMatches = matchRepository.findAll()
            .stream()
            .filter(m -> !m.getId().equals(matchId))
            .filter(m -> m.getDateDebut().equals(match.getDateDebut()))
            .filter(m -> m.getTerrainId().equals(match.getTerrainId()))
            .collect(Collectors.toList());
        
        if (!sameGroundMatches.isEmpty()) {
            conflictList.add("Conflit de terrain détecté");
            hasConflicts = true;
        }

        // Vérifie les conflits d'arbitre
        if (match.getArbitreId() != null) {
            List<Match> sameRefereeMatches = matchRepository.findAll()
                .stream()
                .filter(m -> !m.getId().equals(matchId))
                .filter(m -> m.getDateDebut().equals(match.getDateDebut()))
                .filter(m -> m.getArbitreId() != null && m.getArbitreId().equals(match.getArbitreId()))
                .collect(Collectors.toList());
            
            if (!sameRefereeMatches.isEmpty()) {
                conflictList.add("Conflit d'arbitre détecté");
                hasConflicts = true;
            }
        }

        conflicts.put("matchId", matchId);
        conflicts.put("hasConflicts", hasConflicts);
        conflicts.put("conflicts", conflictList);
        return conflicts;
    }

    /**
     * Valide les données d'un match avant création/modification
     * Retourne une map avec les erreurs de validation
     */
    public Map<String, Object> validateMatchData(Match match) {
        Map<String, Object> errors = new HashMap<>();
        int errorCount = 0;

        // Vérifie que les deux équipes sont différentes
        if (match.getTeam1Id() != null && match.getTeam2Id() != null 
            && match.getTeam1Id().equals(match.getTeam2Id())) {
            errors.put("teamsError", "Les deux équipes doivent être différentes");
            errorCount++;
        }

        // Vérifie que la date n'est pas dans le passé pour les matches SCHEDULED
        if (match.getStatus() == Match.MatchStatus.SCHEDULED 
            && match.getDateDebut().isBefore(java.time.LocalDate.now())) {
            errors.put("dateError", "La date du match ne peut pas être dans le passé");
            errorCount++;
        }

        // Vérifie que les scores sont valides pour les matches COMPLETED
        if (match.getStatus() == Match.MatchStatus.COMPLETED) {
            if (match.getScoreTeam1() < 0 || match.getScoreTeam2() < 0) {
                errors.put("scoreError", "Les scores ne peuvent pas être négatifs");
                errorCount++;
            }
        }

        // Vérifie que les terrains/équipes/arbitres ne sont pas null
        if (match.getTerrainId() == null || match.getTerrainId().isEmpty()) {
            errors.put("terrainError", "Un terrain doit être assigné");
            errorCount++;
        }

        errors.put("isValid", errorCount == 0);
        errors.put("errorCount", errorCount);
        return errors;
    }

    /**
     * Recherche avancée de matches avec critères multiples
     */
    public List<Match> searchMatches(String teamId, String sportId, java.time.LocalDate startDate,
                                      java.time.LocalDate endDate, Match.MatchStatus status) {
        List<Match> matches = matchRepository.findAll();

        if (teamId != null && !teamId.isEmpty()) {
            matches = matches.stream()
                .filter(m -> m.getTeam1Id().equals(teamId) || m.getTeam2Id().equals(teamId))
                .collect(Collectors.toList());
        }

        if (sportId != null && !sportId.isEmpty()) {
            matches = matches.stream()
                .filter(m -> m.getSportId().equals(sportId))
                .collect(Collectors.toList());
        }

        if (startDate != null && endDate != null) {
            matches = matches.stream()
                .filter(m -> !m.getDateDebut().isBefore(startDate) && !m.getDateDebut().isAfter(endDate))
                .collect(Collectors.toList());
        }

        if (status != null) {
            matches = matches.stream()
                .filter(m -> m.getStatus() == status)
                .collect(Collectors.toList());
        }

        return matches.stream().sorted(Comparator.comparing(Match::getDateDebut)).collect(Collectors.toList());
    }

    /**
     * Empêche la modification d'un match complété (lock)
     * Retourne true si le match peut être modifié
     */
    public boolean canModifyMatch(String matchId) {
        Match match = getMatchById(matchId);
        return match.getStatus() != Match.MatchStatus.COMPLETED;
    }

    /**
     * Récupère les matchs complétés d'un événement
     */
    public List<Match> getCompletedMatchesByEvent(String eventId) {
        return matchRepository.findAll()
            .stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED)
            .filter(m -> m.getEventId() != null && m.getEventId().equals(eventId))
            .sorted(Comparator.comparing(Match::getDateDebut).reversed())
            .collect(Collectors.toList());
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    // Méthode privée : applique le DTO sur l'entité
    private void applyRequest(Match match, MatchRequest req, Event event) {
        match.setTeam1Id(req.getTeam1Id());
        match.setTeam2Id(req.getTeam2Id());
        match.setScoreTeam1(req.getScoreTeam1());
        match.setScoreTeam2(req.getScoreTeam2());
        match.setTerrainId(req.getTerrainId());
        match.setDateDebut(req.getDateDebut());
        match.setHeure(req.getHeure());
        // L'événement est la source de vérité pour le sport : on force le sportId
        // du match à celui de l'événement (sinon on retombe sur le sportId demandé).
        match.setSportId(event.getSportId() != null ? event.getSportId() : req.getSportId());
        match.setArbitreId(req.getArbitreId());
        match.setEventId(event.getId());
        if (req.getStatus() != null) match.setStatus(req.getStatus());
        if (req.getType()   != null) match.setType(req.getType());
    }
}