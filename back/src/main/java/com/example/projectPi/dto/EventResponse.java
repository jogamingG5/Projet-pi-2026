package com.example.projectPi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.projectPi.models.Event;

@Schema(name = "EventResponse", description = "Réponse contenant les informations complètes d'un événement")
public class EventResponse {
    
    @Schema(description = "ID unique de l'événement")
    private String id;
    
    @Schema(description = "Nom de l'événement")
    private String nomEvenement;
    
    @Schema(description = "Description de l'événement")
    private String description;
    
    @Schema(description = "Date de début")
    private LocalDate dateDebut;
    
    @Schema(description = "Date de fin")
    private LocalDate dateFin;
    
    @Schema(description = "Type d'événement")
    private Event.EventType type;
    
    @Schema(description = "Statut actuel de l'événement")
    private Event.EventStatus status;
    
    @Schema(description = "ID du sport")
    private String sportId;
    
    @Schema(description = "Liste des IDs d'équipes participants")
    private List<String> teamsIds;
    
    @Schema(description = "ID du terrain/lieu principal")
    private String locationId;
    
    @Schema(description = "Nombre estimé de matches")
    private int expectedMatches;
    
    @Schema(description = "Nombre de matches déjà joués", example = "5")
    private int completedMatches;
    
    @Schema(description = "Pourcentage de progression", example = "25.0")
    private double progressPercentage;
    
    @Schema(description = "Date de création")
    private LocalDateTime createdAt;
    
    @Schema(description = "Date de dernière modification")
    private LocalDateTime updatedAt;

    // Constructors
    public EventResponse() {}

    public EventResponse(Event event) {
        this.id = event.getId();
        this.nomEvenement = event.getNomEvenement();
        this.description = event.getDescription();
        this.dateDebut = event.getDateDebut();
        this.dateFin = event.getDateFin();
        this.type = event.getType();
        this.status = event.getStatus();
        this.sportId = event.getSportId();
        this.teamsIds = event.getTeamsIds();
        this.locationId = event.getLocationId();
        this.expectedMatches = event.getExpectedMatches();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
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

    public Event.EventType getType()                { return type; }
    public void setType(Event.EventType type)       { this.type = type; }

    public Event.EventStatus getStatus()            { return status; }
    public void setStatus(Event.EventStatus status) { this.status = status; }

    public String getSportId()                      { return sportId; }
    public void setSportId(String sportId)          { this.sportId = sportId; }

    public List<String> getTeamsIds()               { return teamsIds; }
    public void setTeamsIds(List<String> teams)     { this.teamsIds = teams; }

    public String getLocationId()                   { return locationId; }
    public void setLocationId(String location)      { this.locationId = location; }

    public int getExpectedMatches()                 { return expectedMatches; }
    public void setExpectedMatches(int count)       { this.expectedMatches = count; }

    public int getCompletedMatches()                { return completedMatches; }
    public void setCompletedMatches(int count)      { this.completedMatches = count; }

    public double getProgressPercentage()           { return progressPercentage; }
    public void setProgressPercentage(double pct)   { this.progressPercentage = pct; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime date)    { this.createdAt = date; }

    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime date)    { this.updatedAt = date; }
}
