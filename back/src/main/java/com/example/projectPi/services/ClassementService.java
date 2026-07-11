package com.example.projectPi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.projectPi.dto.ClassementRequest;
import com.example.projectPi.exception.MatchNotFoundException;
import com.example.projectPi.models.Classement;
import com.example.projectPi.models.Classement.ClassementEntry;
import com.example.projectPi.repositories.ClassementRepository;

@Service
public class ClassementService {

    private final ClassementRepository classementRepository;
    private final StatistiquesService statistiquesService;

    public ClassementService(ClassementRepository classementRepository, StatistiquesService statistiquesService) {
        this.classementRepository = classementRepository;
        this.statistiquesService = statistiquesService;
    }

    /**
     * Crée un nouveau classement
     */
    public Classement createClassement(ClassementRequest request) {
        Classement classement = new Classement(request.getEventId(), request.getSportId());

        List<ClassementEntry> entries = request.getClassements().stream()
            .map(dto -> {
                ClassementEntry entry = new ClassementEntry(dto.getTeamId(), dto.getTeamName());
                entry.setMatchsJoues(dto.getMatchsJoues());
                entry.setVictoires(dto.getVictoires());
                entry.setDefaites(dto.getDefaites());
                entry.setNuls(dto.getNuls());
                entry.setPointsTotal(dto.getPointsTotal());
                entry.setButsMarques(dto.getButsMarques());
                entry.setButsEncaisses(dto.getButsEncaisses());
                entry.setDifferenceButsGoal(dto.getDifferenceButsGoal());
                entry.setTauxVictoire(dto.getTauxVictoire());
                return entry;
            })
            .collect(Collectors.toList());

        classement.setClassements(entries);
        classement.setCreatedAt(LocalDateTime.now());
        classement.setUpdatedAt(LocalDateTime.now());

        return classementRepository.save(classement);
    }

    /**
     * Récupère tous les classements
     */
    public List<Classement> getAllClassements() {
        return classementRepository.findAll();
    }

    /**
     * Récupère un classement par son ID
     */
    public Classement getClassementById(String id) {
        return classementRepository.findById(id)
            .orElseThrow(() -> new MatchNotFoundException("Classement avec l'ID " + id + " n'a pas été trouvé"));
    }

    /**
     * Récupère le classement d'un événement
     */
    public Optional<Classement> getClassementByEventId(String eventId) {
        return classementRepository.findByEventId(eventId);
    }

    /**
     * Récupère le classement d'un sport
     */
    public Optional<Classement> getClassementBySportId(String sportId) {
        return classementRepository.findBySportId(sportId);
    }

    /**
     * Récupère le classement d'un événement et sport
     */
    public Optional<Classement> getClassementByEventIdAndSportId(String eventId, String sportId) {
        return classementRepository.findByEventIdAndSportId(eventId, sportId);
    }

    /**
     * Récupère tous les classements d'un sport (ordonnés par date)
     */
    public List<Classement> getClassementsBySportIdOrdered(String sportId) {
        return classementRepository.findBySportIdOrderByUpdatedAtDesc(sportId);
    }

    /**
     * Met à jour un classement
     */
    public Classement updateClassement(String id, ClassementRequest request) {
        Classement classement = getClassementById(id);

        List<ClassementEntry> entries = request.getClassements().stream()
            .map(dto -> {
                ClassementEntry entry = new ClassementEntry(dto.getTeamId(), dto.getTeamName());
                entry.setMatchsJoues(dto.getMatchsJoues());
                entry.setVictoires(dto.getVictoires());
                entry.setDefaites(dto.getDefaites());
                entry.setNuls(dto.getNuls());
                entry.setPointsTotal(dto.getPointsTotal());
                entry.setButsMarques(dto.getButsMarques());
                entry.setButsEncaisses(dto.getButsEncaisses());
                entry.setDifferenceButsGoal(dto.getDifferenceButsGoal());
                entry.setTauxVictoire(dto.getTauxVictoire());
                return entry;
            })
            .collect(Collectors.toList());

        classement.setClassements(entries);
        classement.setUpdatedAt(LocalDateTime.now());

        return classementRepository.save(classement);
    }

    /**
     * Supprime un classement
     */
    public void deleteClassement(String id) {
        if (!classementRepository.existsById(id)) {
            throw new MatchNotFoundException("Classement avec l'ID " + id + " n'a pas été trouvé");
        }
        classementRepository.deleteById(id);
    }

    /**
     * Calcule et génère le classement basé sur les statistiques d'une équipe
     */
    public Classement generateClassementFromStatistiques(String eventId, String sportId, List<String> teamIds) {
        List<ClassementEntry> entries = teamIds.stream()
            .map(teamId -> {
                Optional<com.example.projectPi.models.Statistiques> statsOpt =
                    statistiquesService.getStatistiquesByTeamAndSport(teamId, sportId);

                ClassementEntry entry = new ClassementEntry(teamId, teamId);

                if (statsOpt.isPresent()) {
                    com.example.projectPi.models.Statistiques stats = statsOpt.get();
                    entry.setMatchsJoues(stats.getNbMatchsJoues());
                    entry.setVictoires(stats.getNbVictoires());
                    entry.setDefaites(stats.getNbDefaites());
                    entry.setNuls(stats.getNbNuls());
                    entry.setPointsTotal(stats.getNbPointsTotal());
                    entry.setButsMarques(stats.getNbButsMarques());
                    entry.setButsEncaisses(stats.getNbButsEncaisses());
                    entry.setDifferenceButsGoal((int) stats.getDifferenceButsGoal());
                    entry.setTauxVictoire(stats.getTauxVictoire());
                }

                return entry;
            })
            .sorted()
            .collect(Collectors.toList());

        // Assigner les rangs
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRang(i + 1);
        }

        // Upsert : on réutilise le classement existant pour cet event+sport afin
        // d'éviter les doublons à chaque recalcul (fin de match).
        Classement classement = classementRepository.findByEventIdAndSportId(eventId, sportId)
            .orElseGet(() -> new Classement(eventId, sportId));
        classement.setClassements(entries);
        classement.setUpdatedAt(LocalDateTime.now());

        return classementRepository.save(classement);
    }

    /**
     * Récupère la position d'une équipe dans le classement
     */
    public Optional<ClassementEntry> getTeamRankingPosition(String classementId, String teamId) {
        Classement classement = getClassementById(classementId);
        return classement.getClassements().stream()
            .filter(entry -> entry.getTeamId().equals(teamId))
            .findFirst();
    }
}
