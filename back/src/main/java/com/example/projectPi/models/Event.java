package com.example.projectPi.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
public class Event {

    @Id
    private String id;

    private String nomEvenement;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private EventType type;
    private EventStatus status = EventStatus.PLANNED;
    private String sportId;
    private List<String> teamsIds;      // IDs des équipes participants
    private String locationId;           // Terrain/lieu principal
    private int expectedMatches;         // Nombre de matches estimé
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum EventType { LEAGUE, FRIENDLY, TOURNAMENT }
    public enum EventStatus { PLANNED, ONGOING, FINISHED, CANCELLED }

    // Constructors
    public Event() {}

    public Event(String nomEvenement, String description, LocalDate dateDebut, 
                 LocalDate dateFin, EventType type, String sportId) {
        this.nomEvenement = nomEvenement;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.sportId = sportId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public String getId()                           { return id; }
    public void setId(String id)                    { this.id = id; }

    public String getNomEvenement()                 { return nomEvenement; }
    public void setNomEvenement(String nom)         { this.nomEvenement = nom; }

    public String getDescription()                  { return description; }
    public void setDescription(String desc)         { this.description = desc; }

    public LocalDate getDateDebut()                 { return dateDebut; }
    public void setDateDebut(LocalDate date)        { this.dateDebut = date; }

    public LocalDate getDateFin()                   { return dateFin; }
    public void setDateFin(LocalDate date)          { this.dateFin = date; }

    public EventType getType()                      { return type; }
    public void setType(EventType type)             { this.type = type; }

    public EventStatus getStatus()                  { return status; }
    public void setStatus(EventStatus status)       { this.status = status; }

    public String getSportId()                      { return sportId; }
    public void setSportId(String sportId)          { this.sportId = sportId; }

    public List<String> getTeamsIds()               { return teamsIds; }
    public void setTeamsIds(List<String> teams)     { this.teamsIds = teams; }

    public String getLocationId()                   { return locationId; }
    public void setLocationId(String location)      { this.locationId = location; }

    public int getExpectedMatches()                 { return expectedMatches; }
    public void setExpectedMatches(int count)       { this.expectedMatches = count; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime date)    { this.createdAt = date; }

    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime date)    { this.updatedAt = date; }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", nomEvenement='" + nomEvenement + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", type=" + type +
                ", status=" + status +
                ", sportId='" + sportId + '\'' +
                ", teamsIds=" + teamsIds +
                '}';
    }
}
