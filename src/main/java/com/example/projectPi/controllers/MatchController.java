package com.example.projectPi.controllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projectPi.dto.MatchRequest;
import com.example.projectPi.models.Match;
import com.example.projectPi.services.MatchService;

@RestController
@RequestMapping("/api/matchs")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody MatchRequest request) {
        Match created = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatchs() {
        return ResponseEntity.ok(matchService.getAllMatchs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable String id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatch(
            @PathVariable String id,
            @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.updateMatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable String id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}