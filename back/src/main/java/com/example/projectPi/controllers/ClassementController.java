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

import com.example.projectPi.dto.ClassementRequest;
import com.example.projectPi.models.Classement;
import com.example.projectPi.models.Classement.ClassementEntry;
import com.example.projectPi.services.ClassementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/classement")
@Tag(name = "Classement", description = "Gestion des classements des équipes basés sur les performances et statistiques")
public class ClassementController {

    private final ClassementService classementService;

    public ClassementController(ClassementService classementService) {
        this.classementService = classementService;
    }

    // ==================== CRUD OPERATIONS ====================

    @PostMapping
    @Operation(summary = "Créer un classement", description = "Crée un nouveau classement pour un événement ou sport")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Classement créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<Classement> createClassement(
            @Valid @RequestBody ClassementRequest request) {
        Classement createdClassement = classementService.createClassement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClassement);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les classements", description = "Récupère la liste complète des classements")
    @ApiResponse(responseCode = "200", description = "Liste des classements")
    public ResponseEntity<List<Classement>> getAllClassements() {
        List<Classement> classements = classementService.getAllClassements();
        return ResponseEntity.ok(classements);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un classement par ID", description = "Récupère les détails d'un classement spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Classement trouvé"),
        @ApiResponse(responseCode = "404", description = "Classement non trouvé")
    })
    public ResponseEntity<Classement> getClassementById(
            @Parameter(description = "ID du classement")
            @PathVariable String id) {
        Classement classement = classementService.getClassementById(id);
        return ResponseEntity.ok(classement);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Récupérer le classement d'un événement", description = "Récupère le classement associé à un événement spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Classement trouvé"),
        @ApiResponse(responseCode = "404", description = "Classement non trouvé")
    })
    public ResponseEntity<?> getClassementByEventId(
            @Parameter(description = "ID de l'événement")
            @PathVariable String eventId) {
        Optional<Classement> classement = classementService.getClassementByEventId(eventId);
        if (classement.isPresent()) {
            return ResponseEntity.ok(classement.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Classement pour l'événement " + eventId + " non trouvé");
        }
    }

    @GetMapping("/sport/{sportId}")
    @Operation(summary = "Récupérer le classement d'un sport", description = "Récupère le classement associé à un sport spécifique")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Classement trouvé"),
        @ApiResponse(responseCode = "404", description = "Classement non trouvé")
    })
    public ResponseEntity<?> getClassementBySportId(
            @Parameter(description = "ID du sport")
            @PathVariable String sportId) {
        Optional<Classement> classement = classementService.getClassementBySportId(sportId);
        if (classement.isPresent()) {
            return ResponseEntity.ok(classement.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Classement pour le sport " + sportId + " non trouvé");
        }
    }

    @GetMapping("/event/{eventId}/sport/{sportId}")
    @Operation(summary = "Récupérer le classement d'un événement et sport", description = "Récupère le classement spécifique pour un événement et un sport")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Classement trouvé"),
        @ApiResponse(responseCode = "404", description = "Classement non trouvé")
    })
    public ResponseEntity<?> getClassementByEventIdAndSportId(
            @Parameter(description = "ID de l'événement")
            @PathVariable String eventId,
            @Parameter(description = "ID du sport")
            @PathVariable String sportId) {
        Optional<Classement> classement = classementService.getClassementByEventIdAndSportId(eventId, sportId);
        if (classement.isPresent()) {
            return ResponseEntity.ok(classement.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Classement pour l'événement " + eventId + " et le sport " + sportId + " non trouvé");
        }
    }

    @GetMapping("/sport/{sportId}/ordered")
    @Operation(summary = "Récupérer tous les classements d'un sport", description = "Récupère tous les classements pour un sport (ordonnés par date de mise à jour)")
    @ApiResponse(responseCode = "200", description = "Classements trouvés")
    public ResponseEntity<List<Classement>> getClassementsBySportIdOrdered(
            @Parameter(description = "ID du sport")
            @PathVariable String sportId) {
        List<Classement> classements = classementService.getClassementsBySportIdOrdered(sportId);
        return ResponseEntity.ok(classements);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un classement", description = "Modifie les données d'un classement existant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Classement mis à jour"),
        @ApiResponse(responseCode = "404", description = "Classement non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<Classement> updateClassement(
            @Parameter(description = "ID du classement")
            @PathVariable String id,
            @Valid @RequestBody ClassementRequest request) {
        Classement updatedClassement = classementService.updateClassement(id, request);
        return ResponseEntity.ok(updatedClassement);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un classement", description = "Supprime un classement")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Classement supprimé"),
        @ApiResponse(responseCode = "404", description = "Classement non trouvé")
    })
    public ResponseEntity<Void> deleteClassement(
            @Parameter(description = "ID du classement")
            @PathVariable String id) {
        classementService.deleteClassement(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ADVANCED ENDPOINTS ====================

    @PostMapping("/generate")
    @Operation(summary = "Générer un classement", description = "Génère un classement basé sur les statistiques des équipes")
    @ApiResponse(responseCode = "201", description = "Classement généré avec succès")
    public ResponseEntity<Classement> generateClassement(
            @Parameter(description = "ID de l'événement")
            @RequestParam String eventId,
            @Parameter(description = "ID du sport")
            @RequestParam String sportId,
            @Parameter(description = "Liste des IDs des équipes")
            @RequestParam List<String> teamIds) {
        Classement classement = classementService.generateClassementFromStatistiques(eventId, sportId, teamIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(classement);
    }

    @GetMapping("/{classementId}/team/{teamId}/position")
    @Operation(summary = "Récupérer la position d'une équipe", description = "Obtient la position (rang) d'une équipe dans un classement")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Position trouvée"),
        @ApiResponse(responseCode = "404", description = "Équipe non trouvée dans le classement")
    })
    public ResponseEntity<?> getTeamRankingPosition(
            @Parameter(description = "ID du classement")
            @PathVariable String classementId,
            @Parameter(description = "ID de l'équipe")
            @PathVariable String teamId) {
        Optional<ClassementEntry> position = classementService.getTeamRankingPosition(classementId, teamId);
        if (position.isPresent()) {
            return ResponseEntity.ok(position.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("L'équipe " + teamId + " n'a pas été trouvée dans le classement");
        }
    }
}
