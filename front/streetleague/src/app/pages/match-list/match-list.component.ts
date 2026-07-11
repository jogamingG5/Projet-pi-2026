import { Component, OnInit, signal, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchService } from '../../services/match.service';
import { EventService } from '../../services/event.service';
import { Match, MatchStatus } from '../../models/match.model';
import { Event } from '../../models/event.model';
import { BadgeComponent } from '../../components/badge.component';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';
import { ConfirmDialogComponent } from '../../components/confirm-dialog.component';
import { ToastContainerComponent } from '../../components/toast.component';
import { MatchModalComponent } from './match-modal.component';

interface MatchPrediction {
  matchId: string;
  eventName: string;
  scoreTeam1: number;
  scoreTeam2: number;
  confidence: number;
  probabilities: {
    team1: number;
    draw: number;
    team2: number;
  };
  rationale: string[];
}

@Component({
  selector: 'app-match-list',
  standalone: true,
  imports: [
    CommonModule,
    BadgeComponent,
    LoadingSpinnerComponent,
    ConfirmDialogComponent,
    ToastContainerComponent,
    MatchModalComponent
  ],
  templateUrl: './match-list.component.html',
  styleUrls: ['./match-list.component.css']
})
export class MatchListComponent implements OnInit {
  private matchService = inject(MatchService);
  private eventService = inject(EventService);
  @ViewChild(ToastContainerComponent) toastContainer!: ToastContainerComponent;
  @ViewChild(MatchModalComponent) matchModal!: MatchModalComponent;

