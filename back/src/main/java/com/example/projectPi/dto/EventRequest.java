package com.example.projectPi.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.projectPi.models.Event;
import com.example.projectPi.validation.ValidDateRange;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidDateRange(message = "Start date must be before or equal to end date")
@Schema(name = "EventRequest", description = "Requête pour créer ou modifier un événement")
public class EventRequest {
    
    @NotBlank(message = "Event name is required")
    @Size(min = 3, max = 255, message = "Event name must be between 3 and 255 characters")
    @Schema(description = "Nom de l'événement", example = "Championnat du Maroc 2026", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nomEvenement;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Description détaillée de l'événement", example = "Championnat national de football")
    private String description;
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @Schema(description = "Date de début de l'événement", example = "2026-05-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;
    
    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    @Schema(description = "Date de fin de l'événement", example = "2026-06-30", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateFin;
    
    @NotNull(message = "Event type is required")
    @Schema(description = "Type d'événement", example = "LEAGUE")
    private Event.EventType type;
    
    @NotBlank(message = "Sport ID is required")
    @Schema(description = "ID du sport", example = "6234567890abc123")
    private String sportId;
    
    @Schema(description = "Liste des IDs d'équipes participants")
    private List<String> teamsIds;
    
    @Schema(description = "ID du terrain/lieu principal")
    private String locationId;
    
    @Min(value = 1, message = "Expected matches must be at least 1")
    @Max(value = 500, message = "Expected matches cannot exceed 500")
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
