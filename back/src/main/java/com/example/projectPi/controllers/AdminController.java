package com.example.projectPi.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectPi.config.DataSeeder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints d'administration pour les données de démonstration.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Outils d'administration (reseed des données de démo)")
public class AdminController {

    private final DataSeeder dataSeeder;

    public AdminController(DataSeeder dataSeeder) {
        this.dataSeeder = dataSeeder;
    }

    @PostMapping("/reseed")
    @Operation(summary = "Réinitialiser les données de démo",
               description = "Vide les collections (events, matchs, statistiques, classement, feuillesDeMatch) "
                   + "puis régénère des équipes tunisiennes + grands championnats européens + Ligues des Champions "
                   + "Europe/Afrique, avec le lien événement obligatoire et la cascade complète.")
    public ResponseEntity<Map<String, String>> reseed() {
        String summary = dataSeeder.reseed();
        return ResponseEntity.ok(Map.of("status", "ok", "message", summary));
    }
}
