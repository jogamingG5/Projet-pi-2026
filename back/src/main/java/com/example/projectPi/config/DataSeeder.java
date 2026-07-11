package com.example.projectPi.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.projectPi.dto.MatchRequest;
import com.example.projectPi.models.Event;
import com.example.projectPi.models.Match;
import com.example.projectPi.repositories.ClassementRepository;
import com.example.projectPi.repositories.EventRepository;
import com.example.projectPi.repositories.FeuillesDeMatchRepository;
import com.example.projectPi.repositories.MatchRepository;
import com.example.projectPi.repositories.StatistiquesRepository;
import com.example.projectPi.services.MatchService;

/**
 * DataSeeder - injecte des données de démonstration basées sur des équipes
 * tunisiennes réelles, réparties sur plusieurs sports.
 *
 * <p>Au démarrage : seed uniquement si la base est vide (idempotent, ne supprime
 * jamais rien). Peut aussi être déclenché explicitement en mode "force" via
 * {@link #reseed()} (utilisé par l'endpoint admin) — dans ce cas les collections
 * de démo sont vidées puis régénérées.
 */
@Component
@Order(2) // après MongoCollectionInitializer
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final MatchRepository matchRepository;
    private final StatistiquesRepository statistiquesRepository;
    private final ClassementRepository classementRepository;
    private final FeuillesDeMatchRepository feuillesDeMatchRepository;
    private final MatchService matchService;

    public DataSeeder(EventRepository eventRepository,
                      MatchRepository matchRepository,
                      StatistiquesRepository statistiquesRepository,
                      ClassementRepository classementRepository,
                      FeuillesDeMatchRepository feuillesDeMatchRepository,
                      MatchService matchService) {
        this.eventRepository = eventRepository;
        this.matchRepository = matchRepository;
        this.statistiquesRepository = statistiquesRepository;
        this.classementRepository = classementRepository;
        this.feuillesDeMatchRepository = feuillesDeMatchRepository;
        this.matchService = matchService;
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() > 0 || matchRepository.count() > 0) {
            System.out.println("DataSeeder: données déjà présentes, seeding automatique ignoré");
            return;
        }
        seed();
    }

    /**
     * Réinitialise complètement les données de démo (vide puis régénère).
     * Utilisé par l'endpoint admin de reseed.
     */
    public String reseed() {
        System.out.println("DataSeeder: RESEED forcé — purge des collections de démo...");
        feuillesDeMatchRepository.deleteAll();
        classementRepository.deleteAll();
        statistiquesRepository.deleteAll();
        matchRepository.deleteAll();
        eventRepository.deleteAll();
        return seed();
    }

    /** Crée les événements + matchs tunisiens et déclenche la cascade. */
    private String seed() {
        System.out.println("DataSeeder: injection des équipes tunisiennes (tous sports)...");

        int eventsCreated = 0;
        int matchesCreated = 0;

        List<SeedEvent> seeds = List.of(
            // ---- Tunisie : tous les sports ----
            new SeedEvent("Ligue 1 Tunisienne", "Championnat national de football", "football",
                List.of("Espérance de Tunis", "Étoile du Sahel", "Club Africain", "CS Sfaxien", "US Monastir", "CA Bizertin")),
            new SeedEvent("Championnat de Basket TN", "Nationale A de basketball", "basketball",
                List.of("Étoile du Sahel", "US Monastir", "Club Africain", "Ezzahra Sports", "JS Kairouan", "Dalia Grombalia")),
            new SeedEvent("Championnat de Volley TN", "Nationale A de volleyball", "volleyball",
                List.of("Espérance de Tunis", "Étoile du Sahel", "CS Sfaxien", "Club Africain", "Saydia Sports", "CO Kelibia")),
            new SeedEvent("Championnat de Handball TN", "Nationale A de handball", "handball",
                List.of("Espérance de Tunis", "Club Africain", "ES Sahel", "CS Sakiet Ezzit", "EM Mahdia", "AS Hammamet")),
            new SeedEvent("Tournoi de Tennis TN", "Tournoi national par équipes", "tennis",
                List.of("Tennis Club de Tunis", "Étoile du Sahel", "Club Africain", "AS Marsa")),

            // ---- Grands championnats européens (football) ----
            new SeedEvent("Premier League", "Championnat d'Angleterre", "football",
                List.of("Manchester City", "Arsenal", "Liverpool", "Manchester United", "Chelsea", "Tottenham")),
            new SeedEvent("La Liga", "Championnat d'Espagne", "football",
                List.of("Real Madrid", "FC Barcelone", "Atlético Madrid", "Séville FC", "Real Sociedad", "Valence CF")),
            new SeedEvent("Serie A", "Championnat d'Italie", "football",
                List.of("Inter Milan", "AC Milan", "Juventus", "Napoli", "AS Roma", "Lazio")),
            new SeedEvent("Bundesliga", "Championnat d'Allemagne", "football",
                List.of("Bayern Munich", "Borussia Dortmund", "RB Leipzig", "Bayer Leverkusen", "Eintracht Francfort", "VfB Stuttgart")),
            new SeedEvent("Ligue 1 (France)", "Championnat de France", "football",
                List.of("Paris Saint-Germain", "Olympique de Marseille", "AS Monaco", "Olympique Lyonnais", "LOSC Lille", "Stade Rennais")),

            // ---- Ligue des Champions ----
            new SeedEvent("UEFA Champions League", "Ligue des Champions européenne", "football",
                List.of("Real Madrid", "Manchester City", "Bayern Munich", "Paris Saint-Germain", "Inter Milan", "Borussia Dortmund", "FC Barcelone", "Arsenal")),
            new SeedEvent("CAF Champions League", "Ligue des Champions africaine", "football",
                List.of("Al Ahly", "Espérance de Tunis", "Wydad Casablanca", "Mamelodi Sundowns", "Étoile du Sahel", "TP Mazembe", "Raja Casablanca", "Al Hilal"))
        );

        LocalDate playedStart = LocalDate.of(2026, 6, 5);
        LocalDate futureStart = LocalDate.now().plusDays(10);

        for (SeedEvent seed : seeds) {
            Event event = new Event(seed.nom, seed.description,
                playedStart, playedStart.plusMonths(3),
                Event.EventType.LEAGUE, seed.sportId);
            event.setTeamsIds(new ArrayList<>(seed.teamIds));
            event.setLocationId("Tunis");
            event.setStatus(Event.EventStatus.ONGOING);
            Event savedEvent = eventRepository.save(event);
            eventsCreated++;

            matchesCreated += seedRoundRobin(savedEvent, seed.teamIds, playedStart, futureStart);
        }

        String summary = "DataSeeder: " + eventsCreated + " événements et " + matchesCreated
            + " matchs créés (équipes tunisiennes, cascade stats/classement/feuilles appliquée)";
        System.out.println(summary);
        return summary;
    }

    /**
     * Génère les affrontements round-robin : ~2/3 COMPLETED (scores réalistes),
     * le reste SCHEDULED (dates futures).
     */
    private int seedRoundRobin(Event event, List<String> teamIds,
                               LocalDate playedStart, LocalDate futureStart) {
        int created = 0;
        int matchIndex = 0;
        int totalPairs = teamIds.size() * (teamIds.size() - 1) / 2;
        int completedTarget = Math.max(1, (totalPairs * 2) / 3);

        for (int i = 0; i < teamIds.size(); i++) {
            for (int j = i + 1; j < teamIds.size(); j++) {
                boolean completed = matchIndex < completedTarget;

                MatchRequest req = new MatchRequest();
                req.setEventId(event.getId());
                req.setSportId(event.getSportId());
                req.setTeam1Id(teamIds.get(i));
                req.setTeam2Id(teamIds.get(j));
                req.setTerrainId("Stade " + ((matchIndex % 3) + 1));
                req.setHeure(String.format("%02d:00", 14 + (matchIndex % 6)));
                req.setArbitreId("arbitre-" + ((matchIndex % 3) + 1));
                req.setType(Match.MatchType.LEAGUE);

                if (completed) {
                    req.setStatus(Match.MatchStatus.COMPLETED);
                    req.setScoreTeam1((matchIndex * 2 + 1) % 4);
                    req.setScoreTeam2((matchIndex + 2) % 3);
                    req.setDateDebut(playedStart.plusDays(matchIndex));
                } else {
                    req.setStatus(Match.MatchStatus.SCHEDULED);
                    req.setScoreTeam1(0);
                    req.setScoreTeam2(0);
                    req.setDateDebut(futureStart.plusDays(matchIndex));
                }

                matchService.createMatch(req);
                created++;
                matchIndex++;
            }
        }

        return created;
    }

    /** Petit conteneur immuable pour décrire un événement à seed. */
    private static final class SeedEvent {
        private final String nom;
        private final String description;
        private final String sportId;
        private final List<String> teamIds;

        private SeedEvent(String nom, String description, String sportId, List<String> teamIds) {
            this.nom = nom;
            this.description = description;
            this.sportId = sportId;
            this.teamIds = teamIds;
        }
    }
}
