import { Component, input, output, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatchService } from '../../services/match.service';
import { Event } from '../../models/event.model';
import { Match, MatchRequest, MatchStatus, MatchType } from '../../models/match.model';

@Component({
  selector: 'app-match-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (isOpen()) {
      <div class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-slate-950/70 backdrop-blur-md p-4 sm:items-center">
        <div class="modal-shell w-full max-w-5xl overflow-hidden max-h-[92vh] flex flex-col">
          <div class="modal-header">
            <div>
              <div class="modal-eyebrow">Match editor</div>
              <h2 class="modal-title">
                {{ isEditMode() ? 'Modify match' : 'Create match' }}
              </h2>
              <p class="modal-subtitle">
                Same polished structure as the event modal, with internal scrolling and grouped inputs.
              </p>
            </div>
            <button type="button" (click)="onCancel()" class="btn-secondary modal-close">✕</button>
          </div>

          <div class="modal-body">
            @if (error()) {
              <div class="modal-alert">
                {{ error() }}
              </div>
            }

            <form (ngSubmit)="onSubmit()" class="modal-form">
              <section class="modal-section">
                <div class="section-head">
                  <h3>Teams and result</h3>
                  <p>Identify both teams and their scores</p>
                </div>

                <div class="modal-grid modal-grid-2">
                  <div class="form-group required">
                    <label class="label">Team 1 ID</label>
                    <input
                      class="input-field"
                      type="text"
                      [(ngModel)]="formData.team1Id"
                      (blur)="validateField('team1Id')"
                      name="team1Id"
                      required
                      minlength="1"
                      placeholder="Ex: team-001"
                      list="team-suggestions"
                    />
                    <datalist id="team-suggestions">
                      @for (team of filteredTeamSuggestions(); track team) {
                        <option [value]="team"></option>
                      }
                    </datalist>
                    <div class="suggestions-block">
                      <span class="suggestions-label">Suggestions</span>
                      <div class="suggestion-list">
                        @for (team of filteredTeamSuggestions(); track team) {
                          <button type="button" class="suggestion-chip" (click)="selectTeam(1, team)">
                            {{ team }}
                          </button>
                        }
                      </div>
                    </div>
                    @if (fieldErrors()['team1Id']) {
                      <p class="form-error">{{ fieldErrors()['team1Id'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Team 2 ID</label>
                    <input
                      class="input-field"
                      type="text"
                      [(ngModel)]="formData.team2Id"
                      (blur)="validateField('team2Id')"
                      name="team2Id"
                      required
                      minlength="1"
                      placeholder="Ex: team-002"
                      list="team-suggestions-2"
                    />
                    <datalist id="team-suggestions-2">
                      @for (team of filteredTeamSuggestions(); track team) {
                        <option [value]="team"></option>
                      }
                    </datalist>
                    <div class="suggestions-block">
                      <span class="suggestions-label">Suggestions</span>
                      <div class="suggestion-list">
                        @for (team of filteredTeamSuggestions(); track team) {
                          <button type="button" class="suggestion-chip" (click)="selectTeam(2, team)">
                            {{ team }}
                          </button>
                        }
                      </div>
                    </div>
                    @if (fieldErrors()['team2Id']) {
                      <p class="form-error">{{ fieldErrors()['team2Id'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Team 1 Score</label>
                    <input
                      class="input-field"
                      type="number"
                      [(ngModel)]="formData.scoreTeam1"
                      (blur)="validateField('scoreTeam1')"
                      name="scoreTeam1"
                      min="0"
                      placeholder="0"
                    />
                    @if (fieldErrors()['scoreTeam1']) {
                      <p class="form-error">{{ fieldErrors()['scoreTeam1'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Team 2 Score</label>
                    <input
                      class="input-field"
                      type="number"
                      [(ngModel)]="formData.scoreTeam2"
                      (blur)="validateField('scoreTeam2')"
                      name="scoreTeam2"
                      min="0"
                      placeholder="0"
                    />
                    @if (fieldErrors()['scoreTeam2']) {
                      <p class="form-error">{{ fieldErrors()['scoreTeam2'] }}</p>
                    }
                  </div>
                </div>
              </section>

              <section class="modal-section">
                <div class="section-head">
                  <h3>Planning</h3>
                  <p>Link the match to an event and sport, then choose the field</p>
                </div>

                <div class="modal-grid modal-grid-2">
                  <div class="form-group required">
                    <label class="label">Event</label>
                    <input
                      class="input-field"
                      type="text"
                      [(ngModel)]="formData.eventDisplay"
                      (blur)="syncEventSelection()"
                      name="eventDisplay"
                      required
                      placeholder="Search or choose an event"
                      list="event-suggestions"
                    />
                    <datalist id="event-suggestions">
                      @for (event of filteredEventSuggestions(); track event.id) {
                        <option [value]="event.nom"></option>
                      }
                    </datalist>
                    <div class="suggestions-block">
                      <span class="suggestions-label">Suggestions</span>
                      <div class="suggestion-list">
                        @for (event of filteredEventSuggestions(); track event.id) {
                          <button type="button" class="suggestion-chip" (click)="selectEvent(event)">
                            {{ event.nom }} · {{ event.sportId }}
                          </button>
                        }
                      </div>
                    </div>
                    @if (fieldErrors()['eventId']) {
                      <p class="form-error">{{ fieldErrors()['eventId'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Sport ID</label>
                    <input
                      class="input-field"
                      type="text"
                      [(ngModel)]="formData.sportId"
                      (blur)="validateField('sportId')"
                      name="sportId"
                      required
                      minlength="1"
                      placeholder="Example: football"
                      list="sport-suggestions"
                    />
                    <datalist id="sport-suggestions">
                      @for (sport of filteredSportSuggestions(); track sport) {
                        <option [value]="sport"></option>
                      }
                    </datalist>
                    <div class="suggestions-block">
                      <span class="suggestions-label">Suggestions</span>
                      <div class="suggestion-list">
                        @for (sport of filteredSportSuggestions(); track sport) {
                          <button type="button" class="suggestion-chip" (click)="selectSport(sport)">
                            {{ sport }}
                          </button>
                        }
                      </div>
                    </div>
                    @if (fieldErrors()['sportId']) {
                      <p class="form-error">{{ fieldErrors()['sportId'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Match date</label>
                    <input
                      class="input-field"
                      type="date"
                      [(ngModel)]="formData.date"
                      (blur)="validateField('date')"
                      name="date"
                      required
                      [min]="minDate"
                    />
                    @if (fieldErrors()['date']) {
                      <p class="form-error">{{ fieldErrors()['date'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Match time</label>
                    <input
                      class="input-field"
                      type="time"
                      [(ngModel)]="formData.heure"
                      (blur)="validateField('heure')"
                      name="heure"
                      required
                    />
                    @if (fieldErrors()['heure']) {
                      <p class="form-error">{{ fieldErrors()['heure'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Terrain ID</label>
                    <input
                      class="input-field"
                      type="text"
                      [(ngModel)]="formData.terrainId"
                      (blur)="validateField('terrainId')"
                      name="terrainId"
                      required
                      minlength="1"
                      placeholder="Ex: terrain-001"
                      list="terrain-suggestions"
                    />
                    <datalist id="terrain-suggestions">
                      @for (terrain of filteredTerrainSuggestions(); track terrain) {
                        <option [value]="terrain"></option>
                      }
                    </datalist>
                    <div class="suggestions-block">
                      <span class="suggestions-label">Suggestions</span>
                      <div class="suggestion-list">
                        @for (terrain of filteredTerrainSuggestions(); track terrain) {
                          <button type="button" class="suggestion-chip" (click)="formData.terrainId = terrain">
                            {{ terrain }}
                          </button>
                        }
                      </div>
                    </div>
                    @if (fieldErrors()['terrainId']) {
                      <p class="form-error">{{ fieldErrors()['terrainId'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Match type</label>
                    <select
                      class="input-field"
                      [(ngModel)]="formData.type"
                      name="type"
                      required
                    >
                      <option value="">Select a type</option>
                      <option value="LEAGUE">League</option>
                      <option value="FRIENDLY">Friendly</option>
                    </select>
                    @if (fieldErrors()['type']) {
                      <p class="form-error">{{ fieldErrors()['type'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">Status</label>
                    <select
                      class="input-field"
                      [(ngModel)]="formData.status"
                      name="status"
                      required
                    >
                      <option value="">Select a status</option>
                      <option value="SCHEDULED">Scheduled</option>
                      <option value="ONGOING">Ongoing</option>
                      <option value="COMPLETED">Completed</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>
                    @if (fieldErrors()['status']) {
                      <p class="form-error">{{ fieldErrors()['status'] }}</p>
                    }
                  </div>
                </div>
              </section>

              <div class="modal-actions">
                <button type="button" (click)="onCancel()" class="btn-secondary">
                  Cancel
                </button>
                <button type="submit" [disabled]="loading() || !isFormValid()" class="btn-primary">
                  {{ loading() ? 'Saving...' : 'Save match' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .modal-shell {
      background: linear-gradient(180deg, rgba(255,255,255,0.98), rgba(245,249,255,0.98));
      border: 1px solid rgba(185, 197, 214, 0.7);
      border-radius: 28px;
      box-shadow: 0 30px 80px rgba(11, 17, 32, 0.34);
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 1rem;
      padding: 1.25rem 1.5rem;
      border-bottom: 1px solid rgba(185, 197, 214, 0.6);
      background: linear-gradient(135deg, rgba(15, 122, 229, 0.08), rgba(0, 183, 216, 0.05));
    }

    .modal-eyebrow {
      font-size: 0.75rem;
      font-weight: 800;
      text-transform: uppercase;
      letter-spacing: 0.14em;
      color: #5b6b7e;
      margin-bottom: 0.35rem;
    }

    .modal-title {
      font-size: clamp(1.5rem, 2vw, 2.1rem);
      font-weight: 900;
      color: #102033;
      line-height: 1.1;
      margin: 0;
    }

    .modal-subtitle {
      color: #5b6b7e;
      margin-top: 0.4rem;
      max-width: 44rem;
    }

    .modal-close {
      flex: 0 0 auto;
      width: 2.75rem;
      height: 2.75rem;
      padding: 0;
      border-radius: 999px;
    }

    .modal-body {
      overflow-y: auto;
      padding: 1.25rem 1.5rem 1.5rem;
    }

    .modal-form {
      display: grid;
      gap: 1rem;
    }

    .modal-section {
      background: rgba(255, 255, 255, 0.9);
      border: 1px solid rgba(185, 197, 214, 0.72);
      border-radius: 22px;
      padding: 1.1rem;
      box-shadow: 0 10px 24px rgba(16, 32, 51, 0.05);
    }

    .section-head {
      margin-bottom: 1rem;
    }

    .section-head h3 {
      font-size: 0.92rem;
      font-weight: 900;
      letter-spacing: 0.12em;
      text-transform: uppercase;
      color: #27415d;
      margin-bottom: 0.25rem;
    }

    .section-head p {
      color: #5b6b7e;
      font-size: 0.92rem;
    }

    .modal-grid {
      display: grid;
      gap: 1rem;
    }

    .modal-grid-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 0.75rem;
      padding-top: 0.25rem;
    }

    .modal-alert {
      margin-bottom: 1rem;
      padding: 0.9rem 1rem;
      border-radius: 16px;
      border: 1px solid rgba(216, 73, 73, 0.25);
      background: rgba(255, 239, 239, 0.95);
      color: #8e2d2d;
      font-weight: 700;
    }

    @media (max-width: 720px) {
      .modal-grid-2 {
        grid-template-columns: 1fr;
      }

      .modal-header,
      .modal-body {
        padding-left: 1rem;
        padding-right: 1rem;
      }

      .modal-actions {
        flex-direction: column-reverse;
      }

      .modal-actions button {
        width: 100%;
      }
    }
  `]
})
export class MatchModalComponent {
  private matchService = inject(MatchService);

  isOpen = input(false);
  match = input<Match | null>(null);
  close = output<void>();
  saved = output<void>();
  eventSuggestions = input<Event[]>([]);
  matchSuggestions = input<Match[]>([]);

  // Date validation
  minDate = this.getTodayDate();

  form = signal<MatchRequest>({
    eventId: '',
    team1Id: '',
    team2Id: '',
    scoreTeam1: 0,
    scoreTeam2: 0,
    terrainId: '',
    date: '',
    heure: '',
    sportId: '',
    status: 'SCHEDULED' as MatchStatus,
    type: 'LEAGUE' as MatchType
  });

  loading = signal(false);
  error = signal<string | null>(null);
  fieldErrors = signal<Record<string, string>>({});
  isEditMode = signal(false);

  // Simpler form reference for template
  formData = {
    eventId: '',
    eventDisplay: '',
    team1Id: '',
    team2Id: '',
    scoreTeam1: 0,
    scoreTeam2: 0,
    terrainId: '',
    date: '',
    heure: '',
    sportId: '',
    status: 'SCHEDULED' as MatchStatus,
    type: 'LEAGUE' as MatchType
  };

  constructor() {
    effect(() => {
      const matchData = this.match();
      if (matchData) {
        const event = this.eventSuggestions().find(item => item.id === matchData.eventId);
        this.formData = {
          ...matchData,
          eventId: matchData.eventId || '',
          eventDisplay: event?.nom || matchData.eventId || ''
        };
        this.form.set(this.formData as any);
        this.isEditMode.set(true);
      } else {
        this.resetForm();
        this.isEditMode.set(false);
      }
    });
  }

  filteredEventSuggestions(): Event[] {
    const sport = this.formData.sportId?.trim();

    return this.eventSuggestions()
      .filter(event => !sport || event.sportId === sport)
      .slice(0, 8);
  }

  filteredSportSuggestions(): string[] {
    const sports = new Set<string>();

    for (const event of this.eventSuggestions()) {
      if (event.sportId) {
        sports.add(event.sportId);
      }
    }

    for (const match of this.matchSuggestions()) {
      if (match.sportId) {
        sports.add(match.sportId);
      }
    }

    return [...sports].sort().slice(0, 8);
  }

  filteredTeamSuggestions(): string[] {
    const sport = this.formData.sportId?.trim();
    const event = this.eventSuggestions().find(item => item.id === this.formData.eventId);

    const source = [...new Set([
      ...(event?.teamsIds || []),
      ...this.matchSuggestions()
        .filter(match => !sport || match.sportId === sport)
        .flatMap(match => [match.team1Id, match.team2Id]),
      'team_1',
      'team_2',
      'team_3',
      'team_4',
      'team_5'
    ])];

    return source
      .filter(team => team !== this.formData.team1Id && team !== this.formData.team2Id)
      .slice(0, 8);
  }

  filteredTerrainSuggestions(): string[] {
    const sport = this.formData.sportId?.trim();
    const source = [...new Set([
      ...this.matchSuggestions()
        .filter(match => !sport || match.sportId === sport)
        .map(match => match.terrainId),
      'terrain-001',
      'terrain-002',
      'terrain-003'
    ])];

    return source.slice(0, 8);
  }

  selectEvent(event: Event): void {
    this.formData.eventId = event.id;
    this.formData.eventDisplay = event.nom;

    if (!this.formData.sportId?.trim()) {
      this.formData.sportId = event.sportId;
    }

    this.validateField('eventId');
    this.validateField('sportId');
  }

  selectSport(sportId: string): void {
    this.formData.sportId = sportId;

    const matchingEvent = this.filteredEventSuggestions().find(event => event.sportId === sportId);
    if (matchingEvent && !this.formData.eventId) {
      this.formData.eventId = matchingEvent.id;
      this.validateField('eventId');
    }

    this.validateField('sportId');
  }

  syncEventSelection(): void {
    const normalized = this.formData.eventDisplay?.trim();
    const matchingEvent = this.eventSuggestions().find(event =>
      event.nom === normalized || event.id === normalized
    );

    if (matchingEvent) {
      this.formData.eventId = matchingEvent.id;
      this.formData.eventDisplay = matchingEvent.nom;

      if (!this.formData.sportId?.trim()) {
        this.formData.sportId = matchingEvent.sportId;
      }
    } else {
      this.formData.eventId = '';
    }

    this.validateField('eventId');
    this.validateField('sportId');
  }

  selectTeam(slot: 1 | 2, teamId: string): void {
    if (slot === 1) {
      this.formData.team1Id = teamId;
      this.validateField('team1Id');
      return;
    }

    this.formData.team2Id = teamId;
    this.validateField('team2Id');
  }
  resetForm(): void {
    this.formData = {
      eventId: '',
      eventDisplay: '',
      team1Id: '',
      team2Id: '',
      scoreTeam1: 0,
      scoreTeam2: 0,
      terrainId: '',
      date: '',
      heure: '',
      sportId: '',
      status: 'SCHEDULED',
      type: 'LEAGUE'
    };
    this.form.set(this.formData);
    this.error.set(null);
    this.fieldErrors.set({});
  }

  validateField(fieldName: string): void {
    const errors: Record<string, string> = { ...this.fieldErrors() };

    switch (fieldName) {
      case 'team1Id':
        if (!this.formData.team1Id?.trim()) {
          errors['team1Id'] = 'Team 1 ID is required';
        } else if (this.formData.team1Id === this.formData.team2Id) {
          errors['team1Id'] = 'Team 1 ID cannot be the same as Team 2 ID';
        } else {
          delete errors['team1Id'];
        }
        break;

      case 'team2Id':
        if (!this.formData.team2Id?.trim()) {
          errors['team2Id'] = 'Team 2 ID is required';
        } else if (this.formData.team2Id === this.formData.team1Id) {
          errors['team2Id'] = 'Team 2 ID must differ from Team 1 ID';
        } else {
          delete errors['team2Id'];
        }
        break;

      case 'eventId':
        if (!this.formData.eventId?.trim()) {
          errors['eventId'] = 'Event is required';
        } else {
          delete errors['eventId'];
        }
        break;

      case 'scoreTeam1':
        if (this.formData.scoreTeam1 < 0) {
          errors['scoreTeam1'] = 'Score cannot be negative';
        } else if (!Number.isInteger(this.formData.scoreTeam1)) {
          errors['scoreTeam1'] = 'Score must be an integer';
        } else {
          delete errors['scoreTeam1'];
        }
        break;

      case 'scoreTeam2':
        if (this.formData.scoreTeam2 < 0) {
          errors['scoreTeam2'] = 'Score cannot be negative';
        } else if (!Number.isInteger(this.formData.scoreTeam2)) {
          errors['scoreTeam2'] = 'Score must be an integer';
        } else {
          delete errors['scoreTeam2'];
        }
        break;

      case 'terrainId':
        if (!this.formData.terrainId?.trim()) {
          errors['terrainId'] = 'Terrain ID is required';
        } else {
          delete errors['terrainId'];
        }
        break;

      case 'date':
        if (!this.formData.date) {
          errors['date'] = 'Date is required';
        } else {
          const today = new Date(this.getTodayDate());
          const selectedDate = new Date(this.formData.date);
          today.setHours(0, 0, 0, 0);
          selectedDate.setHours(0, 0, 0, 0);

          if (selectedDate < today) {
            errors['date'] = 'Date cannot be in the past';
          } else {
            delete errors['date'];
          }
        }
        break;

      case 'heure':
        if (!this.formData.heure) {
          errors['heure'] = 'Time is required';
        } else if (!/^\d{2}:\d{2}$/.test(this.formData.heure)) {
          errors['heure'] = 'Time format must be HH:mm';
        } else {
          delete errors['heure'];
        }
        break;

      case 'sportId':
        if (!this.formData.sportId?.trim()) {
          errors['sportId'] = 'Sport ID is required';
        } else {
          delete errors['sportId'];
        }
        break;
    }

    this.fieldErrors.set(errors);
  }

  isFormValid(): boolean {
    // Check required fields
    if (!this.formData.team1Id?.trim() || 
        !this.formData.team2Id?.trim() || 
        !this.formData.eventId?.trim() ||
        !this.formData.terrainId?.trim() || 
        !this.formData.date || 
        !this.formData.heure || 
        !this.formData.sportId?.trim() ||
        !this.formData.status ||
        !this.formData.type) {
      return false;
    }

    // Check team IDs are different
    if (this.formData.team1Id === this.formData.team2Id) {
      return false;
    }

    // Check scores
    if (this.formData.scoreTeam1 < 0 || this.formData.scoreTeam2 < 0) {
      return false;
    }

    if (!Number.isInteger(this.formData.scoreTeam1) || !Number.isInteger(this.formData.scoreTeam2)) {
      return false;
    }

    // Check date
    const today = new Date(this.getTodayDate());
    const selectedDate = new Date(this.formData.date);
    today.setHours(0, 0, 0, 0);
    selectedDate.setHours(0, 0, 0, 0);

    if (selectedDate < today) {
      return false;
    }

    // Check time format
    if (!/^\d{2}:\d{2}$/.test(this.formData.heure)) {
      return false;
    }

    return true;
  }

  private getTodayDate(): string {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onSubmit(): void {
    this.error.set(null);
    this.fieldErrors.set({});

    this.syncEventSelection();

    // Validate all fields
    this.validateField('eventId');
    this.validateField('team1Id');
    this.validateField('team2Id');
    this.validateField('scoreTeam1');
    this.validateField('scoreTeam2');
    this.validateField('terrainId');
    this.validateField('date');
    this.validateField('heure');
    this.validateField('sportId');
    this.validateField('status');
    this.validateField('type');

    if (!this.isFormValid()) {
      this.error.set('Please fix all errors before submitting');
      return;
    }

    this.loading.set(true);

    const request = this.isEditMode()
      ? this.matchService.updateMatch(this.match()!.id, this.formData)
      : this.matchService.createMatch(this.formData);

    request.subscribe({
      next: () => {
        this.loading.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.loading.set(false);
        if (err.status === 400 && err.error?.errors) {
          this.fieldErrors.set(err.error.errors);
          this.error.set('Please fix the errors above');
        } else {
          this.error.set(err.error?.message || 'Failed to save match');
        }
      }
    });
  }

  onCancel(): void {
    this.resetForm();
    this.close.emit();
  }
}
