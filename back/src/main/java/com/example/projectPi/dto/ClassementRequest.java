package com.example.projectPi.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ClassementRequest", description = "Requête pour créer ou modifier un classement")
public class ClassementRequest {

    @NotBlank(message = "Event ID is required")
    @Schema(description = "ID de l'événement", requiredMode = Schema.RequiredMode.REQUIRED)
    private String eventId;

    @NotBlank(message = "Sport ID is required")
    @Schema(description = "ID du sport", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sportId;

    @NotNull(message = "Classements list is required")
    @Schema(description = "Liste des équipes classées", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ClassementEntryDTO> classements;

    // Constructors
    public ClassementRequest() {}

    public ClassementRequest(String eventId, String sportId, List<ClassementEntryDTO> classements) {
        this.eventId = eventId;
        this.sportId = sportId;
        this.classements = classements;
    }

    // Inner DTO for ranking entry
    @Schema(name = "ClassementEntryDTO", description = "Entrée du classement d'une équipe")
    public static class ClassementEntryDTO {
        @NotBlank(message = "Team ID is required")
        @Schema(description = "ID de l'équipe", requiredMode = Schema.RequiredMode.REQUIRED)
        private String teamId;

        @NotBlank(message = "Team Name is required")
        @Schema(description = "Nom de l'équipe", requiredMode = Schema.RequiredMode.REQUIRED)
        private String teamName;

        @Schema(description = "Rang de l'équipe", example = "1")
        private int rang;

        @Schema(description = "Nombre de matchs joués", example = "10")
        private int matchsJoues;

        @Schema(description = "Nombre de victoires", example = "7")
        private int victoires;

        @Schema(description = "Nombre de défaites", example = "2")
        private int defaites;

        @Schema(description = "Nombre de nuls", example = "1")
        private int nuls;

        @Schema(description = "Points totaux", example = "22")
        private int pointsTotal;

        @Schema(description = "Buts marqués", example = "25")
        private int butsMarques;

        @Schema(description = "Buts encaissés", example = "12")
        private int butsEncaisses;

        @Schema(description = "Différence de buts", example = "13")
        private int differenceButsGoal;

        @Schema(description = "Taux de victoire", example = "70.0")
        private double tauxVictoire;

        // Constructors
        public ClassementEntryDTO() {}

        public ClassementEntryDTO(String teamId, String teamName) {
            this.teamId = teamId;
            this.teamName = teamName;
        }

        // Getters & Setters
        public String getTeamId()                     { return teamId; }
        public void setTeamId(String teamId)          { this.teamId = teamId; }
        public String getTeamName()                   { return teamName; }
        public void setTeamName(String teamName)      { this.teamName = teamName; }
        public int getRang()                          { return rang; }
        public void setRang(int rang)                 { this.rang = rang; }
        public int getMatchsJoues()                   { return matchsJoues; }
        public void setMatchsJoues(int matchsJoues)   { this.matchsJoues = matchsJoues; }
        public int getVictoires()                     { return victoires; }
        public void setVictoires(int victoires)       { this.victoires = victoires; }
        public int getDefaites()                      { return defaites; }
        public void setDefaites(int defaites)         { this.defaites = defaites; }
        public int getNuls()                          { return nuls; }
        public void setNuls(int nuls)                 { this.nuls = nuls; }
        public int getPointsTotal()                   { return pointsTotal; }
        public void setPointsTotal(int pointsTotal)   { this.pointsTotal = pointsTotal; }
        public int getButsMarques()                   { return butsMarques; }
        public void setButsMarques(int butsMarques)   { this.butsMarques = butsMarques; }
        public int getButsEncaisses()                 { return butsEncaisses; }
        public void setButsEncaisses(int butsEncaisses) { this.butsEncaisses = butsEncaisses; }
        public int getDifferenceButsGoal()            { return differenceButsGoal; }
        public void setDifferenceButsGoal(int diff)   { this.differenceButsGoal = diff; }
        public double getTauxVictoire()               { return tauxVictoire; }
        public void setTauxVictoire(double taux)      { this.tauxVictoire = taux; }
    }

    // Getters & Setters
    public String getEventId()                       { return eventId; }
    public void setEventId(String eventId)           { this.eventId = eventId; }
    public String getSportId()                       { return sportId; }
    public void setSportId(String sportId)           { this.sportId = sportId; }
    public List<ClassementEntryDTO> getClassements() { return classements; }
    public void setClassements(List<ClassementEntryDTO> c) { this.classements = c; }
}
