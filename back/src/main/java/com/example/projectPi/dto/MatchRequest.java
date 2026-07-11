package com.example.projectPi.dto;

import java.time.LocalDate;

import com.example.projectPi.models.Match.MatchStatus;
import com.example.projectPi.models.Match.MatchType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(name = "MatchRequest", description = "Requête pour créer ou modifier un match")
public class MatchRequest {

    @NotBlank(message = "Team 1 ID is required")
    @Schema(description = "ID de la première équipe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String team1Id;
    
    @NotBlank(message = "Team 2 ID is required")
    @Schema(description = "ID de la deuxième équipe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String team2Id;
    
    @Min(value = 0, message = "Team 1 score cannot be negative")
    @Max(value = 999, message = "Team 1 score is too high")
    @Schema(description = "Score de l'équipe 1", example = "2")
    private int scoreTeam1;
    
    @Min(value = 0, message = "Team 2 score cannot be negative")
    @Max(value = 999, message = "Team 2 score is too high")
    @Schema(description = "Score de l'équipe 2", example = "1")
    private int scoreTeam2;
    
    @NotBlank(message = "Terrain ID is required")
    @Schema(description = "ID du terrain", requiredMode = Schema.RequiredMode.REQUIRED)
    private String terrainId;
    
    @NotNull(message = "Match date is required")
    @Schema(description = "Date du match", example = "2026-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;
    
    @NotBlank(message = "Match time is required")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", 
             message = "Time must be in HH:mm format")
    @Schema(description = "Heure du match", example = "15:30", requiredMode = Schema.RequiredMode.REQUIRED)
    private String heure;
    
    @NotBlank(message = "Sport ID is required")
    @Schema(description = "ID du sport", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sportId;
    
    @NotBlank(message = "Referee ID is required")
    @Schema(description = "ID de l'arbitre", requiredMode = Schema.RequiredMode.REQUIRED)
    private String arbitreId;
    
    @NotBlank(message = "Event ID is required")
    @Schema(description = "ID de l'événement", requiredMode = Schema.RequiredMode.REQUIRED)
    private String eventId;
    
    @NotNull(message = "Match status is required")
    @Schema(description = "Statut du match", requiredMode = Schema.RequiredMode.REQUIRED)
    private MatchStatus status;
    
    @NotNull(message = "Match type is required")
    @Schema(description = "Type du match", requiredMode = Schema.RequiredMode.REQUIRED)
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