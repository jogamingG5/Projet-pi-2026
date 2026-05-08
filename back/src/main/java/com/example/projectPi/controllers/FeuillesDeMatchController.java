package com.example.projectPi.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectPi.dto.FeuillesDeMatchRequest;
import com.example.projectPi.models.FeuillesDeMatch;
import com.example.projectPi.services.FeuillesDeMatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feuillesDeMatch")
@Tag(name = "Feuilles de Match", description = "Gestion des feuilles de match avec résultats et statistiques par équipe")
public class FeuillesDeMatchController {

    private final FeuillesDeMatchService feuillesDeMatchService;

    public FeuillesDeMatchController(FeuillesDeMatchService feuillesDeMatchService) {
        this.feuillesDeMatchService = feuillesDeMatchService;
    }

    // ==================== CRUD OPERATIONS ====================

    @PostMapping
    @Operation(summary = "Créer une feuille de match", description = "Crée une nouvelle feuille de match")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Feuille de match créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<FeuillesDeMatch> createFeuillesDeMatch(
            @Valid @RequestBody FeuillesDeMatchRequest request) {
        FeuillesDeMatch createdFeuillesDeMatch = feuillesDeMatchService.createFeuillesDeMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFeuillesDeMatch);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les feuilles de match", description = "Récupère la liste complète des feuilles de match")
    @ApiResponse(responseCode = "200", description = "Liste des feuilles de match")
    public ResponseEntity<List<FeuillesDeMatch>> getAllFeuillesDeMatch() {
        List<FeuillesDeMatch> feuillesDeMatch = feuillesDeMatchService.getAllFeuillesDeMatch();
        return ResponseEntity.ok(feuillesDeMatch);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une feuille de match par ID", description = "Récupère les détails d'une feuille de match spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feuille de match trouvée"),
        @ApiResponse(responseCode = "404", description = "Feuille de match non trouvée")
    })
    public ResponseEntity<FeuillesDeMatch> getFeuillesDeMatchById(
            @Parameter(description = "ID de la feuille de match")
            @PathVariable String id) {
        FeuillesDeMatch feuillesDeMatch = feuillesDeMatchService.getFeuillesDeMatchById(id);
        return ResponseEntity.ok(feuillesDeMatch);
    }

    @GetMapping("/match/{matchId}")
    @Operation(summary = "Récupérer la feuille de match par Match ID", description = "Récupère la feuille de match associée à un match spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feuille de match trouvée"),
        @ApiResponse(responseCode = "404", description = "Feuille de match non trouvée")
    })
    public ResponseEntity<?> getFeuillesDeMatchByMatchId(
            @Parameter(description = "ID du match")
            @PathVariable String matchId) {
        Optional<FeuillesDeMatch> feuillesDeMatch = feuillesDeMatchService.getFeuillesDeMatchByMatchId(matchId);
        if (feuillesDeMatch.isPresent()) {
            return ResponseEntity.ok(feuillesDeMatch.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Feuille de match pour le match " + matchId + " non trouvée");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une feuille de match", description = "Modifie les données d'une feuille de match existante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feuille de match mise à jour"),
        @ApiResponse(responseCode = "404", description = "Feuille de match non trouvée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<FeuillesDeMatch> updateFeuillesDeMatch(
            @Parameter(description = "ID de la feuille de match")
            @PathVariable String id,
            @Valid @RequestBody FeuillesDeMatchRequest request) {
        FeuillesDeMatch updatedFeuillesDeMatch = feuillesDeMatchService.updateFeuillesDeMatch(id, request);
        return ResponseEntity.ok(updatedFeuillesDeMatch);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une feuille de match", description = "Supprime une feuille de match")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Feuille de match supprimée"),
        @ApiResponse(responseCode = "404", description = "Feuille de match non trouvée")
    })
    public ResponseEntity<Void> deleteFeuillesDeMatch(
            @Parameter(description = "ID de la feuille de match")
            @PathVariable String id) {
        feuillesDeMatchService.deleteFeuillesDeMatch(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ADVANCED ENDPOINTS ====================

    @GetMapping("/{id}/team/{teamId}/score")
    @Operation(summary = "Récupérer le score d'une équipe", description = "Obtient le score total d'une équipe dans une feuille de match")
    @ApiResponse(responseCode = "200", description = "Score récupéré")
    public ResponseEntity<Integer> getTeamScore(
            @Parameter(description = "ID de la feuille de match")
            @PathVariable String id,
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        int score = feuillesDeMatchService.getTeamScoreInMatch(id, teamId);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/{id}/team/{teamId}/yellow-cards")
    @Operation(summary = "Récupérer les cartons jaunes d'une équipe", description = "Obtient le nombre de cartons jaunes reçus par une équipe")
    @ApiResponse(responseCode = "200", description = "Cartons jaunes récupérés")
    public ResponseEntity<Integer> getTeamYellowCards(
            @Parameter(description = "ID de la feuille de match")
            @PathVariable String id,
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        int yellowCards = feuillesDeMatchService.getTeamYellowCardsCount(id, teamId);
        return ResponseEntity.ok(yellowCards);
    }

    @GetMapping("/{id}/team/{teamId}/red-cards")
    @Operation(summary = "Récupérer les cartons rouges d'une équipe", description = "Obtient le nombre de cartons rouges reçus par une équipe")
    @ApiResponse(responseCode = "200", description = "Cartons rouges récupérés")
    public ResponseEntity<Integer> getTeamRedCards(
            @Parameter(description = "ID de la feuille de match")
            @PathVariable String id,
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        int redCards = feuillesDeMatchService.getTeamRedCardsCount(id, teamId);
        return ResponseEntity.ok(redCards);
    }
}
