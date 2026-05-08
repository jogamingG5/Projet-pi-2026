package com.example.projectPi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.StatistiquesRequest;
import com.example.projectPi.exception.MatchNotFoundException;
import com.example.projectPi.models.Statistiques;
import com.example.projectPi.repositories.StatistiquesRepository;

@Service
public class StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;

    public StatistiquesService(StatistiquesRepository statistiquesRepository) {
        this.statistiquesRepository = statistiquesRepository;
    }

    /**
     * Crée de nouvelles statistiques pour une équipe et un sport
     */
    public Statistiques createStatistiques(StatistiquesRequest request) {
        Statistiques statistiques = new Statistiques(request.getTeamId(), request.getSportId());
        applyRequest(statistiques, request);
        statistiques.setCreatedAt(LocalDateTime.now());
        statistiques.setUpdatedAt(LocalDateTime.now());
        return statistiquesRepository.save(statistiques);
    }

    /**
     * Récupère toutes les statistiques
     */
    public List<Statistiques> getAllStatistiques() {
        return statistiquesRepository.findAll();
    }

    /**
     * Récupère les statistiques par ID
     */
    public Statistiques getStatistiquesById(String id) {
        return statistiquesRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException("Statistiques avec l'ID " + id + " n'ont pas été trouvées"));
    }

    /**
     * Récupère les statistiques d'une équipe pour un sport
     */
    public Optional<Statistiques> getStatistiquesByTeamAndSport(String teamId, String sportId) {
        return statistiquesRepository.findByTeamIdAndSportId(teamId, sportId);
    }

    /**
     * Récupère toutes les statistiques d'une équipe
     */
    public List<Statistiques> getStatistiquesByTeam(String teamId) {
        return statistiquesRepository.findByTeamId(teamId);
    }

    /**
     * Récupère toutes les statistiques d'un sport
     */
    public List<Statistiques> getStatistiquesBySport(String sportId) {
        return statistiquesRepository.findBySportId(sportId);
    }

    /**
     * Met à jour les statistiques
     */
    public Statistiques updateStatistiques(String id, StatistiquesRequest request) {
        Statistiques statistiques = getStatistiquesById(id);
        applyRequest(statistiques, request);
        statistiques.setUpdatedAt(LocalDateTime.now());
        return statistiquesRepository.save(statistiques);
    }

    /**
     * Supprime les statistiques
     */
    public void deleteStatistiques(String id) {
        if (!statistiquesRepository.existsById(id)) {
            throw new MatchNotFoundException("Statistiques avec l'ID " + id + " n'ont pas été trouvées");
        }
        statistiquesRepository.deleteById(id);
    }

    /**
     * Ajoute une victoire aux statistiques
     */
    public Statistiques addVictoire(String id, int buts, int butsEncaisses) {
        Statistiques statistiques = getStatistiquesById(id);
        statistiques.setNbMatchsJoues(statistiques.getNbMatchsJoues() + 1);
        statistiques.setNbVictoires(statistiques.getNbVictoires() + 1);
        statistiques.setNbButsMarques(statistiques.getNbButsMarques() + buts);
        statistiques.setNbButsEncaisses(statistiques.getNbButsEncaisses() + butsEncaisses);
        statistiques.setUpdatedAt(LocalDateTime.now());
        return statistiquesRepository.save(statistiques);
    }

    /**
     * Ajoute une défaite aux statistiques
     */
    public Statistiques addDefaite(String id, int buts, int butsEncaisses) {
        Statistiques statistiques = getStatistiquesById(id);
        statistiques.setNbMatchsJoues(statistiques.getNbMatchsJoues() + 1);
        statistiques.setNbDefaites(statistiques.getNbDefaites() + 1);
        statistiques.setNbButsMarques(statistiques.getNbButsMarques() + buts);
        statistiques.setNbButsEncaisses(statistiques.getNbButsEncaisses() + butsEncaisses);
        statistiques.setUpdatedAt(LocalDateTime.now());
        return statistiquesRepository.save(statistiques);
    }

    /**
     * Ajoute un nul aux statistiques
     */
    public Statistiques addNul(String id, int buts) {
        Statistiques statistiques = getStatistiquesById(id);
        statistiques.setNbMatchsJoues(statistiques.getNbMatchsJoues() + 1);
        statistiques.setNbNuls(statistiques.getNbNuls() + 1);
        statistiques.setNbButsMarques(statistiques.getNbButsMarques() + buts);
        statistiques.setNbButsEncaisses(statistiques.getNbButsEncaisses() + buts);
        statistiques.setUpdatedAt(LocalDateTime.now());
        return statistiquesRepository.save(statistiques);
    }

    /**
     * Helper method pour appliquer les modifications
     */
    private void applyRequest(Statistiques statistiques, StatistiquesRequest request) {
        if (request.getNbMatchsJoues() > 0) {
            statistiques.setNbMatchsJoues(request.getNbMatchsJoues());
        }
        if (request.getNbVictoires() > 0) {
            statistiques.setNbVictoires(request.getNbVictoires());
        }
        if (request.getNbDefaites() > 0) {
            statistiques.setNbDefaites(request.getNbDefaites());
        }
        if (request.getNbNuls() > 0) {
            statistiques.setNbNuls(request.getNbNuls());
        }
        if (request.getNbButsMarques() > 0) {
            statistiques.setNbButsMarques(request.getNbButsMarques());
        }
        if (request.getNbButsEncaisses() > 0) {
            statistiques.setNbButsEncaisses(request.getNbButsEncaisses());
        }
    }
}
