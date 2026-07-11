import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FeuillesDeMatchService } from '../../services/statistiques.service';
import { FeuillesDeMatch, RecapEquipe } from '../../models/statistiques.model';
import { EventService } from '../../services/event.service';
import { MatchService } from '../../services/match.service';
import { Event } from '../../models/event.model';
import { Match } from '../../models/match.model';
import { extractErrorMessage } from '../../utils/http-error';
import { ExportMenuComponent } from '../../components/export-menu.component';

@Component({
  selector: 'app-feuillesdematch',
  standalone: true,
  imports: [CommonModule, FormsModule, ExportMenuComponent],
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
    private matchService: MatchService,
    private cdr: ChangeDetectorRef
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
        this.cdr.markForCheck();
      },
      error: () => {
        this.events = [];
        this.cdr.markForCheck();
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
        this.cdr.markForCheck();
      },
      error: () => {
        this.matches = [];
        this.cdr.markForCheck();
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
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = extractErrorMessage(err, 'Erreur lors du chargement des feuilles de match');
        this.loading = false;
        this.cdr.markForCheck();
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
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = extractErrorMessage(err, 'Feuille de match non trouvée pour cet ID');
        this.loading = false;
        this.cdr.markForCheck();
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
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.error = extractErrorMessage(err, 'Erreur lors de la suppression');
          this.cdr.markForCheck();
        }
      });
    }
  }

  exportToPDF(): void {
    const feuille = this.selectedFeuille;
    if (!feuille) return;

    const match = this.matches.find(item => item.id === feuille.matchId);
    const title = this.getMatchLabel(feuille.matchId);
    const subtitle = this.getMatchSubtitle(feuille.matchId);

    const rows = (feuille.recap || []).map((r: RecapEquipe) => `
      <tr>
        <td class="team">${this.escapeHtml(r.teamId)}</td>
        <td class="num">${this.getTeamScore(r.teamId)}</td>
        <td class="num">${this.getTeamYellowCards(r.teamId)}</td>
        <td class="num">${this.getTeamRedCards(r.teamId)}</td>
      </tr>`).join('');

    const scoreLine = match ? `${match.scoreTeam1} – ${match.scoreTeam2}` : '';

    const html = `<!doctype html>
<html lang="fr"><head><meta charset="utf-8">
<title>Feuille de match — ${this.escapeHtml(title)}</title>
<style>
  * { box-sizing: border-box; }
  body { font-family: 'Segoe UI', Arial, sans-serif; color: #111; margin: 40px; }
  .brand { font-size: 12px; letter-spacing: .18em; text-transform: uppercase; color: #2f7dff; font-weight: 800; }
  h1 { font-size: 22px; margin: 6px 0 2px; }
  .sub { color: #555; margin-bottom: 4px; }
  .score { font-size: 34px; font-weight: 900; margin: 14px 0 22px; }
  table { width: 100%; border-collapse: collapse; margin-top: 8px; }
  th, td { padding: 10px 12px; border-bottom: 1px solid #ddd; text-align: left; }
  th { font-size: 11px; text-transform: uppercase; letter-spacing: .05em; color: #666; }
  td.num, th.num { text-align: center; }
  td.team { font-weight: 700; }
  .footer { margin-top: 32px; font-size: 11px; color: #999; }
  @media print { body { margin: 20px; } }
</style></head>
<body>
  <div class="brand">StreetLeague · Feuille de match</div>
  <h1>${this.escapeHtml(title)}</h1>
  <div class="sub">${this.escapeHtml(subtitle)}</div>
  ${scoreLine ? `<div class="score">${scoreLine}</div>` : ''}
  <table>
    <thead>
      <tr><th>Équipe</th><th class="num">Score</th><th class="num">Cartons jaunes</th><th class="num">Cartons rouges</th></tr>
    </thead>
    <tbody>${rows}</tbody>
  </table>
  <div class="footer">Feuille #${this.escapeHtml(feuille.id)} — généré depuis StreetLeague</div>
</body></html>`;

    const printWindow = window.open('', '_blank', 'width=900,height=700');
    if (!printWindow) {
      this.error = 'Autorisez les pop-ups pour exporter en PDF';
      return;
    }
    printWindow.document.open();
    printWindow.document.write(html);
    printWindow.document.close();
    printWindow.focus();
    // Laisse le navigateur peindre le contenu avant d'ouvrir la boîte d'impression.
    printWindow.onload = () => printWindow.print();
  }

  private escapeHtml(value: string): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
}
