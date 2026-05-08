package com.example.projectPi.dto;

/**
 * DTO for match suggestions from the matchmaking engine
 * Contains team pairing and balance metrics
 */
public class MatchSuggestion {
    
    private String teamAId;
    private String teamBId;
    private String teamAName;
    private String teamBName;
    
    /**
     * Balance score: 0-1 where 1 means perfectly balanced
     * Calculation: 1 - (|winRateA - winRateB| / 100)
     */
    private Double balanceScore;
    
    /**
     * Predicted winner team ID based on historical stats
     */
    private String predictedWinnerId;
    
    /**
     * Confidence in prediction: 0-100%
     */
    private Double confidencePercent;
    
    /**
     * Recommendation priority (higher = better matchup)
     */
    private Double priorityScore;
    
    // Constructors
    public MatchSuggestion() {}
    
    public MatchSuggestion(String teamAId, String teamBId, String teamAName, String teamBName,
                          Double balanceScore, String predictedWinnerId, Double confidencePercent,
                          Double priorityScore) {
        this.teamAId = teamAId;
        this.teamBId = teamBId;
        this.teamAName = teamAName;
        this.teamBName = teamBName;
        this.balanceScore = balanceScore;
        this.predictedWinnerId = predictedWinnerId;
        this.confidencePercent = confidencePercent;
        this.priorityScore = priorityScore;
    }
    
    // Getters & Setters
    public String getTeamAId()              { return teamAId; }
    public void setTeamAId(String id)       { this.teamAId = id; }
    
    public String getTeamBId()              { return teamBId; }
    public void setTeamBId(String id)       { this.teamBId = id; }
    
    public String getTeamAName()            { return teamAName; }
    public void setTeamAName(String name)   { this.teamAName = name; }
    
    public String getTeamBName()            { return teamBName; }
    public void setTeamBName(String name)   { this.teamBName = name; }
    
    public Double getBalanceScore()         { return balanceScore; }
    public void setBalanceScore(Double score) { this.balanceScore = score; }
    
    public String getPredictedWinnerId()    { return predictedWinnerId; }
    public void setPredictedWinnerId(String id) { this.predictedWinnerId = id; }
    
    public Double getConfidencePercent()    { return confidencePercent; }
    public void setConfidencePercent(Double conf) { this.confidencePercent = conf; }
    
    public Double getPriorityScore()        { return priorityScore; }
    public void setPriorityScore(Double score) { this.priorityScore = score; }
}
