package com.example.projectPi.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import com.example.projectPi.models.Event;

@Schema(name = "EventRequest", description = "Requête pour créer ou modifier un événement")
public class EventRequest {
    
    @Schema(description = "Nom de l'événement", example = "Championnat du Maroc 2026", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nomEvenement;
    
    @Schema(description = "Description détaillée de l'événement", example = "Championnat national de football")
    private String description;
    
    @Schema(description = "Date de début de l'événement", example = "2026-05-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;
    
    @Schema(description = "Date de fin de l'événement", example = "2026-06-30", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateFin;
    
    @Schema(description = "Type d'événement", example = "LEAGUE")
    private Event.EventType type;
    
    @Schema(description = "ID du sport", example = "6234567890abc123")
    private String sportId;
    
    @Schema(description = "Liste des IDs d'équipes participants")
    private List<String> teamsIds;
    
    @Schema(description = "ID du terrain/lieu principal")
    private String locationId;
    
    @Schema(description = "Nombre estimé de matches", example = "20")
    private int expectedMatches;

    // Constructors
    public EventRequest() {}

    public EventRequest(String nomEvenement, String description, LocalDate dateDebut,
                        LocalDate dateFin, Event.EventType type, String sportId) {
        this.nomEvenement = nomEvenement;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.type = type;
        this.sportId = sportId;
    }

    // Getters & Setters
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

    public String getSportId()                      { return sportId; }
    public void setSportId(String sportId)          { this.sportId = sportId; }

    public List<String> getTeamsIds()               { return teamsIds; }
    public void setTeamsIds(List<String> teams)     { this.teamsIds = teams; }

    public String getLocationId()                   { return locationId; }
    public void setLocationId(String location)      { this.locationId = location; }

    public int getExpectedMatches()                 { return expectedMatches; }
    public void setExpectedMatches(int count)       { this.expectedMatches = count; }
}
