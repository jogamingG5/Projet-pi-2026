package com.example.projectPi.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectPi.models.Classement;
import com.example.projectPi.models.Classement.ClassementEntry;
import com.example.projectPi.models.Event;
import com.example.projectPi.models.FeuillesDeMatch;
import com.example.projectPi.models.FeuillesDeMatch.RecapEquipe;
import com.example.projectPi.models.Match;
import com.example.projectPi.models.Statistiques;
import com.example.projectPi.services.ClassementService;
import com.example.projectPi.services.EventService;
import com.example.projectPi.services.ExportService;
import com.example.projectPi.services.FeuillesDeMatchService;
import com.example.projectPi.services.MatchService;
import com.example.projectPi.services.StatistiquesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoints d'export de fichiers réels (CSV et PDF) pour les principales
 * collections : matchs, événements, statistiques, classement.
 *
 * Chaque endpoint renvoie un fichier en pièce jointe (Content-Disposition:
 * attachment) — pas d'impression navigateur.
 */
@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "Export CSV / PDF des données (matchs, événements, stats, classement)")
public class ExportController {

    private final ExportService exportService;
    private final MatchService matchService;
    private final EventService eventService;
    private final StatistiquesService statistiquesService;
    private final ClassementService classementService;
    private final FeuillesDeMatchService feuillesDeMatchService;

    public ExportController(ExportService exportService,
                            MatchService matchService,
                            EventService eventService,
                            StatistiquesService statistiquesService,
                            ClassementService classementService,
                            FeuillesDeMatchService feuillesDeMatchService) {
        this.exportService = exportService;
        this.matchService = matchService;
        this.eventService = eventService;
        this.statistiquesService = statistiquesService;
        this.classementService = classementService;
        this.feuillesDeMatchService = feuillesDeMatchService;
    }

    // ==================== MATCHS ====================

    @GetMapping("/matchs")
    @Operation(summary = "Exporter les matchs (csv|pdf)",
               description = "Filtres optionnels sport/statut, comme la recherche de matchs")
    public ResponseEntity<byte[]> exportMatchs(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String sportId,
            @RequestParam(required = false) Match.MatchStatus status) {

        List<Match> matches = matchService.searchMatches(null, sportId, null, null, status);

        List<String> headers = List.of("Équipe 1", "Équipe 2", "Score", "Date", "Heure",
            "Sport", "Statut", "Type", "Terrain", "Événement");
        List<List<String>> rows = new ArrayList<>();
        for (Match m : matches) {
            rows.add(List.of(
                nz(m.getTeam1Id()), nz(m.getTeam2Id()),
                m.getScoreTeam1() + " - " + m.getScoreTeam2(),
                m.getDateDebut() == null ? "" : m.getDateDebut().toString(),
                nz(m.getHeure()), nz(m.getSportId()),
                m.getStatus() == null ? "" : m.getStatus().name(),
                m.getType() == null ? "" : m.getType().name(),
                nz(m.getTerrainId()), nz(m.getEventId())
            ));
        }
        return file(format, "matchs", "Matchs", headers, rows);
    }

    // ==================== ÉVÉNEMENTS ====================

    @GetMapping("/events")
    @Operation(summary = "Exporter les événements (csv|pdf)")
    public ResponseEntity<byte[]> exportEvents(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String sportId,
            @RequestParam(required = false) Event.EventType type) {

        List<Event> events = eventService.getAllEvents().stream()
            .filter(e -> sportId == null || sportId.isEmpty() || sportId.equals(e.getSportId()))
            .filter(e -> type == null || e.getType() == type)
            .toList();

        List<String> headers = List.of("Nom", "Sport", "Type", "Statut",
            "Début", "Fin", "Équipes", "Description");
        List<List<String>> rows = new ArrayList<>();
        for (Event e : events) {
            rows.add(List.of(
                nz(e.getNomEvenement()), nz(e.getSportId()),
                e.getType() == null ? "" : e.getType().name(),
                e.getStatus() == null ? "" : e.getStatus().name(),
                e.getDateDebut() == null ? "" : e.getDateDebut().toString(),
                e.getDateFin() == null ? "" : e.getDateFin().toString(),
                e.getTeamsIds() == null ? "" : String.join(" | ", e.getTeamsIds()),
                nz(e.getDescription())
            ));
        }
        return file(format, "evenements", "Événements", headers, rows);
    }

    // ==================== STATISTIQUES ====================

