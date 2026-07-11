package com.example.projectPi.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.projectPi.models.Event;
import com.example.projectPi.models.FeuillesDeMatch;
import com.example.projectPi.models.FeuillesDeMatch.RecapEquipe;
import com.example.projectPi.models.Match;
import com.example.projectPi.models.Statistiques;
import com.example.projectPi.repositories.EventRepository;
import com.example.projectPi.repositories.FeuillesDeMatchRepository;
import com.example.projectPi.repositories.MatchRepository;
import com.example.projectPi.repositories.StatistiquesRepository;

/**
 * MatchResultProcessor - propage le résultat d'un match dans le reste du graphe
 * domaine : Statistiques (par équipe+sport), Classement (par event+sport) et
 * FeuillesDeMatch (par match).
 *
 * <p>Stratégie idempotente : les statistiques sont <b>recalculées à partir de
 * zéro</b> depuis tous les matchs COMPLETED de l'équipe (pas d'incrément), donc
 * rejouer/éditer un match ne double jamais les compteurs.
 */
@Service
public class MatchResultProcessor {

    private final MatchRepository matchRepository;
    private final StatistiquesRepository statistiquesRepository;
    private final FeuillesDeMatchRepository feuillesDeMatchRepository;
    private final ClassementService classementService;
    private final EventRepository eventRepository;

    public MatchResultProcessor(MatchRepository matchRepository,
                                StatistiquesRepository statistiquesRepository,
                                FeuillesDeMatchRepository feuillesDeMatchRepository,
                                ClassementService classementService,
                                EventRepository eventRepository) {
        this.matchRepository = matchRepository;
        this.statistiquesRepository = statistiquesRepository;
        this.feuillesDeMatchRepository = feuillesDeMatchRepository;
        this.classementService = classementService;
        this.eventRepository = eventRepository;
    }

    /**
     * Traite un match terminé : recalcule les stats des deux équipes, écrit la
     * feuille de match et régénère le classement de l'événement.
     */
    public void processCompletedMatch(Match match) {
        reprocessTeams(match);
        upsertFeuilleDeMatch(match);
    }

    /**
     * Recalcule les statistiques (et le classement) des deux équipes du match,
     * quel que soit le statut. Utilisé sur toute mise à jour pour retirer la
     * contribution d'un match repassé hors COMPLETED. N'écrit pas la feuille.
     */
    public void reprocessTeams(Match match) {
        recomputeTeamStatistiques(match.getTeam1Id(), match.getSportId());
        recomputeTeamStatistiques(match.getTeam2Id(), match.getSportId());
        regenerateClassement(match);
    }

    /**
     * Recalcule les statistiques d'une équipe pour un sport à partir de tous ses
     * matchs COMPLETED, puis upsert le document Statistiques.
     */
    private void recomputeTeamStatistiques(String teamId, String sportId) {
        if (teamId == null || sportId == null) {
            return;
        }

        List<Match> completed = matchRepository.findAll().stream()
            .filter(m -> m.getStatus() == Match.MatchStatus.COMPLETED)
            .filter(m -> sportId.equals(m.getSportId()))
            .filter(m -> teamId.equals(m.getTeam1Id()) || teamId.equals(m.getTeam2Id()))
            .collect(Collectors.toList());

        int played = completed.size();
        int wins = 0;
        int draws = 0;
        int losses = 0;
        int goalsFor = 0;
        int goalsAgainst = 0;

        for (Match m : completed) {
            boolean isTeam1 = teamId.equals(m.getTeam1Id());
            int scoreFor = isTeam1 ? m.getScoreTeam1() : m.getScoreTeam2();
            int scoreAgainst = isTeam1 ? m.getScoreTeam2() : m.getScoreTeam1();

            goalsFor += scoreFor;
            goalsAgainst += scoreAgainst;

            if (scoreFor > scoreAgainst) {
                wins++;
            } else if (scoreFor == scoreAgainst) {
                draws++;
            } else {
                losses++;
            }
        }

        Statistiques stats = statistiquesRepository.findByTeamIdAndSportId(teamId, sportId)
            .orElseGet(() -> new Statistiques(teamId, sportId));
        stats.setNbMatchsJoues(played);
        stats.setNbVictoires(wins);
        stats.setNbNuls(draws);
        stats.setNbDefaites(losses);
        stats.setNbButsMarques(goalsFor);
        stats.setNbButsEncaisses(goalsAgainst);
        stats.setUpdatedAt(LocalDateTime.now());
        statistiquesRepository.save(stats);
    }

    /**
     * Upsert la feuille de match (une par matchId) avec le récapitulatif des
     * deux équipes (score depuis le match ; listes de cartons vides).
     */
    private void upsertFeuilleDeMatch(Match match) {
        RecapEquipe recap1 = new RecapEquipe(
            match.getTeam1Id(), match.getScoreTeam1(), new ArrayList<>(), new ArrayList<>());
        RecapEquipe recap2 = new RecapEquipe(
            match.getTeam2Id(), match.getScoreTeam2(), new ArrayList<>(), new ArrayList<>());

        FeuillesDeMatch feuille = feuillesDeMatchRepository.findByMatchId(match.getId())
            .orElseGet(FeuillesDeMatch::new);
        feuille.setMatchId(match.getId());
        feuille.setRecap(new ArrayList<>(List.of(recap1, recap2)));
        feuille.setUpdatedAt(LocalDateTime.now());
        feuillesDeMatchRepository.save(feuille);
    }

    /**
     * Régénère le classement de l'événement+sport à partir des statistiques
     * mises à jour. Utilise le roster de l'événement, à défaut les deux équipes.
     */
    private void regenerateClassement(Match match) {
        String eventId = match.getEventId();
        String sportId = match.getSportId();
        if (eventId == null || sportId == null) {
            return;
        }

        List<String> teamIds = eventRepository.findById(eventId)
            .map(Event::getTeamsIds)
            .filter(ids -> ids != null && !ids.isEmpty())
            .orElseGet(() -> new ArrayList<>(List.of(match.getTeam1Id(), match.getTeam2Id())));

        classementService.generateClassementFromStatistiques(eventId, sportId, teamIds);
    }
}
