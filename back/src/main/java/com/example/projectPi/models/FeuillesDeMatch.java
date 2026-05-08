package com.example.projectPi.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FeuillesDeMatch - Représente la feuille de match avec les résultats et statistiques de chaque équipe
 */
@Document(collection = "feuillesDeMatch")
public class FeuillesDeMatch {

    @Id
    private String id;

    private String matchId;

    @JsonProperty("recap")
    private List<RecapEquipe> recap;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public FeuillesDeMatch() {}

    public FeuillesDeMatch(String matchId, List<RecapEquipe> recap) {
        this.matchId = matchId;
        this.recap = recap;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Inner class for team recap
    public static class RecapEquipe {
        private String teamId;
        private int score;
        private List<String> playerbookedYellowCards;
        private List<String> playerbookedRedCards;

        // Constructors
        public RecapEquipe() {}

        public RecapEquipe(String teamId, int score, List<String> yellows, List<String> reds) {
            this.teamId = teamId;
            this.score = score;
            this.playerbookedYellowCards = yellows;
            this.playerbookedRedCards = reds;
        }

        // Getters & Setters
        public String getTeamId()                               { return teamId; }
        public void setTeamId(String teamId)                   { this.teamId = teamId; }
        public int getScore()                                  { return score; }
        public void setScore(int score)                        { this.score = score; }
        public List<String> getPlayerbookedYellowCards()       { return playerbookedYellowCards; }
        public void setPlayerbookedYellowCards(List<String> c) { this.playerbookedYellowCards = c; }
        public List<String> getPlayerbookedRedCards()          { return playerbookedRedCards; }
        public void setPlayerbookedRedCards(List<String> c)    { this.playerbookedRedCards = c; }
    }

    // Getters & Setters
    public String getId()                              { return id; }
    public void setId(String id)                       { this.id = id; }
    public String getMatchId()                         { return matchId; }
    public void setMatchId(String matchId)             { this.matchId = matchId; }
    public List<RecapEquipe> getRecap()               { return recap; }
    public void setRecap(List<RecapEquipe> recap)     { this.recap = recap; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
