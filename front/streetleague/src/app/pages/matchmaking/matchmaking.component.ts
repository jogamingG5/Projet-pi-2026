import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatchService } from '../../services/match.service';
import { EventService } from '../../services/event.service';
import { Match } from '../../models/match.model';
import { Event } from '../../models/event.model';

// Model for match suggestions
interface MatchSuggestion {
  teamAId: string;
  teamBId: string;
  teamAName: string;
  teamBName: string;
  balanceScore: number;
  predictedWinnerId: string;
  confidencePercent: number;
  priorityScore: number;
}

@Component({
  selector: 'app-matchmaking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './matchmaking.component.html',
  styleUrls: ['./matchmaking.component.css']
})
export class MatchmakingComponent implements OnInit {
  
  // Form state
  selectedEventId: string = '';
  selectedSportId: string = '';
  selectedFormat: string = 'roundrobin';
  roundNumber: number = 1;
  
  // Data
  suggestions: MatchSuggestion[] = [];
  events: Event[] = [];
  matches: Match[] = [];
  sports: string[] = ['football', 'volleyball', 'basketball', 'badminton', 'tennis'];
  
  // Loading & error states
  loading: boolean = false;
  error: string | null = null;
  success: string | null = null;

  constructor(
    private matchService: MatchService,
    private eventService: EventService
  ) {}
  
  ngOnInit(): void {
    this.loadMatches();
    this.loadEvents();
  }
  
  /**
   * Load matches from backend and derive sports/events from live data
   */
  loadMatches(): void {
    this.matchService.getMatches().subscribe({
      next: (data) => {
        this.matches = data;
        this.sports = [...new Set(data.map(match => match.sportId).filter(Boolean))].sort();

        if (!this.selectedSportId && this.sports.length > 0) {
          this.selectedSportId = this.sports[0];
        }

        if (this.selectedEventId && this.selectedSportId && this.suggestions.length === 0) {
          this.generateSuggestions();
        }
      },
      error: () => {
        this.error = 'Unable to load existing matches';
      }
    });
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.events = data;
        if (!this.selectedEventId && data.length > 0) {
          this.selectedEventId = data[0].id;
        }

        if (this.selectedEventId && this.selectedSportId && this.matches.length > 0 && this.suggestions.length === 0) {
          this.generateSuggestions();
        }
      },
      error: () => {
        this.events = [];
      }
    });
  }

  availableEventOptions(): { id: string; label: string }[] {
    const eventIds = [...new Set(this.matches.map(match => match.eventId).filter((value): value is string => !!value))];

    return eventIds.map((eventId) => ({
      id: eventId,
      label: this.getEventLabel(eventId)
    }));
  }
  
  /**
   * Generate balanced match suggestions
   */
  generateSuggestions(): void {
    if (!this.selectedEventId || !this.selectedSportId) {
      this.error = 'Please select both an event and sport';
      return;
    }
    
    this.loading = true;
    this.error = null;
    this.success = null;

    const scopedMatches = this.matches.filter(match => {
      const eventMatches = !this.selectedEventId || match.eventId === this.selectedEventId;
      const sportMatches = match.sportId === this.selectedSportId;
      return eventMatches && sportMatches;
    });

    const teamIds = [...new Set(scopedMatches.flatMap(match => [match.team1Id, match.team2Id]).filter(Boolean))];

    if (teamIds.length < 2) {
      this.suggestions = [];
      this.loading = false;
      this.error = 'No existing matches found for this event and sport';
      return;
    }

    const predictions: MatchSuggestion[] = [];

    for (let i = 0; i < teamIds.length; i++) {
      for (let j = i + 1; j < teamIds.length; j++) {
        const teamAId = teamIds[i];
        const teamBId = teamIds[j];
        const teamAStats = this.getTeamStats(scopedMatches, teamAId);
        const teamBStats = this.getTeamStats(scopedMatches, teamBId);
        const strengthA = teamAStats.form + teamAStats.attack - teamAStats.defense;
        const strengthB = teamBStats.form + teamBStats.attack - teamBStats.defense;
        const totalStrength = Math.max(0.1, Math.abs(strengthA) + Math.abs(strengthB));
        const closeness = 1 - Math.min(1, Math.abs(strengthA - strengthB) / totalStrength);
        const predictedWinnerId = strengthA >= strengthB ? teamAId : teamBId;
        const confidencePercent = 50 + closeness * 45;

        predictions.push({
          teamAId,
          teamBId,
          teamAName: teamAId,
          teamBName: teamBId,
          balanceScore: closeness,
          predictedWinnerId,
          confidencePercent,
          priorityScore: closeness * 0.7 + (confidencePercent / 100) * 0.3
        });
      }
    }

    this.suggestions = predictions.sort((left, right) => right.priorityScore - left.priorityScore).slice(0, 12);
    this.loading = false;

    this.success = this.suggestions.length > 0
      ? `Generated ${this.suggestions.length} balanced match suggestion(s)`
      : 'No balanced match suggestions available';
  }
  
  /**
   * Get color for balance score (green = balanced, red = unbalanced)
   */
  getBalanceColor(score: number): string {
    if (score > 0.8) return '#109D76'; // Green - excellent balance
    if (score > 0.6) return '#00B7D8'; // Cyan - good balance
    if (score > 0.4) return '#E08B12'; // Warning - moderate balance
    return '#D94848'; // Red - poor balance
  }
  
  /**
   * Get winner badge (emoji + percentage)
   */
  getWinnerBadge(suggestion: MatchSuggestion, teamId: string): string {
    if (suggestion.predictedWinnerId === teamId) {
      return `🏆 ${suggestion.confidencePercent.toFixed(0)}%`;
    }
    return '';
  }
  
  /**
   * Format balance score as percentage
   */
  formatBalancePercent(score: number): string {
    return (score * 100).toFixed(0);
  }
  
  /**
   * Clear all suggestions
   */
  clearSuggestions(): void {
    this.suggestions = [];
    this.error = null;
    this.success = null;
  }

  getEventLabel(eventId: string): string {
    return this.events.find(event => event.id === eventId)?.nom || eventId;
  }

  private getTeamStats(matches: Match[], teamId: string): { attack: number; defense: number; form: number } {
    const teamMatches = matches.filter(match => match.team1Id === teamId || match.team2Id === teamId);

    if (teamMatches.length === 0) {
      return { attack: 1.2, defense: 1.2, form: 0 };
    }

    let goalsFor = 0;
    let goalsAgainst = 0;
    let formScore = 0;

    teamMatches.forEach(match => {
      const isTeam1 = match.team1Id === teamId;
      const scored = isTeam1 ? match.scoreTeam1 : match.scoreTeam2;
      const conceded = isTeam1 ? match.scoreTeam2 : match.scoreTeam1;

      goalsFor += scored;
      goalsAgainst += conceded;

      if (scored > conceded) {
        formScore += 1;
      } else if (scored === conceded) {
        formScore += 0.4;
      } else {
        formScore -= 0.35;
      }
    });

    const matchesCount = teamMatches.length;
    return {
      attack: goalsFor / matchesCount || 1.2,
      defense: goalsAgainst / matchesCount || 1.2,
      form: formScore / matchesCount
    };
  }
}
