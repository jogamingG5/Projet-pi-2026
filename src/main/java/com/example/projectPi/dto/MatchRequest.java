package com.example.projectPi.dto;

import java.time.LocalDate;

import com.example.projectPi.models.Match.MatchStatus;
import com.example.projectPi.models.Match.MatchType;

public class MatchRequest {

    private String team1Id;
    private String team2Id;
    private int scoreTeam1;
    private int scoreTeam2;
    private String terrainId;
    private LocalDate dateDebut;
    private String heure;
    private String sportId;
    private String arbitreId;
    private String eventId;
    private MatchStatus status;
    private MatchType type;

    // Getters & Setters
    public String getTeam1Id()             { return team1Id; }
    public void   setTeam1Id(String t)     { this.team1Id = t; }
    public String getTeam2Id()             { return team2Id; }
    public void   setTeam2Id(String t)     { this.team2Id = t; }
    public int    getScoreTeam1()          { return scoreTeam1; }
    public void   setScoreTeam1(int s)     { this.scoreTeam1 = s; }
    public int    getScoreTeam2()          { return scoreTeam2; }
    public void   setScoreTeam2(int s)     { this.scoreTeam2 = s; }
    public String getTerrainId()           { return terrainId; }
    public void   setTerrainId(String t)   { this.terrainId = t; }
    public LocalDate getDateDebut()        { return dateDebut; }
    public void   setDateDebut(LocalDate d){ this.dateDebut = d; }
    public String getHeure()               { return heure; }
    public void   setHeure(String h)       { this.heure = h; }
    public String getSportId()             { return sportId; }
    public void   setSportId(String s)     { this.sportId = s; }
    public String getArbitreId()           { return arbitreId; }
    public void   setArbitreId(String a)   { this.arbitreId = a; }
    public String getEventId()             { return eventId; }
    public void   setEventId(String e)     { this.eventId = e; }
    public MatchStatus getStatus()         { return status; }
    public void   setStatus(MatchStatus s) { this.status = s; }
    public MatchType getType()             { return type; }
    public void   setType(MatchType t)     { this.type = t; }
}