package com.example.projectPi.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.MatchSuggestion;
import com.example.projectPi.models.Match;
import com.example.projectPi.repositories.MatchRepository;

/**
 * Service for generating balanced match suggestions using Elo-like algorithm
 */
@Service
public class MatchmakingService {
    
    private final MatchRepository matchRepository;
    
    public MatchmakingService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
    
    /**
     * Suggest balanced matchups for an event and sport
     * 
     * @param eventId Event identifier
     * @param sportId Sport type
     * @param round Round number (optional, for round-robin format)
     * @param format "roundrobin" or "suisse"
     * @return List of match suggestions sorted by balance score
     */
    public List<MatchSuggestion> suggestMatchups(String eventId, String sportId, 
                                                  Integer round, String format) {
        // Fetch existing matches to exclude already-played pairs
        List<Match> existingMatches = matchRepository.findAll().stream()
            .filter(m -> m.getEventId().equals(eventId) && m.getSportId().equals(sportId))
            .collect(Collectors.toList());
        
        Set<String> existingPairs = new HashSet<>();
        for (Match match : existingMatches) {
            String pairKey = getPairKey(match.getTeam1Id(), match.getTeam2Id());
            existingPairs.add(pairKey);
        }
        
        // Generate all possible team pairs with balance calculations
        List<MatchSuggestion> suggestions = generatePairings(eventId, sportId, 
                                                             existingPairs, round);
        
        // Sort by balance score (descending) and priority
        suggestions.sort((a, b) -> {
            int balanceCompare = b.getBalanceScore().compareTo(a.getBalanceScore());
            if (balanceCompare != 0) return balanceCompare;
            return b.getPriorityScore().compareTo(a.getPriorityScore());
        });
        
        return suggestions;
    }
    
    /**
     * Generate all possible team pairings with balance metrics
     */
    private List<MatchSuggestion> generatePairings(String eventId, String sportId,
                                                    Set<String> existingPairs,
                                                    Integer round) {
        List<MatchSuggestion> suggestions = new ArrayList<>();
        
        // In real implementation, fetch team list from EventRepository or Cache
        // For now, using mock list - would be replaced with actual DB queries
        List<String> teamIds = getTeamIds(eventId);
        
        Map<String, TeamStats> teamStatsMap = new HashMap<>();
        for (String teamId : teamIds) {
            teamStatsMap.put(teamId, calculateTeamStats(teamId, eventId, sportId));
        }
        
        // Generate all unique pairs
        for (int i = 0; i < teamIds.size(); i++) {
            for (int j = i + 1; j < teamIds.size(); j++) {
                String teamAId = teamIds.get(i);
                String teamBId = teamIds.get(j);
                
                String pairKey = getPairKey(teamAId, teamBId);
                if (existingPairs.contains(pairKey)) {
                    continue; // Skip already played pairs
                }
                
                MatchSuggestion suggestion = calculateMatchSuggestion(
                    teamAId, teamBId,
                    teamStatsMap.get(teamAId),
                    teamStatsMap.get(teamBId)
                );
                
                suggestions.add(suggestion);
            }
        }
        
        return suggestions;
    }
    
    /**
     * Calculate balance metrics for a specific match suggestion
     */
    private MatchSuggestion calculateMatchSuggestion(String teamAId, String teamBId,
                                                     TeamStats statsA, TeamStats statsB) {
        MatchSuggestion suggestion = new MatchSuggestion();
        suggestion.setTeamAId(teamAId);
        suggestion.setTeamBId(teamBId);
        suggestion.setTeamAName(statsA.teamName);
        suggestion.setTeamBName(statsB.teamName);
        
        // Calculate balance score: 1 - (|winRateA - winRateB| / 100)
        double winRateDiff = Math.abs(statsA.winRate - statsB.winRate);
        double balanceScore = Math.max(0, 1.0 - (winRateDiff / 100.0));
        suggestion.setBalanceScore(balanceScore);
        
        // Predict winner based on composite score
        String predictedWinner = predictWinner(teamAId, teamBId, statsA, statsB);
        suggestion.setPredictedWinnerId(predictedWinner);
        
        // Calculate confidence in prediction
        double confidence = calculateConfidence(statsA, statsB);
        suggestion.setConfidencePercent(confidence);
        
        // Priority = (balance score * 0.6) + (competitive level * 0.4)
        double competitiveLevel = 1.0 - Math.abs(statsA.eloRating - statsB.eloRating) / 3000.0;
        double priority = (balanceScore * 0.6) + (Math.max(0, competitiveLevel) * 0.4);
        suggestion.setPriorityScore(priority);
        
        return suggestion;
    }
    
