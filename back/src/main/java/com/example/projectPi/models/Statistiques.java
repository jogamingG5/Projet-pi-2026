package com.example.projectPi.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Statistiques - Représente les statistiques d'une équipe pour un sport donné
 */
@Document(collection = "statistiques")
public class Statistiques {

    @Id
    private String id;

    private String teamId;
    private String sportId;

    private int nbMatchsJoues = 0;
    private int nbVictoires = 0;
    private int nbDefaites = 0;
    private int nbNuls = 0;
    private int nbButsMarques = 0;
    private int nbButsEncaisses = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors
    public Statistiques() {}

    public Statistiques(String teamId, String sportId) {
        this.teamId = teamId;
        this.sportId = sportId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Computed properties
    public int getNbPointsTotal() {
        return (nbVictoires * 3) + nbNuls;
    }

    public double getDifferenceButsGoal() {
        return nbButsMarques - nbButsEncaisses;
    }

    public double getTauxVictoire() {
        return nbMatchsJoues > 0 ? (double) nbVictoires / nbMatchsJoues * 100 : 0;
    }

    public double getMoyenneButsMarques() {
        return nbMatchsJoues > 0 ? (double) nbButsMarques / nbMatchsJoues : 0;
    }

    public double getMoyenneButsEncaisses() {
        return nbMatchsJoues > 0 ? (double) nbButsEncaisses / nbMatchsJoues : 0;
    }

    // Getters & Setters
    public String getId()                          { return id; }
    public void setId(String id)                   { this.id = id; }
    public String getTeamId()                      { return teamId; }
    public void setTeamId(String teamId)           { this.teamId = teamId; }
    public String getSportId()                     { return sportId; }
    public void setSportId(String sportId)         { this.sportId = sportId; }
    public int getNbMatchsJoues()                  { return nbMatchsJoues; }
    public void setNbMatchsJoues(int n)            { this.nbMatchsJoues = n; }
    public int getNbVictoires()                    { return nbVictoires; }
    public void setNbVictoires(int n)              { this.nbVictoires = n; }
    public int getNbDefaites()                     { return nbDefaites; }
    public void setNbDefaites(int n)               { this.nbDefaites = n; }
    public int getNbNuls()                         { return nbNuls; }
    public void setNbNuls(int n)                   { this.nbNuls = n; }
    public int getNbButsMarques()                  { return nbButsMarques; }
    public void setNbButsMarques(int n)            { this.nbButsMarques = n; }
    public int getNbButsEncaisses()                { return nbButsEncaisses; }
    public void setNbButsEncaisses(int n)          { this.nbButsEncaisses = n; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime d)      { this.createdAt = d; }
    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime d)      { this.updatedAt = d; }
}
