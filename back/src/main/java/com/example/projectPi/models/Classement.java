package com.example.projectPi.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Classement - Représente le classement des équipes pour un événement ou un sport donné
 * Calcul basé sur les performances et les statistiques des matchs
 */
@Document(collection = "classement")
public class Classement {

    @Id
    private String id;

    private String eventId;
    private String sportId;

    private List<ClassementEntry> classements;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Classement() {}

    public Classement(String eventId, String sportId) {
        this.eventId = eventId;
        this.sportId = sportId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Inner class for ranking entry
    public static class ClassementEntry implements Comparable<ClassementEntry> {
        private String teamId;
        private String teamName;
        private int rang;

        private int matchsJoues;
        private int victoires;
        private int defaites;
        private int nuls;

        private int pointsTotal;
        private int butsMarques;
        private int butsEncaisses;
        private int differenceButsGoal;

        private double tauxVictoire;

        // Constructors
        public ClassementEntry() {}

        public ClassementEntry(String teamId, String teamName) {
            this.teamId = teamId;
            this.teamName = teamName;
            this.rang = 0;
            this.pointsTotal = 0;
            this.matchsJoues = 0;
            this.victoires = 0;
            this.defaites = 0;
            this.nuls = 0;
            this.butsMarques = 0;
            this.butsEncaisses = 0;
            this.differenceButsGoal = 0;
            this.tauxVictoire = 0.0;
        }

        @Override
        public int compareTo(ClassementEntry other) {
            // Comparaison par points (descendant)
            if (this.pointsTotal != other.pointsTotal) {
                return Integer.compare(other.pointsTotal, this.pointsTotal);
            }
            // En cas d'égalité, comparaison par différence de buts
            if (this.differenceButsGoal != other.differenceButsGoal) {
                return Integer.compare(other.differenceButsGoal, this.differenceButsGoal);
            }
            // En cas d'égalité, comparaison par buts marqués
            return Integer.compare(other.butsMarques, this.butsMarques);
        }

        // Getters & Setters
        public String getTeamId()                     { return teamId; }
        public void setTeamId(String teamId)          { this.teamId = teamId; }
        public String getTeamName()                   { return teamName; }
        public void setTeamName(String teamName)      { this.teamName = teamName; }
        public int getRang()                          { return rang; }
        public void setRang(int rang)                 { this.rang = rang; }
        public int getMatchsJoues()                   { return matchsJoues; }
        public void setMatchsJoues(int matchsJoues)   { this.matchsJoues = matchsJoues; }
        public int getVictoires()                     { return victoires; }
        public void setVictoires(int victoires)       { this.victoires = victoires; }
        public int getDefaites()                      { return defaites; }
        public void setDefaites(int defaites)         { this.defaites = defaites; }
        public int getNuls()                          { return nuls; }
        public void setNuls(int nuls)                 { this.nuls = nuls; }
        public int getPointsTotal()                   { return pointsTotal; }
        public void setPointsTotal(int pointsTotal)   { this.pointsTotal = pointsTotal; }
        public int getButsMarques()                   { return butsMarques; }
        public void setButsMarques(int butsMarques)   { this.butsMarques = butsMarques; }
        public int getButsEncaisses()                 { return butsEncaisses; }
        public void setButsEncaisses(int butsEncaisses) { this.butsEncaisses = butsEncaisses; }
        public int getDifferenceButsGoal()            { return differenceButsGoal; }
        public void setDifferenceButsGoal(int diff)   { this.differenceButsGoal = diff; }
        public double getTauxVictoire()               { return tauxVictoire; }
        public void setTauxVictoire(double taux)      { this.tauxVictoire = taux; }
    }

    // Getters & Setters
    public String getId()                          { return id; }
    public void setId(String id)                   { this.id = id; }
    public String getEventId()                     { return eventId; }
    public void setEventId(String eventId)         { this.eventId = eventId; }
    public String getSportId()                     { return sportId; }
    public void setSportId(String sportId)         { this.sportId = sportId; }
    public List<ClassementEntry> getClassements()  { return classements; }
    public void setClassements(List<ClassementEntry> c) { this.classements = c; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime d)      { this.createdAt = d; }
    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime d)      { this.updatedAt = d; }
}
