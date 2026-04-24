package com.example.projectPi.dto;

public class TeamRanking {
    
    private int rank;            // Position au classement
    private String teamId;
    private int points;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private double goalDifference;
    private double winRate;

    public TeamRanking() {}

    public TeamRanking(int rank, TeamStats stats) {
        this.rank = rank;
        this.teamId = stats.getTeamId();
        this.points = stats.getPoints();
        this.played = stats.getPlayed();
        this.wins = stats.getWins();
        this.draws = stats.getDraws();
        this.losses = stats.getLosses();
        this.goalsFor = stats.getGoalsFor();
        this.goalsAgainst = stats.getGoalsAgainst();
        this.goalDifference = stats.getGoalDifference();
        this.winRate = stats.getWinRate();
    }

    // Getters & Setters
    public int getRank()                        { return rank; }
    public void setRank(int rank)               { this.rank = rank; }

    public String getTeamId()                   { return teamId; }
    public void setTeamId(String teamId)        { this.teamId = teamId; }

    public int getPoints()                      { return points; }
    public void setPoints(int points)           { this.points = points; }

    public int getPlayed()                      { return played; }
    public void setPlayed(int played)           { this.played = played; }

    public int getWins()                        { return wins; }
    public void setWins(int wins)               { this.wins = wins; }

    public int getDraws()                       { return draws; }
    public void setDraws(int draws)             { this.draws = draws; }

    public int getLosses()                      { return losses; }
    public void setLosses(int losses)           { this.losses = losses; }

    public int getGoalsFor()                    { return goalsFor; }
    public void setGoalsFor(int goalsFor)       { this.goalsFor = goalsFor; }

    public int getGoalsAgainst()                { return goalsAgainst; }
    public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }

    public double getGoalDifference()           { return goalDifference; }
    public void setGoalDifference(double goalDifference) { this.goalDifference = goalDifference; }

    public double getWinRate()                  { return winRate; }
    public void setWinRate(double winRate)      { this.winRate = winRate; }

    @Override
    public String toString() {
        return "TeamRanking{" +
                "rank=" + rank +
                ", teamId='" + teamId + '\'' +
                ", points=" + points +
                ", played=" + played +
                ", W-D-L=" + wins + "-" + draws + "-" + losses +
                ", GF-GA=" + goalsFor + "-" + goalsAgainst +
                ", GD=" + goalDifference +
                ", WR=" + String.format("%.1f", winRate) + "%" +
                '}';
    }
}
