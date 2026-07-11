import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StatistiquesService } from '../../services/statistiques.service';
import { Statistiques } from '../../models/statistiques.model';
import { EventService } from '../../services/event.service';
import { MatchService } from '../../services/match.service';
import { Event } from '../../models/event.model';
import { Match } from '../../models/match.model';
import { finalize } from 'rxjs';
import { ExportMenuComponent } from '../../components/export-menu.component';

@Component({
  selector: 'app-statistiques',
  standalone: true,
  imports: [CommonModule, FormsModule, ExportMenuComponent],
  templateUrl: './statistiques.component.html',
  styleUrls: ['./statistiques.component.css']
})
export class StatistiquesComponent implements OnInit {
  statistiques: Statistiques[] = [];
  events: Event[] = [];
  matches: Match[] = [];
  selectedStatistiques: Statistiques | null = null;
  teamId: string = '';
  sportId: string = '';
  selectedEventId: string = '';
  loading: boolean = false;
  error: string = '';
  filterBy: 'all' | 'team' | 'sport' = 'all';

  constructor(
    private statistiquesService: StatistiquesService,
    private eventService: EventService,
    private matchService: MatchService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAllStatistiques();
    this.loadEvents();
    this.loadMatches();
  }

  loadAllStatistiques(): void {
    this.loading = true;
    this.error = '';
    this.statistiquesService.getAll().pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (data) => {
        this.statistiques = data;
        if (!this.selectedStatistiques && data.length > 0) {
          this.selectedStatistiques = data[0];
        }
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des statistiques';
        console.error(err);
      }
    });
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.events = data;
        if (!this.selectedEventId && data.length > 0) {
          this.selectedEventId = data[0].id;
          if (!this.selectedStatistiques) {
            this.selectedStatistiques = this.filteredStatistiques()[0] || this.statistiques[0] || null;
          }
        }
      },
      error: () => {
        this.events = [];
      }
    });
  }

  loadMatches(): void {
    this.matchService.getMatches().subscribe({
      next: (data) => {
        this.matches = data;
      },
      error: () => {
        this.matches = [];
      }
    });
  }

  searchByTeamAndSport(): void {
    if (!this.teamId || !this.sportId) {
      this.error = 'Veuillez remplir l\'équipe et le sport';
      return;
    }

    this.loading = true;
    this.error = '';
    this.statistiquesService.getByTeamAndSport(this.teamId, this.sportId).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (data) => {
        this.selectedStatistiques = data;
      },
      error: (err) => {
        this.error = 'Statistiques non trouvées pour ces critères';
        console.error(err);
      }
    });
  }

  searchByTeam(): void {
    if (!this.teamId) {
      this.error = 'Veuillez remplir l\'équipe';
      return;
    }

    this.loading = true;
    this.error = '';
    this.statistiquesService.getByTeam(this.teamId).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (data) => {
        this.statistiques = data;
        if (data.length > 0) {
          this.selectedStatistiques = data[0];
        }
      },
      error: (err) => {
        this.error = 'Statistiques non trouvées pour cette équipe';
        console.error(err);
      }
    });
  }

  searchBySport(): void {
    if (!this.sportId) {
      this.error = 'Veuillez remplir le sport';
      return;
    }

    this.loading = true;
    this.error = '';
    this.statistiquesService.getBySport(this.sportId).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (data) => {
        this.statistiques = data;
        if (data.length > 0) {
          this.selectedStatistiques = data[0];
        }
      },
      error: (err) => {
        this.error = 'Statistiques non trouvées pour ce sport';
        console.error(err);
      }
    });
  }

  selectStatistiques(stats: Statistiques): void {
    this.selectedStatistiques = stats;
  }

  onEventChange(event: globalThis.Event): void {
    this.selectedEventId = (event.target as HTMLSelectElement).value;
    this.selectedStatistiques = this.filteredStatistiques()[0] || null;
  }

  get selectedEvent(): Event | null {
    return this.events.find(event => event.id === this.selectedEventId) || null;
  }

  get linkedMatches(): Match[] {
    const selectedEvent = this.selectedEvent;
    if (!selectedEvent) {
      return [];
    }

    return this.matches.filter(match => match.eventId === selectedEvent.id);
  }

  filteredStatistiques(): Statistiques[] {
    if (!this.selectedEvent) {
      return this.statistiques;
    }

    const eventTeams = new Set(this.selectedEvent.teamsIds || []);
    return this.statistiques.filter(stats =>
      eventTeams.has(stats.teamId) && stats.sportId === this.selectedEvent?.sportId
    );
  }

  getEventName(eventId: string): string {
    return this.events.find(event => event.id === eventId)?.nom || eventId;
  }

  getMatchLabel(match: Match): string {
    return `${match.team1Id} vs ${match.team2Id}`;
  }

  clearEventFilter(): void {
    this.selectedEventId = '';
    this.selectedStatistiques = this.statistiques[0] || null;
  }

  getPerformanceClass(taux: number): string {
    if (taux >= 70) return 'bg-green-100 text-green-800';
    if (taux >= 50) return 'bg-blue-100 text-blue-800';
    if (taux >= 30) return 'bg-orange-100 text-orange-800';
    return 'bg-red-100 text-red-800';
  }

  getPerformanceIcon(taux: number): string {
    if (taux >= 70) return '⭐';
    if (taux >= 50) return '✓';
    return '✗';
  }

  getNbPointsTotal(stats: Statistiques): number {
    return stats.nbVictoires * 3 + stats.nbNuls;
  }

  getDifferenceButsGoal(stats: Statistiques): number {
    return stats.nbButsMarques - stats.nbButsEncaisses;
  }

  getTauxVictoire(stats: Statistiques): number {
    return stats.nbMatchsJoues > 0 ? (stats.nbVictoires / stats.nbMatchsJoues) * 100 : 0;
  }

  getMoyenneButsMarques(stats: Statistiques): number {
    return stats.nbMatchsJoues > 0 ? stats.nbButsMarques / stats.nbMatchsJoues : 0;
  }

  getMoyenneButsEncaisses(stats: Statistiques): number {
    return stats.nbMatchsJoues > 0 ? stats.nbButsEncaisses / stats.nbMatchsJoues : 0;
  }
}