  matches = signal<Match[]>([]);
  events = signal<Event[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  selectedFilter = signal<MatchStatus | 'ALL'>('ALL');
  selectedSport = signal('ALL');
  selectedDate = signal('');
  showConfirmDelete = signal(false);
  matchToDelete = signal<string | null>(null);
  showModal = signal(false);
  selectedMatch = signal<Match | null>(null);
  selectedPrediction = signal<MatchPrediction | null>(null);

  filters: (MatchStatus | 'ALL')[] = ['ALL', 'SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED'];

  ngOnInit(): void {
    this.loadMatches();
    this.loadEvents();
  }

  loadMatches(): void {
    this.loading.set(true);
    this.error.set(null);

    this.matchService.getMatches().subscribe({
      next: (data) => {
        this.matches.set(data);
        console.log('Matches loaded:', JSON.stringify(data, null, 2));
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load matches');
        this.toastContainer?.show('Error loading matches', 'error');
        this.loading.set(false);
      }
    });
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.events.set(data);
      },
      error: () => {
        this.events.set([]);
      }
    });
  }

  getFilteredMatches(): Match[] {
    let filtered = this.matches();

    const filter = this.selectedFilter();
    if (filter !== 'ALL') {
      filtered = filtered.filter(m => m.status === filter);
    }

    if (this.selectedSport() !== 'ALL') {
      filtered = filtered.filter(m => m.sportId === this.selectedSport());
    }

    if (this.selectedDate()) {
      filtered = filtered.filter(m => this.normalizeDate(m.date) === this.selectedDate());
    }

    return filtered;
  }

  onFilterChange(filter: MatchStatus | 'ALL'): void {
    this.selectedFilter.set(filter);
  }

  onSportChange(event: globalThis.Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedSport.set(value);
  }

  onDateChange(event: globalThis.Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.selectedDate.set(value);
  }

  clearFilters(): void {
    this.selectedFilter.set('ALL');
    this.selectedSport.set('ALL');
    this.selectedDate.set('');
  }

  togglePrediction(match: Match): void {
    this.selectedMatch.set(match);

    const current = this.selectedPrediction();
    if (current?.matchId === match.id) {
      this.selectedPrediction.set(null);
      return;
    }

    this.selectedPrediction.set(this.buildPrediction(match));
  }

  closePrediction(): void {
    this.selectedPrediction.set(null);
  }

  availableTeamSuggestions(): string[] {
    const selectedEvent = this.events().find(event => event.id === this.selectedMatch()?.eventId);
    const sport = this.selectedMatch()?.sportId;

    const matchTeams = this.matches()
      .filter(match => !sport || match.sportId === sport)
      .flatMap(match => [match.team1Id, match.team2Id]);

    const eventTeams = selectedEvent?.teamsIds || [];

    return [...new Set([...eventTeams, ...matchTeams].filter(Boolean))].sort();
  }

  availableTerrainSuggestions(): string[] {
    const sport = this.selectedMatch()?.sportId;
    return [...new Set(
      this.matches()
        .filter(match => !sport || match.sportId === sport)
        .map(match => match.terrainId)
        .filter(Boolean)
    )].sort();
  }

  availableSports(): string[] {
    const eventSports = this.events().map(event => event.sportId).filter(Boolean);
    const matchSports = this.matches().map(match => match.sportId).filter(Boolean);
    return [...new Set([...eventSports, ...matchSports])].sort();
  }

  availableEventSuggestions(): Event[] {
    const sport = this.selectedMatch()?.sportId;
    return this.events().filter(event => !sport || event.sportId === sport);
  }

  getEventName(match: Match): string {
    return this.events().find(event => event.id === match.eventId)?.nom || match.eventId || 'No event';
  }

  getPredictionFor(match: Match): MatchPrediction {
    return this.buildPrediction(match);
  }

  openModal(match?: Match): void {
    if (match) {
      this.selectedMatch.set(match);
    } else {
      this.selectedMatch.set(null);
    }
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.selectedMatch.set(null);
  }

  onMatchSaved(): void {
    this.closeModal();
    this.loadMatches();
    this.toastContainer?.show('Match saved successfully', 'success');
  }

  onEditMatch(match: Match): void {
    this.openModal(match);
  }

  onDeleteMatch(id: string): void {
    this.matchToDelete.set(id);
    this.showConfirmDelete.set(true);
  }

  confirmDelete(): void {
    const id = this.matchToDelete();
    if (!id) return;

    this.matchService.deleteMatch(id).subscribe({
      next: () => {
        this.loadMatches();
        this.showConfirmDelete.set(false);
        this.matchToDelete.set(null);
        this.toastContainer?.show('Match deleted successfully', 'success');
      },
      error: () => {
        this.toastContainer?.show('Error deleting match', 'error');
        this.showConfirmDelete.set(false);
      }
    });
  }

  cancelDelete(): void {
    this.showConfirmDelete.set(false);
    this.matchToDelete.set(null);
  }

  formatDate(dateValue: any): string {
    try {
      // Handle null and undefined
      if (dateValue === null || dateValue === undefined || dateValue === '') {
        return '--';
      }
      
      let year, month, day;
      
      // Handle array format [year, month, day] (Java LocalDate serialization)
      if (Array.isArray(dateValue)) {
        [year, month, day] = dateValue;
      } 
      // Handle ISO 8601 string format (YYYY-MM-DDTHH:mm:ss.SSS or YYYY-MM-DD)
      else if (typeof dateValue === 'string') {
        // Extract just the date part (YYYY-MM-DD)
        const datePart = dateValue.split('T')[0];
        const parts = datePart.split('-');
        if (parts.length === 3) {
          [year, month, day] = parts.map(Number);
        } else {
          return '--';
        }
      } 
      // Handle Date object
      else if (dateValue instanceof Date) {
        year = dateValue.getFullYear();
        month = dateValue.getMonth() + 1;
        day = dateValue.getDate();
      } 
      else {
        return '--';
      }
      
      // Create date without timezone issues
      const dateObj = new Date(year, month - 1, day);
      return dateObj.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch (error) {
      console.error('Date formatting error:', error, dateValue);
      return '--';
    }
  }

  formatTime(timeString: string): string {
    return timeString; // Already in HH:mm format
  }

  private buildPrediction(match: Match): MatchPrediction {
    const sameSportMatches = this.matches().filter(item => item.sportId === match.sportId);
    const completedMatches = sameSportMatches.filter(item => item.status === 'COMPLETED' && Number.isFinite(item.scoreTeam1) && Number.isFinite(item.scoreTeam2));
    const relevantMatches = completedMatches.length >= 3 ? completedMatches : sameSportMatches.filter(item => Number.isFinite(item.scoreTeam1) && Number.isFinite(item.scoreTeam2));

    const team1Stats = this.getTeamStats(relevantMatches, match.team1Id);
    const team2Stats = this.getTeamStats(relevantMatches, match.team2Id);

    const event = this.events().find(item => item.id === match.eventId);
    const eventBoost = event ? 0.15 : 0;

    const expectedTeam1Goals = this.clamp(
      (team1Stats.attack + team2Stats.defense) / 2 + team1Stats.form * 0.15 + eventBoost,
      0,
      5
    );
    const expectedTeam2Goals = this.clamp(
      (team2Stats.attack + team1Stats.defense) / 2 + team2Stats.form * 0.15,
      0,
      5
    );

    const baseStrength1 = Math.max(0.1, expectedTeam1Goals + team1Stats.form * 0.35);
    const baseStrength2 = Math.max(0.1, expectedTeam2Goals + team2Stats.form * 0.35);
    const totalStrength = baseStrength1 + baseStrength2;
    const diff = Math.abs(baseStrength1 - baseStrength2);

    let drawPercent = this.clamp(33 - diff * 5 - (expectedTeam1Goals + expectedTeam2Goals - 2.5) * 2, 12, 38);
    let remaining = 100 - drawPercent;
    let team1Percent = remaining * (baseStrength1 / totalStrength);
    let team2Percent = remaining - team1Percent;

    team1Percent = this.clamp(team1Percent, 8, 88);
    team2Percent = this.clamp(team2Percent, 8, 88);

    const normalizedTotal = team1Percent + drawPercent + team2Percent;
    if (normalizedTotal !== 100) {
      const delta = 100 - normalizedTotal;
      team1Percent += delta;
    }

    const predictedScoreTeam1 = this.roundScore(expectedTeam1Goals);
    const predictedScoreTeam2 = this.roundScore(expectedTeam2Goals);
    const confidence = this.clamp(58 + Math.abs(team1Percent - team2Percent) * 0.45 + eventBoost * 100, 55, 92);

    return {
      matchId: match.id,
      eventName: event?.nom || match.eventId || 'No event',
      scoreTeam1: predictedScoreTeam1,
      scoreTeam2: predictedScoreTeam2,
      confidence,
      probabilities: {
        team1: Math.round(team1Percent),
        draw: Math.round(drawPercent),
        team2: Math.round(team2Percent)
      },
      rationale: [
        `Sport: ${match.sportId}`,
        `Recent form: ${team1Stats.form.toFixed(1)} vs ${team2Stats.form.toFixed(1)}`,
        event ? `Event boost from ${event.nom}` : 'No linked event found'
      ]
    };
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

  private roundScore(value: number): number {
    return Math.max(0, Math.min(6, Math.round(value)));
  }

  private clamp(value: number, min: number, max: number): number {
    return Math.min(max, Math.max(min, value));
  }

  private normalizeDate(dateValue: any): string {
    if (!dateValue) {
      return '';
    }

    if (Array.isArray(dateValue)) {
      const [year, month, day] = dateValue;
      return `${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
    }

    if (typeof dateValue === 'string') {
      return dateValue.split('T')[0];
    }

    if (dateValue instanceof Date) {
      const year = dateValue.getFullYear();
      const month = `${dateValue.getMonth() + 1}`.padStart(2, '0');
      const day = `${dateValue.getDate()}`.padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

    return '';
  }
}
