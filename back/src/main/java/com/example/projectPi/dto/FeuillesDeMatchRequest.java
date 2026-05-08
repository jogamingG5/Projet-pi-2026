package com.example.projectPi.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FeuillesDeMatchRequest", description = "Requête pour créer ou modifier une feuille de match")
public class FeuillesDeMatchRequest {

    @NotBlank(message = "Match ID is required")
    @Schema(description = "ID du match", requiredMode = Schema.RequiredMode.REQUIRED)
    private String matchId;

    @NotNull(message = "Recap list is required")
    @Schema(description = "Récapitulatif par équipe", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RecapEquipeDTO> recap;

    // Constructors
    public FeuillesDeMatchRequest() {}

    public FeuillesDeMatchRequest(String matchId, List<RecapEquipeDTO> recap) {
        this.matchId = matchId;
        this.recap = recap;
    }

    // Inner DTO for team recap
    @Schema(name = "RecapEquipeDTO", description = "Récapitulatif d'une équipe dans un match")
    public static class RecapEquipeDTO {
        @NotBlank(message = "Team ID is required")
        @Schema(description = "ID de l'équipe", requiredMode = Schema.RequiredMode.REQUIRED)
        private String teamId;

        @Min(value = 0, message = "Score cannot be negative")
        @Schema(description = "Score de l'équipe", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        private int score;

        @Schema(description = "IDs des joueurs ayant reçu un carton jaune")
        private List<String> playerbookedYellowCards;

        @Schema(description = "IDs des joueurs ayant reçu un carton rouge")
        private List<String> playerbookedRedCards;

        // Constructors
        public RecapEquipeDTO() {}

        public RecapEquipeDTO(String teamId, int score, List<String> yellows, List<String> reds) {
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
    public String getMatchId()              { return matchId; }
    public void setMatchId(String matchId)  { this.matchId = matchId; }
    public List<RecapEquipeDTO> getRecap() { return recap; }
    public void setRecap(List<RecapEquipeDTO> recap) { this.recap = recap; }
}
