package com.example.projectPi.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectPi.dto.ApiResponseDTO;
import com.example.projectPi.dto.MatchRequest;
import com.example.projectPi.dto.TeamRanking;
import com.example.projectPi.dto.TeamStats;
import com.example.projectPi.models.Match;
import com.example.projectPi.services.MatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * EXAMPLE: Updated MatchController showing the pattern for all controllers
 * 
 * Key changes:
 * 1. @Valid annotation on all @RequestBody parameters
 * 2. Return type changed to ResponseEntity<ApiResponseDTO<T>>
 * 3. Using ApiResponse factory methods (success, created, etc.)
 * 4. HTTP status codes properly set (201 for creation, 200 for success)
 * 
 * Apply this same pattern to EventController and UserController
 */
@RestController
@RequestMapping("/api/matchs")
@Tag(name = "Matches", description = "Sports Match Management")
public class MatchControllerExample {

    private final MatchService matchService;

    public MatchControllerExample(MatchService matchService) {
        this.matchService = matchService;
    }

    // ==================== CRUD ENDPOINTS ====================

    /**
     * Create a new match
     * @Valid - Validates MatchRequest based on annotations (@NotNull, @NotBlank, etc.)
     * Returns 201 (Created) with created match wrapped in ApiResponse
     */
    @PostMapping
    @Operation(summary = "Create a new match")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Match created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed - invalid input")
    })
    public ResponseEntity<ApiResponseDTO<Match>> createMatch(
            @Valid @RequestBody MatchRequest request) {
        Match created = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.created(created, "Match created successfully"));
    }

    /**
     * Get all matches
     * Returns 200 with list of matches wrapped in ApiResponse
     */
    @GetMapping
    @Operation(summary = "Get all matches")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of matches")
    public ResponseEntity<ApiResponseDTO<List<Match>>> getAllMatches() {
        List<Match> matches = matchService.getAllMatchs();
        return ResponseEntity.ok(
            ApiResponseDTO.success(matches, "Matches retrieved successfully")
        );
    }

    /**
     * Get match by ID
     * Returns 404 if not found (handled by GlobalExceptionHandler)
     * Returns 200 if found, wrapped in ApiResponse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get match by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Match found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<ApiResponseDTO<Match>> getMatchById(
            @Parameter(description = "Match ID")
            @PathVariable String id) {
        Match match = matchService.getMatchById(id);
        return ResponseEntity.ok(
            ApiResponseDTO.success(match, "Match retrieved successfully")
        );
    }

    /**
     * Update match
     * @Valid - Validates input
     * Returns 200 with updated match wrapped in ApiResponse
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a match")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Match updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Match not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed")
    })
    public ResponseEntity<ApiResponseDTO<Match>> updateMatch(
            @Parameter(description = "Match ID")
            @PathVariable String id,
            @Valid @RequestBody MatchRequest request) {
        Match updated = matchService.updateMatch(id, request);
        return ResponseEntity.ok(
            ApiResponseDTO.success(updated, "Match updated successfully")
        );
    }

    /**
     * Delete match
     * Returns 204 (No Content) on success
     * Returns 404 if not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a match")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Match deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "Match ID")
            @PathVariable String id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== SEARCH & FILTER ENDPOINTS ====================

    /**
     * Search matches with filters
     */
    @GetMapping("/search")
    @Operation(summary = "Search matches with filters")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search results")
    public ResponseEntity<ApiResponseDTO<List<Match>>> searchMatches(
            @Parameter(description = "Team ID (optional)")
            @RequestParam(required = false) String teamId,
            @Parameter(description = "Sport ID (optional)")
            @RequestParam(required = false) String sportId,
            @Parameter(description = "Start date (optional)")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End date (optional)")
            @RequestParam(required = false) LocalDate endDate,
            @Parameter(description = "Match status (optional)")
            @RequestParam(required = false) Match.MatchStatus status) {
        List<Match> results = matchService.searchMatches(teamId, sportId, startDate, endDate, status);
        return ResponseEntity.ok(
            ApiResponseDTO.success(results, "Search results retrieved")
        );
    }

    /**
     * Get upcoming matches for a team
     */
    @GetMapping("/upcoming/{teamId}")
    @Operation(summary = "Get upcoming matches for a team")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Upcoming matches")
    public ResponseEntity<ApiResponseDTO<List<Match>>> getUpcomingMatches(
            @Parameter(description = "Team ID")
            @PathVariable String teamId) {
        List<Match> matches = matchService.getUpcomingMatches(teamId);
        return ResponseEntity.ok(
            ApiResponseDTO.success(matches, "Upcoming matches retrieved")
        );
    }

    /**
     * Get match history for a team
     */
    @GetMapping("/history/{teamId}")
    @Operation(summary = "Get match history for a team")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Match history")
    public ResponseEntity<ApiResponseDTO<List<Match>>> getTeamHistory(
            @Parameter(description = "Team ID")
            @PathVariable String teamId) {
        List<Match> history = matchService.getTeamHistory(teamId);
        return ResponseEntity.ok(
            ApiResponseDTO.success(history, "Team history retrieved")
        );
    }

    // ==================== STATISTICS ENDPOINTS ====================

    /**
     * Get team statistics
     */
    @GetMapping("/stats/team/{teamId}")
    @Operation(summary = "Get team statistics")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Team statistics")
    public ResponseEntity<ApiResponseDTO<TeamStats>> getTeamStats(
            @Parameter(description = "Team ID")
            @PathVariable String teamId) {
        TeamStats stats = matchService.getTeamStats(teamId);
        return ResponseEntity.ok(
            ApiResponseDTO.success(stats, "Team statistics retrieved")
        );
    }

    /**
     * Get team rankings for a sport
     */
    @GetMapping("/rankings/{sportId}")
    @Operation(summary = "Get team rankings for a sport")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Team rankings")
    public ResponseEntity<ApiResponseDTO<List<TeamRanking>>> getTeamRankings(
            @Parameter(description = "Sport ID")
            @PathVariable String sportId) {
        List<TeamRanking> rankings = matchService.getTeamRankings(sportId);
        return ResponseEntity.ok(
            ApiResponseDTO.success(rankings, "Team rankings retrieved")
        );
    }

    /**
     * Get match count by status
     */
    @GetMapping("/stats/status-count")
    @Operation(summary = "Get match count by status")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status count")
    public ResponseEntity<ApiResponseDTO<Map<String, Integer>>> getMatchCountByStatus() {
        Map<String, Integer> statusCount = matchService.getMatchCountByStatus();
        return ResponseEntity.ok(
            ApiResponseDTO.success(statusCount, "Match count by status retrieved")
        );
    }

    /**
     * Get most active referees
     */
    @GetMapping("/referees/active")
    @Operation(summary = "Get most active referees")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active referees")
    public ResponseEntity<ApiResponseDTO<List<Map<String, Object>>>> getMostActiveReferees() {
        List<Map<String, Object>> referees = matchService.getMostActiveReferees();
        return ResponseEntity.ok(
            ApiResponseDTO.success(referees, "Active referees retrieved")
        );
    }
}
