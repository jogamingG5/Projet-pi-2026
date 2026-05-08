package com.example.projectPi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "StatistiquesRequest", description = "Requête pour créer ou modifier des statistiques")
public class StatistiquesRequest {

    @NotBlank(message = "Team ID is required")
    @Schema(description = "ID de l'équipe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String teamId;

    @NotBlank(message = "Sport ID is required")
    @Schema(description = "ID du sport", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sportId;

    @Schema(description = "Nombre de matchs joués", example = "10")
    private int nbMatchsJoues;

    @Schema(description = "Nombre de victoires", example = "7")
    private int nbVictoires;

    @Schema(description = "Nombre de défaites", example = "2")
    private int nbDefaites;

    @Schema(description = "Nombre de nuls", example = "1")
    private int nbNuls;

    @Schema(description = "Nombre de buts marqués", example = "25")
    private int nbButsMarques;

    @Schema(description = "Nombre de buts encaissés", example = "12")
    private int nbButsEncaisses;

    // Constructors
    public StatistiquesRequest() {}

    public StatistiquesRequest(String teamId, String sportId) {
        this.teamId = teamId;
        this.sportId = sportId;
    }

    // Getters & Setters
    public String getTeamId()                      { return teamId; }
    public void setTeamId(String teamId)           { this.teamId = teamId; }
    public String getSportId()                     { return sportId; }
    public void setSportId(String sportId)         { this.sportId = sportId; }
    public int getNbMatchsJoues()                  { return nbMatchsJoues; }
    public void setNbMatchsJoues(int n)            { this.nbMatchsJoues = n; }
    public int getNbVictoires()                    { return nbVictoires; }
    public void setNbVictoires(int n)              { this.nbVictoires = n; }
    public int getNbDefaites()                     { return nbDefaites; }
    public void setNbDefaites(int n)               { this.nbDefaites = n; }
    public int getNbNuls()                         { return nbNuls; }
    public void setNbNuls(int n)                   { this.nbNuls = n; }
    public int getNbButsMarques()                  { return nbButsMarques; }
    public void setNbButsMarques(int n)            { this.nbButsMarques = n; }
    public int getNbButsEncaisses()                { return nbButsEncaisses; }
    public void setNbButsEncaisses(int n)          { this.nbButsEncaisses = n; }
}
