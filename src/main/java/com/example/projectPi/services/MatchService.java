package com.example.projectPi.services;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.MatchRequest;
import com.example.projectPi.exception.MatchNotFoundException;
import com.example.projectPi.models.Match;
import com.example.projectPi.repositories.MatchRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match createMatch(MatchRequest request) {
        Match match = new Match();
        applyRequest(match, request);
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    public List<Match> getAllMatchs() {
        return matchRepository.findAll();
    }

    public Match getMatchById(String id) {
        return matchRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException(id));
    }

    public Match updateMatch(String id, MatchRequest request) {
        Match match = getMatchById(id); // lance l'exception si absent
        applyRequest(match, request);
        match.setUpdatedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    public void deleteMatch(String id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchNotFoundException(id);
        }
        matchRepository.deleteById(id);
    }

    // Méthode privée : applique le DTO sur l'entité
    private void applyRequest(Match match, MatchRequest req) {
        match.setTeam1Id(req.getTeam1Id());
        match.setTeam2Id(req.getTeam2Id());
        match.setScoreTeam1(req.getScoreTeam1());
        match.setScoreTeam2(req.getScoreTeam2());
        match.setTerrainId(req.getTerrainId());
        match.setDateDebut(req.getDateDebut());
        match.setHeure(req.getHeure());
        match.setSportId(req.getSportId());
        match.setArbitreId(req.getArbitreId());
        match.setEventId(req.getEventId());
        if (req.getStatus() != null) match.setStatus(req.getStatus());
        if (req.getType()   != null) match.setType(req.getType());
    }
}