    @GetMapping("/statistiques")
    @Operation(summary = "Exporter les statistiques (csv|pdf)")
    public ResponseEntity<byte[]> exportStatistiques(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String sportId) {

        List<Statistiques> stats = statistiquesService.getAllStatistiques().stream()
            .filter(s -> sportId == null || sportId.isEmpty() || sportId.equals(s.getSportId()))
            .toList();

        List<String> headers = List.of("Équipe", "Sport", "Joués", "Victoires", "Nuls",
            "Défaites", "Buts marqués", "Buts encaissés", "Différence", "Points", "% Victoire");
        List<List<String>> rows = new ArrayList<>();
        for (Statistiques s : stats) {
            rows.add(List.of(
                nz(s.getTeamId()), nz(s.getSportId()),
                String.valueOf(s.getNbMatchsJoues()), String.valueOf(s.getNbVictoires()),
                String.valueOf(s.getNbNuls()), String.valueOf(s.getNbDefaites()),
                String.valueOf(s.getNbButsMarques()), String.valueOf(s.getNbButsEncaisses()),
                String.valueOf((int) s.getDifferenceButsGoal()),
                String.valueOf(s.getNbPointsTotal()),
                String.format("%.0f%%", s.getTauxVictoire())
            ));
        }
        return file(format, "statistiques", "Statistiques", headers, rows);
    }

    // ==================== CLASSEMENT ====================

    @GetMapping("/classement")
    @Operation(summary = "Exporter le classement (csv|pdf)",
               description = "Exporte un classement précis (eventId+sportId) ou tous les classements")
    public ResponseEntity<byte[]> exportClassement(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam(required = false) String eventId,
            @RequestParam(required = false) String sportId) {

        List<Classement> classements;
        if (eventId != null && !eventId.isEmpty() && sportId != null && !sportId.isEmpty()) {
            classements = classementService.getClassementByEventIdAndSportId(eventId, sportId)
                .map(List::of).orElseGet(List::of);
        } else {
            classements = classementService.getAllClassements();
        }

        List<String> headers = List.of("Classement (event/sport)", "Rang", "Équipe", "Joués",
            "V", "N", "D", "Buts +", "Buts -", "Diff", "Points", "% Victoire");
        List<List<String>> rows = new ArrayList<>();
        for (Classement c : classements) {
            String scope = nz(c.getEventId()) + " / " + nz(c.getSportId());
            List<ClassementEntry> entries = c.getClassements() == null ? List.of() : c.getClassements();
            for (ClassementEntry e : entries) {
                rows.add(List.of(
                    scope, String.valueOf(e.getRang()),
                    e.getTeamName() != null ? e.getTeamName() : nz(e.getTeamId()),
                    String.valueOf(e.getMatchsJoues()), String.valueOf(e.getVictoires()),
                    String.valueOf(e.getNuls()), String.valueOf(e.getDefaites()),
                    String.valueOf(e.getButsMarques()), String.valueOf(e.getButsEncaisses()),
                    String.valueOf(e.getDifferenceButsGoal()), String.valueOf(e.getPointsTotal()),
                    String.format("%.0f%%", e.getTauxVictoire())
                ));
            }
        }
        return file(format, "classement", "Classement", headers, rows);
    }

    // ==================== FEUILLES DE MATCH ====================

    @GetMapping("/feuilles")
    @Operation(summary = "Exporter les feuilles de match (csv|pdf)",
               description = "Une ligne par équipe : score et cartons")
    public ResponseEntity<byte[]> exportFeuilles(
            @RequestParam(defaultValue = "csv") String format) {

        List<FeuillesDeMatch> feuilles = feuillesDeMatchService.getAllFeuillesDeMatch();

        List<String> headers = List.of("Match", "Équipe", "Score", "Cartons jaunes", "Cartons rouges");
        List<List<String>> rows = new ArrayList<>();
        for (FeuillesDeMatch f : feuilles) {
            List<RecapEquipe> recap = f.getRecap() == null ? List.of() : f.getRecap();
            for (RecapEquipe r : recap) {
                int yellow = r.getPlayerbookedYellowCards() == null ? 0 : r.getPlayerbookedYellowCards().size();
                int red = r.getPlayerbookedRedCards() == null ? 0 : r.getPlayerbookedRedCards().size();
                rows.add(List.of(
                    nz(f.getMatchId()), nz(r.getTeamId()),
                    String.valueOf(r.getScore()),
                    String.valueOf(yellow), String.valueOf(red)
                ));
            }
        }
        return file(format, "feuilles-de-match", "Feuilles de match", headers, rows);
    }

    // ==================== HELPERS ====================

    private ResponseEntity<byte[]> file(String format, String baseName, String title,
                                        List<String> headers, List<List<String>> rows) {
        boolean pdf = "pdf".equalsIgnoreCase(format);
        byte[] body = pdf
            ? exportService.toPdf(title, headers, rows)
            : exportService.toCsv(headers, rows);

        String filename = baseName + (pdf ? ".pdf" : ".csv");
        MediaType mediaType = pdf ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("text/csv");

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(mediaType)
            .body(body);
    }

    private String nz(String value) {
        return value == null ? "" : value;
    }
}
