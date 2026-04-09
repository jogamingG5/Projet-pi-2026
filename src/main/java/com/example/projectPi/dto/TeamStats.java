package com.example.projectPi.dto;

public class TeamStats {
    
    private String teamId;
    private int played;      // Nombre de matches joués
    private int wins;        // Victoires
    private int draws;       // Matchs nuls
    private int losses;      // Défaites
    private int goalsFor;    // Buts marqués
    private int goalsAgainst;// Buts concédés
    private int points;      // Points en classement (victoire=3, nul=1, défaite=0)

    public TeamStats() {}

    public TeamStats(String teamId, int played, int wins, int draws, int losses, 
                     int goalsFor, int goalsAgainst, int points) {
        this.teamId = teamId;
        this.played = played;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.points = points;
    }

    // Getters & Setters
    public String getTeamId()           { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public int getPlayed()              { return played; }
    public void setPlayed(int played)   { this.played = played; }

    public int getWins()                { return wins; }
    public void setWins(int wins)       { this.wins = wins; }

    public int getDraws()               { return draws; }
    public void setDraws(int draws)     { this.draws = draws; }

    public int getLosses()              { return losses; }
    public void setLosses(int losses)   { this.losses = losses; }

    public int getGoalsFor()            { return goalsFor; }
    public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }

    public int getGoalsAgainst()        { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }

    public int getPoints()              { return points; }
    public void setPoints(int points)   { this.points = points; }

    // Méthodes calculées
    public double getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public double getWinRate() {
        return played == 0 ? 0 : (double) wins / played * 100;
    }

    public double getAvgGoalsPerGame() {
        return played == 0 ? 0 : (double) goalsFor / played;
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "teamId='" + teamId + '\'' +
                ", played=" + played +
                ", wins=" + wins +
                ", draws=" + draws +
                ", losses=" + losses +
                ", goalsFor=" + goalsFor +
                ", goalsAgainst=" + goalsAgainst +
                ", points=" + points +
                '}';
    }
}