    /**
     * Predict winner using weighted statistics
     * Weight: Win Rate (40%) + Goal Differential (35%) + Points Total (25%)
     */
    private String predictWinner(String teamAId, String teamBId,
                                  TeamStats statsA, TeamStats statsB) {
        double scoreA = (statsA.winRate * 0.40) + 
                       (statsA.avgGoalDiff * 0.35) + 
                       (statsA.totalPoints * 0.25);
        
        double scoreB = (statsB.winRate * 0.40) + 
                       (statsB.avgGoalDiff * 0.35) + 
                       (statsB.totalPoints * 0.25);
        
        return scoreA > scoreB ? teamAId : teamBId;
    }
    
    /**
     * Calculate confidence percentage based on statistical variance
     */
    private double calculateConfidence(TeamStats statsA, TeamStats statsB) {
        // Higher confidence when win rates are more different (clearer winner)
        // But lower confidence when teams are very unbalanced
        double winRateDiff = Math.abs(statsA.winRate - statsB.winRate);
        double balance = 1.0 - (winRateDiff / 100.0);
        
        // Confidence: 50-95% based on statistical strength
        double baseConfidence = 50 + (winRateDiff * 0.45);
        return Math.min(95, baseConfidence);
    }
    
    /**
     * Calculate statistics for a specific team
     */
    private TeamStats calculateTeamStats(String teamId, String eventId, String sportId) {
        TeamStats stats = new TeamStats();
        stats.teamId = teamId;
        
        // Fetch team name, calculate win rate, goals, etc.
        // This would query actual repositories in production
        List<Match> teamMatches = matchRepository.findAll().stream()
            .filter(m -> m.getEventId().equals(eventId) && m.getSportId().equals(sportId) &&
                    (m.getTeam1Id().equals(teamId) || m.getTeam2Id().equals(teamId)))
            .collect(Collectors.toList());
        
        if (teamMatches.isEmpty()) {
            stats.winRate = 50.0;
            stats.totalPoints = 0;
            stats.avgGoalDiff = 0.0;
            stats.eloRating = 1500.0; // Default Elo
            return stats;
        }
        
        int wins = 0;
        int draws = 0;
        int totalGoalsFor = 0;
        int totalGoalsAgainst = 0;
        
        for (Match match : teamMatches) {
            boolean isTeamA = match.getTeam1Id().equals(teamId);
            int goalsFor = isTeamA ? match.getScoreTeam1() : match.getScoreTeam2();
            int goalsAgainst = isTeamA ? match.getScoreTeam2() : match.getScoreTeam1();
            
            totalGoalsFor += goalsFor;
            totalGoalsAgainst += goalsAgainst;
            
            if (goalsFor > goalsAgainst) {
                wins++;
            } else if (goalsFor == goalsAgainst) {
                draws++;
            }
        }
        
        stats.winRate = (wins * 100.0) / teamMatches.size();
        stats.totalPoints = (wins * 3) + draws;
        stats.avgGoalDiff = ((double) (totalGoalsFor - totalGoalsAgainst)) / teamMatches.size();
        stats.eloRating = 1500.0 + (stats.winRate - 50.0) * 10; // Simplified Elo
        
        return stats;
    }
    
    /**
     * Get unique pair key for checking duplicates
     */
    private String getPairKey(String teamA, String teamB) {
        List<String> pair = Arrays.asList(teamA, teamB);
        Collections.sort(pair);
        return pair.get(0) + "-" + pair.get(1);
    }
    
    /**
     * Fetch team IDs for an event (stub - would query EventRepository)
     */
    private List<String> getTeamIds(String eventId) {
        // In production: query EventRepository.findById(eventId).getTeamsIds()
        // For now returning empty list - integration will fill this
        return new ArrayList<>();
    }
    
    /**
     * Internal class for team statistics
     */
    private static class TeamStats {
        String teamId;
        String teamName;
        double winRate;
        int totalPoints;
        double avgGoalDiff;
        double eloRating;
    }
}
