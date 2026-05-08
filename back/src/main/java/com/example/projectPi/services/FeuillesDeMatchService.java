package com.example.projectPi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.FeuillesDeMatchRequest;
import com.example.projectPi.exception.MatchNotFoundException;
import com.example.projectPi.models.FeuillesDeMatch;
import com.example.projectPi.repositories.FeuillesDeMatchRepository;

@Service
public class FeuillesDeMatchService {

    private final FeuillesDeMatchRepository feuillesDeMatchRepository;

    public FeuillesDeMatchService(FeuillesDeMatchRepository feuillesDeMatchRepository) {
        this.feuillesDeMatchRepository = feuillesDeMatchRepository;
    }

    /**
     * Crée une nouvelle feuille de match
     */
    public FeuillesDeMatch createFeuillesDeMatch(FeuillesDeMatchRequest request) {
        FeuillesDeMatch feuillesDeMatch = new FeuillesDeMatch();
        feuillesDeMatch.setMatchId(request.getMatchId());

        List<FeuillesDeMatch.RecapEquipe> recap = request.getRecap().stream()
            .map(dto -> new FeuillesDeMatch.RecapEquipe(
                dto.getTeamId(),
                dto.getScore(),
                dto.getPlayerbookedYellowCards(),
                dto.getPlayerbookedRedCards()
            ))
            .toList();

        feuillesDeMatch.setRecap(recap);
        feuillesDeMatch.setCreatedAt(LocalDateTime.now());
        feuillesDeMatch.setUpdatedAt(LocalDateTime.now());

        return feuillesDeMatchRepository.save(feuillesDeMatch);
    }

    /**
     * Récupère tous les feuillesDeMatch
     */
    public List<FeuillesDeMatch> getAllFeuillesDeMatch() {
        return feuillesDeMatchRepository.findAll();
    }

    /**
     * Récupère une feuille de match par son ID
     */
    public FeuillesDeMatch getFeuillesDeMatchById(String id) {
        return feuillesDeMatchRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException("Feuille de match avec l'ID " + id + " n'a pas été trouvée"));
    }

    /**
     * Récupère une feuille de match par le Match ID
     */
    public Optional<FeuillesDeMatch> getFeuillesDeMatchByMatchId(String matchId) {
        return feuillesDeMatchRepository.findByMatchId(matchId);
    }

    /**
     * Met à jour une feuille de match
     */
    public FeuillesDeMatch updateFeuillesDeMatch(String id, FeuillesDeMatchRequest request) {
        FeuillesDeMatch feuillesDeMatch = getFeuillesDeMatchById(id);

        feuillesDeMatch.setMatchId(request.getMatchId());

        List<FeuillesDeMatch.RecapEquipe> recap = request.getRecap().stream()
            .map(dto -> new FeuillesDeMatch.RecapEquipe(
                dto.getTeamId(),
                dto.getScore(),
                dto.getPlayerbookedYellowCards(),
                dto.getPlayerbookedRedCards()
            ))
            .toList();

        feuillesDeMatch.setRecap(recap);
        feuillesDeMatch.setUpdatedAt(LocalDateTime.now());

        return feuillesDeMatchRepository.save(feuillesDeMatch);
    }

    /**
     * Supprime une feuille de match
     */
    public void deleteFeuillesDeMatch(String id) {
        if (!feuillesDeMatchRepository.existsById(id)) {
            throw new MatchNotFoundException("Feuille de match avec l'ID " + id + " n'a pas été trouvée");
        }
        feuillesDeMatchRepository.deleteById(id);
    }

    /**
     * Récupère le score total d'une équipe dans une feuille de match
     */
    public int getTeamScoreInMatch(String feuillesDeMatchId, String teamId) {
        FeuillesDeMatch feuillesDeMatch = getFeuillesDeMatchById(feuillesDeMatchId);
        return feuillesDeMatch.getRecap().stream()
            .filter(recap -> recap.getTeamId().equals(teamId))
            .mapToInt(FeuillesDeMatch.RecapEquipe::getScore)
            .sum();
    }

    /**
     * Récupère le nombre de cartons jaunes d'une équipe
     */
    public int getTeamYellowCardsCount(String feuillesDeMatchId, String teamId) {
        FeuillesDeMatch feuillesDeMatch = getFeuillesDeMatchById(feuillesDeMatchId);
        return feuillesDeMatch.getRecap().stream()
            .filter(recap -> recap.getTeamId().equals(teamId))
            .mapToInt(recap -> recap.getPlayerbookedYellowCards() != null ?
                recap.getPlayerbookedYellowCards().size() : 0)
            .sum();
    }

    /**
     * Récupère le nombre de cartons rouges d'une équipe
     */
    public int getTeamRedCardsCount(String feuillesDeMatchId, String teamId) {
        FeuillesDeMatch feuillesDeMatch = getFeuillesDeMatchById(feuillesDeMatchId);
        return feuillesDeMatch.getRecap().stream()
            .filter(recap -> recap.getTeamId().equals(teamId))
            .mapToInt(recap -> recap.getPlayerbookedRedCards() != null ?
                recap.getPlayerbookedRedCards().size() : 0)
            .sum();
    }
}
