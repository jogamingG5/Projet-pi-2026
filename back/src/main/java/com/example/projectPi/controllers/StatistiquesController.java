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

import com.example.projectPi.dto.StatistiquesRequest;
import com.example.projectPi.models.Statistiques;
import com.example.projectPi.services.StatistiquesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/statistiques")
@Tag(name = "Statistiques", description = "Gestion des statistiques des équipes par sport")
public class StatistiquesController {

    private final StatistiquesService statistiquesService;

    public StatistiquesController(StatistiquesService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    // ==================== CRUD OPERATIONS ====================

    @PostMapping
    @Operation(summary = "Créer des statistiques", description = "Crée de nouvelles statistiques pour une équipe et un sport")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Statistiques créées avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<Statistiques> createStatistiques(
            @Valid @RequestBody StatistiquesRequest request) {
        Statistiques createdStatistiques = statistiquesService.createStatistiques(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatistiques);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les statistiques", description = "Récupère la liste complète des statistiques")
    @ApiResponse(responseCode = "200", description = "Liste des statistiques")
    public ResponseEntity<List<Statistiques>> getAllStatistiques() {
        List<Statistiques> statistiques = statistiquesService.getAllStatistiques();
        return ResponseEntity.ok(statistiques);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer les statistiques par ID", description = "Récupère les détails des statistiques d'une équipe")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques trouvées"),
        @ApiResponse(responseCode = "404", description = "Statistiques non trouvées")
    })
    public ResponseEntity<Statistiques> getStatistiquesById(
            @Parameter(description = "ID des statistiques")
            @PathVariable String id) {
        Statistiques statistiques = statistiquesService.getStatistiquesById(id);
        return ResponseEntity.ok(statistiques);
    }

    @GetMapping("/team/{teamId}/sport/{sportId}")
    @Operation(summary = "Récupérer les statistiques d'une équipe pour un sport", description = "Récupère les statistiques spécifiques d'une équipe dans un sport")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques trouvées"),
        @ApiResponse(responseCode = "404", description = "Statistiques non trouvées")
    })
    public ResponseEntity<?> getStatistiquesByTeamAndSport(
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId,
            @Parameter(description = "ID du sport")
            @PathVariable String sportId) {
        Optional<Statistiques> statistiques = statistiquesService.getStatistiquesByTeamAndSport(teamId, sportId);
        if (statistiques.isPresent()) {
            return ResponseEntity.ok(statistiques.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Statistiques pour l'équipe " + teamId + " dans le sport " + sportId + " non trouvées");
        }
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Récupérer toutes les statistiques d'une équipe", description = "Récupère toutes les statistiques pour une équipe (tous les sports)")
    @ApiResponse(responseCode = "200", description = "Statistiques trouvées")
    public ResponseEntity<List<Statistiques>> getStatistiquesByTeam(
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        List<Statistiques> statistiques = statistiquesService.getStatistiquesByTeam(teamId);
        return ResponseEntity.ok(statistiques);
    }

    @GetMapping("/sport/{sportId}")
    @Operation(summary = "Récupérer toutes les statistiques d'un sport", description = "Récupère toutes les statistiques pour un sport (tous les équipes)")
    @ApiResponse(responseCode = "200", description = "Statistiques trouvées")
    public ResponseEntity<List<Statistiques>> getStatistiquesBySport(
            @Parameter(description = "ID du sport")
            @PathVariable String sportId) {
        List<Statistiques> statistiques = statistiquesService.getStatistiquesBySport(sportId);
        return ResponseEntity.ok(statistiques);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour des statistiques", description = "Modifie les données des statistiques")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques mises à jour"),
        @ApiResponse(responseCode = "404", description = "Statistiques non trouvées"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<Statistiques> updateStatistiques(
            @Parameter(description = "ID des statistiques")
            @PathVariable String id,
            @Valid @RequestBody StatistiquesRequest request) {
        Statistiques updatedStatistiques = statistiquesService.updateStatistiques(id, request);
        return ResponseEntity.ok(updatedStatistiques);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer des statistiques", description = "Supprime des statistiques")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Statistiques supprimées"),
        @ApiResponse(responseCode = "404", description = "Statistiques non trouvées")
    })
    public ResponseEntity<Void> deleteStatistiques(
            @Parameter(description = "ID des statistiques")
            @PathVariable String id) {
        statistiquesService.deleteStatistiques(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ADVANCED ENDPOINTS ====================

    @PutMapping("/{id}/victoire")
    @Operation(summary = "Ajouter une victoire", description = "Ajoute une victoire aux statistiques avec les buts marqués et encaissés")
    @ApiResponse(responseCode = "200", description = "Victoire ajoutée")
    public ResponseEntity<Statistiques> addVictoire(
            @Parameter(description = "ID des statistiques")
            @PathVariable String id,
            @Parameter(description = "Buts marqués")
            @RequestParam int buts,
            @Parameter(description = "Buts encaissés")
            @RequestParam int butsEncaisses) {
        Statistiques statistiques = statistiquesService.addVictoire(id, buts, butsEncaisses);
        return ResponseEntity.ok(statistiques);
    }

    @PutMapping("/{id}/defaite")
    @Operation(summary = "Ajouter une défaite", description = "Ajoute une défaite aux statistiques avec les buts marqués et encaissés")
    @ApiResponse(responseCode = "200", description = "Défaite ajoutée")
    public ResponseEntity<Statistiques> addDefaite(
            @Parameter(description = "ID des statistiques")
            @PathVariable String id,
            @Parameter(description = "Buts marqués")
            @RequestParam int buts,
            @Parameter(description = "Buts encaissés")
            @RequestParam int butsEncaisses) {
        Statistiques statistiques = statistiquesService.addDefaite(id, buts, butsEncaisses);
        return ResponseEntity.ok(statistiques);
    }

    @PutMapping("/{id}/nul")
    @Operation(summary = "Ajouter un nul", description = "Ajoute un nul aux statistiques avec les buts marqués")
    @ApiResponse(responseCode = "200", description = "Nul ajouté")
    public ResponseEntity<Statistiques> addNul(
            @Parameter(description = "ID des statistiques")
            @PathVariable String id,
            @Parameter(description = "Buts marqués")
            @RequestParam int buts) {
        Statistiques statistiques = statistiquesService.addNul(id, buts);
        return ResponseEntity.ok(statistiques);
    }
}
