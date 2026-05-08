package com.example.projectPi.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectPi.dto.MatchSuggestion;
import com.example.projectPi.services.MatchmakingService;

/**
 * REST Controller for matchmaking operations
 * Handles requests for balanced match suggestions
 */
@RestController
@RequestMapping("/api/matchmaking")
@CrossOrigin(origins = {"http://localhost:63343", "http://localhost:4200"})
public class MatchmakingController {
    
    private final MatchmakingService matchmakingService;
    
    public MatchmakingController(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }
    
    /**
     * Get match suggestions for a specific event and sport
     * 
     * @param eventId Event identifier
     * @param sportId Sport type (football, volleyball, etc.)
     * @param round Round number (optional, for round-robin format)
     * @param format Format: "roundrobin" (default) or "suisse" (Swiss format)
     * @return List of balanced match suggestions sorted by priority
     */
    @GetMapping("/event/{eventId}/sport/{sportId}")
    public ResponseEntity<List<MatchSuggestion>> suggestMatchups(
            @PathVariable String eventId,
            @PathVariable String sportId,
            @RequestParam(required = false) Integer round,
            @RequestParam(defaultValue = "roundrobin") String format) {
        
        try {
            List<MatchSuggestion> suggestions = matchmakingService.suggestMatchups(
                eventId, sportId, round, format
            );
            
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
