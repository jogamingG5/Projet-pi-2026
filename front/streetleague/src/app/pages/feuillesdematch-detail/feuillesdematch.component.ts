import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FeuillesDeMatchService } from '../../services/statistiques.service';
import { FeuillesDeMatch, RecapEquipe } from '../../models/statistiques.model';
import { EventService } from '../../services/event.service';
import { MatchService } from '../../services/match.service';
import { Event } from '../../models/event.model';
import { Match } from '../../models/match.model';

@Component({
  selector: 'app-feuillesdematch',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feuillesdematch.component.html',
  styleUrls: ['./feuillesdematch.component.css']
})
export class FeuillesDeMatchComponent implements OnInit {
  feuillesDeMatch: FeuillesDeMatch[] = [];
  selectedFeuille: FeuillesDeMatch | null = null;
  selectedMatch: Match | null = null;
  events: Event[] = [];
  matches: Match[] = [];
  matchId: string = '';
  loading: boolean = false;
  error: string = '';

  constructor(
    private feuillesDeMatchService: FeuillesDeMatchService,
    private eventService: EventService,
    private matchService: MatchService
  ) {}

  ngOnInit(): void {
    this.loadAllFeuillesDeMatch();
    this.loadEvents();
    this.loadMatches();
  }

  loadEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.events = data;
        if (!this.selectedFeuille && this.feuillesDeMatch.length > 0) {
          this.selectedFeuille = this.feuillesDeMatch[0];
          this.syncSelectedMatch(this.selectedFeuille.matchId);
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
        if (this.selectedFeuille) {
          this.syncSelectedMatch(this.selectedFeuille.matchId);
        } else if (this.feuillesDeMatch.length > 0) {
          this.selectedFeuille = this.feuillesDeMatch[0];
          this.syncSelectedMatch(this.selectedFeuille.matchId);
        }
      },
      error: () => {
        this.matches = [];
      }
    });
  }

  loadAllFeuillesDeMatch(): void {
    this.loading = true;
    this.error = '';
    this.feuillesDeMatchService.getAll().subscribe({
      next: (data) => {
        this.feuillesDeMatch = data;
        if (!this.selectedFeuille && data.length > 0) {
          this.selectedFeuille = data[0];
        }
        this.loading = false;
        if (this.selectedFeuille) {
          this.syncSelectedMatch(this.selectedFeuille.matchId);
        }
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des feuilles de match';
        console.error(err);
        this.loading = false;
      }
    });
  }

  searchByMatchId(): void {
    if (!this.matchId) {
      this.error = 'Veuillez remplir l\'ID du match';
      return;
    }

    this.loading = true;
    this.error = '';
    this.feuillesDeMatchService.getByMatchId(this.matchId).subscribe({
      next: (data) => {
        this.selectedFeuille = data;
        this.syncSelectedMatch(data.matchId);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Feuille de match non trouvée pour cet ID';
        console.error(err);
        this.loading = false;
      }
    });
  }

  selectFeuille(feuille: FeuillesDeMatch): void {
    this.selectedFeuille = feuille;
    this.syncSelectedMatch(feuille.matchId);
  }

  getMatchLabel(matchId: string): string {
    const match = this.matches.find(item => item.id === matchId);
    if (!match) return `Match #${matchId}`;
    const eventName = this.getEventName(match.eventId);
    return `${match.team1Id} vs ${match.team2Id} • ${match.sportId} • ${eventName}`;
  }

  getMatchSubtitle(matchId: string): string {
    const match = this.matches.find(item => item.id === matchId);
    if (!match) return 'Données du match non disponibles';
    return `${match.date} ${match.heure} • ${match.terrainId}`;
  }

  getEventName(eventId?: string): string {
    if (!eventId) return 'Sans événement';
    return this.events.find(event => event.id === eventId)?.nom || eventId;
  }

  private syncSelectedMatch(matchId: string): void {
    this.selectedMatch = this.matches.find(item => item.id === matchId) || null;
  }

  getTeamScore(teamId: string): number {
    if (!this.selectedFeuille) return 0;
    const team = this.selectedFeuille.recap?.find((e: RecapEquipe) => e.teamId === teamId);
    return team?.score || 0;
  }

  getTeamYellowCards(teamId: string): number {
    if (!this.selectedFeuille) return 0;
    const team = this.selectedFeuille.recap?.find((e: RecapEquipe) => e.teamId === teamId);
    return team?.nbCartesJaunes || team?.playerbookedYellowCards?.length || 0;
  }

  getTeamRedCards(teamId: string): number {
    if (!this.selectedFeuille) return 0;
    const team = this.selectedFeuille.recap?.find((e: RecapEquipe) => e.teamId === teamId);
    return team?.nbCartesRouges || team?.playerbookedRedCards?.length || 0;
  }

  deleteFeuille(id: string): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette feuille de match ?')) {
      this.feuillesDeMatchService.delete(id).subscribe({
        next: () => {
          this.feuillesDeMatch = this.feuillesDeMatch.filter(f => f.id !== id);
          this.selectedFeuille = null;
          this.error = '';
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
          console.error(err);
        }
      });
    }
  }

  exportToPDF(): void {
    if (!this.selectedFeuille) return;
    // Implementation pour exporter en PDF
    console.log('Export PDF de la feuille de match:', this.selectedFeuille.id);
  }
}
