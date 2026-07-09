import { Component, input, output, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { Event, EventRequest, EventType } from '../../models/event.model';

@Component({
  selector: 'app-event-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (isOpen()) {
      <div class="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-slate-950/70 backdrop-blur-md p-4 sm:items-center">
        <div class="modal-shell w-full max-w-5xl overflow-hidden max-h-[92vh] flex flex-col">
          <div class="modal-header">
            <div>
              <div class="modal-eyebrow">Event editor</div>
              <h2 class="modal-title">
                {{ isEditMode() ? 'Modify event' : 'Create event' }}
              </h2>
              <p class="modal-subtitle">
                One clean form, internal scroll, and team chips with suggestions.
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
                  <h3>Basics</h3>
                  <p>Main event identity</p>
                </div>

                <div class="modal-grid modal-grid-2">
                  <div class="form-group required">
                    <label class="label">Event name</label>
                    <input
                      class="input-field"
                      type="text"
                      [(ngModel)]="formData.nom"
                      (blur)="validateField('nom')"
                      name="nom"
                      required
                      minlength="3"
                      maxlength="100"
                      placeholder="Example: Street League Regional Tournament"
                    />
                    <div class="char-count">{{ (formData.nom || '').length }}/100</div>
                    @if (fieldErrors()['nom']) {
                      <p class="form-error">{{ fieldErrors()['nom'] }}</p>
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
                      placeholder="football, basketball, volleyball"
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
                          <button type="button" class="suggestion-chip" (click)="formData.sportId = sport">
                            {{ sport }}
                          </button>
                        }
                      </div>
                    </div>

                    @if (fieldErrors()['sportId']) {
                      <p class="form-error">{{ fieldErrors()['sportId'] }}</p>
                    }
                  </div>
                </div>

                <div class="form-group">
                  <label class="label">Description</label>
                  <textarea
                    class="input-field"
                    [(ngModel)]="formData.description"
                    (blur)="validateField('description')"
                    name="description"
                    rows="4"
                    maxlength="500"
                    placeholder="Describe the event, format, and any useful context"
                  ></textarea>
                  <div class="char-count">{{ (formData.description || '').length }}/500</div>
                  @if (fieldErrors()['description']) {
                    <p class="form-error">{{ fieldErrors()['description'] }}</p>
                  }
                </div>
              </section>

              <section class="modal-section">
                <div class="section-head">
                  <h3>Planning</h3>
                  <p>Dates and event type</p>
                </div>

                <div class="modal-grid modal-grid-2">
                  <div class="form-group required">
                    <label class="label">Start date</label>
                    <input
                      class="input-field"
                      type="date"
                      [(ngModel)]="formData.dateDebut"
                      (blur)="validateField('dateDebut')"
                      name="dateDebut"
                      required
                      [min]="minDate"
                      (change)="validateDates()"
                    />
                    @if (fieldErrors()['dateDebut']) {
                      <p class="form-error">{{ fieldErrors()['dateDebut'] }}</p>
                    }
                  </div>

                  <div class="form-group required">
                    <label class="label">End date</label>
                    <input
                      class="input-field"
                      type="date"
                      [(ngModel)]="formData.dateFin"
                      (blur)="validateField('dateFin')"
                      name="dateFin"
                      required
                      [min]="formData.dateDebut"
                      (change)="validateDates()"
                    />
                    @if (fieldErrors()['dateFin']) {
                      <p class="form-error">{{ fieldErrors()['dateFin'] }}</p>
                    }
                  </div>
                </div>

                @if (dateError()) {
                  <div class="modal-alert modal-alert-warn">
                    {{ dateError() }}
                  </div>
                }

                <div class="modal-grid modal-grid-2">
                  <div class="form-group required">
                    <label class="label">Event type</label>
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
                    <label class="label">Teams</label>
                    <div class="chip-field" [class.chip-field-error]="formData.teamsIds.length < 2 && fieldErrors()['teamsIds']">
                      <div class="chip-list">
                        @for (team of formData.teamsIds; track $index) {
                          <span class="team-chip">
                            {{ team }}
                            <button type="button" (click)="removeTeam($index)" aria-label="Remove team">×</button>
                          </span>
                        }

                        <input
                          type="text"
                          [value]="teamQuery()"
                          (input)="onTeamQueryChange($event)"
                          (keydown.enter)="addTeamFromQuery($event)"
                          (keydown.backspace)="removeLastTeamOnEmpty($event)"
                          placeholder="Type a team ID and press Enter"
                          class="chip-input"
                          list="team-suggestions"
                        />
                      </div>
                    </div>

                    <datalist id="team-suggestions">
                      @for (team of filteredTeamSuggestions(); track team) {
                        <option [value]="team"></option>
                      }
                    </datalist>

                    <div class="suggestions-block">
                      <span class="suggestions-label">Suggestions</span>
                      <div class="suggestion-list">
                        @for (team of filteredTeamSuggestions(); track team) {
                          <button type="button" class="suggestion-chip" (click)="addTeam(team)">
                            {{ team }}
                          </button>
                        }
                      </div>
                    </div>

                    <div class="form-help">
                      {{ formData.teamsIds.length }} team(s) added
                      @if (formData.teamsIds.length < 2) {
                        <span class="text-red-600 font-semibold">(minimum 2)</span>
                      }
                    </div>
                    @if (fieldErrors()['teamsIds']) {
                      <p class="form-error">{{ fieldErrors()['teamsIds'] }}</p>
                    }
                  </div>
                </div>
              </section>

              <div class="modal-actions">
                <button type="button" (click)="onCancel()" class="btn-secondary">
                  Cancel
                </button>
                <button type="submit" [disabled]="loading() || !isFormValid()" class="btn-primary">
                  {{ loading() ? 'Saving...' : 'Save event' }}
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

    .modal-alert-warn {
      border-color: rgba(224, 139, 18, 0.25);
      background: rgba(255, 247, 231, 0.95);
      color: #8d5a0d;
      margin-top: 0.75rem;
    }

    .chip-field {
      border: 1.5px solid var(--line);
      border-radius: 16px;
      background: #ffffff;
      padding: 0.75rem;
      min-height: 4.4rem;
      transition: border-color 160ms ease, box-shadow 160ms ease;
    }

    .chip-field:focus-within {
      border-color: var(--primary);
      box-shadow: 0 0 0 4px rgba(15, 122, 229, 0.12);
    }

    .chip-field-error {
      border-color: rgba(217, 72, 72, 0.75);
    }

    .chip-list {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
      align-items: center;
    }

    .team-chip {
      display: inline-flex;
      align-items: center;
      gap: 0.45rem;
      padding: 0.55rem 0.8rem;
      border-radius: 999px;
      background: linear-gradient(135deg, #0f7ae5 0%, #00b7d8 100%);
      color: #ffffff;
      font-size: 0.9rem;
      font-weight: 800;
      box-shadow: 0 10px 18px rgba(15, 122, 229, 0.2);
    }

    .team-chip button {
      padding: 0;
      background: transparent;
      color: inherit;
      font-size: 1.1rem;
      line-height: 1;
      box-shadow: none;
      transform: none;
    }

    .chip-input {
      flex: 1 1 14rem;
      min-width: 14rem;
      border: none;
      background: transparent;
      padding: 0.45rem 0.2rem;
      box-shadow: none;
    }

    .chip-input:focus {
      box-shadow: none;
      background: transparent;
    }

    .suggestions-block {
      margin-top: 0.8rem;
    }

    .suggestions-label {
      display: block;
      margin-bottom: 0.45rem;
      font-size: 0.82rem;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: #5b6b7e;
    }

    .suggestion-list {
      display: flex;
      flex-wrap: wrap;
      gap: 0.5rem;
    }

    .suggestion-chip {
      padding: 0.5rem 0.8rem;
      border-radius: 999px;
      border: 1px solid rgba(15, 122, 229, 0.18);
      background: #f4f8fc;
      color: #20405f;
      font-weight: 700;
      box-shadow: none;
    }

    .suggestion-chip:hover {
      background: #eaf4ff;
      border-color: rgba(15, 122, 229, 0.3);
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
export class EventModalComponent {
  private eventService = inject(EventService);

  isOpen = input(false);
  event = input<Event | null>(null);
  teamSuggestions = input<string[]>([]);
  sportSuggestions = input<string[]>([]);
  close = output<void>();
  saved = output<void>();

  // Date validation
  minDate = this.getTodayDate();

  formData: EventRequest = {
    nom: '',
    description: '',
    dateDebut: '',
    dateFin: '',
    type: 'LEAGUE' as EventType,
    sportId: '',
    teamsIds: []
  };

  loading = signal(false);
  error = signal<string | null>(null);
  fieldErrors = signal<Record<string, string>>({});
  dateError = signal<string | null>(null);
  isEditMode = signal(false);
  teamQuery = signal('');
  private readonly fallbackTeamSuggestions = ['team_1', 'team_2', 'team_3', 'team_4', 'team_5', 'team_6'];

  constructor() {
    effect(() => {
      const eventData = this.event();
      if (eventData) {
        this.formData = { ...eventData };
        this.isEditMode.set(true);
      } else {
        this.resetForm();
        this.isEditMode.set(false);
      }
    });
  }

  resetForm(): void {
    this.formData = {
      nom: '',
      description: '',
      dateDebut: '',
      dateFin: '',
      type: 'LEAGUE',
      sportId: '',
      teamsIds: []
    };
    this.error.set(null);
    this.fieldErrors.set({});
    this.dateError.set(null);
    this.teamQuery.set('');
  }

  validateDates(): void {
    const errors: Record<string, string> = { ...this.fieldErrors() };

    if (this.formData.dateDebut && this.formData.dateFin) {
      const startDate = new Date(this.formData.dateDebut);
      const endDate = new Date(this.formData.dateFin);

      if (endDate < startDate) {
        this.dateError.set('La date de fin doit être après ou égale à la date de début');
        errors['dateFin'] = 'La date de fin doit être après ou égale à la date de début';
      } else {
        this.dateError.set(null);
        delete errors['dateFin'];
      }

      // Check if start date is today or later
      const today = new Date(this.getTodayDate());
      today.setHours(0, 0, 0, 0);
      startDate.setHours(0, 0, 0, 0);

      if (startDate < today) {
        errors['dateDebut'] = 'La date de début ne peut pas être dans le passé';
      } else {
        delete errors['dateDebut'];
      }

      this.fieldErrors.set(errors);
    }
  }

  validateField(fieldName: string): void {
    const errors: Record<string, string> = { ...this.fieldErrors() };

    switch (fieldName) {
      case 'nom':
        if (!this.formData.nom || this.formData.nom.trim().length === 0) {
          errors['nom'] = 'Event name is required';
        } else if (this.formData.nom.length < 3) {
          errors['nom'] = 'Event name must be at least 3 characters';
        } else if (this.formData.nom.length > 100) {
          errors['nom'] = 'Event name cannot exceed 100 characters';
        } else {
          delete errors['nom'];
        }
        break;

      case 'description':
        if (this.formData.description && this.formData.description.length > 500) {
          errors['description'] = 'Description cannot exceed 500 characters';
        } else {
          delete errors['description'];
        }
        break;

      case 'sportId':
        if (!this.formData.sportId || this.formData.sportId.trim().length === 0) {
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
    if (!this.formData.nom?.trim() || 
        !this.formData.dateDebut || 
        !this.formData.dateFin || 
        !this.formData.type || 
        !this.formData.sportId?.trim()) {
      return false;
    }

    // Check field lengths
    if (this.formData.nom.length < 3 || this.formData.nom.length > 100) {
      return false;
    }

    if (this.formData.description && this.formData.description.length > 500) {
      return false;
    }

    // Check teams minimum
    if (this.formData.teamsIds.length < 2) {
      return false;
    }

    // Check date validation
    const startDate = new Date(this.formData.dateDebut);
    const endDate = new Date(this.formData.dateFin);

    if (endDate < startDate) {
      return false;
    }

    const today = new Date(this.getTodayDate());
    today.setHours(0, 0, 0, 0);
    startDate.setHours(0, 0, 0, 0);

    if (startDate < today) {
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

  addTeam(teamId: string): void {
    const cleanId = teamId.trim();
    if (cleanId && !this.formData.teamsIds.includes(cleanId)) {
      this.formData.teamsIds.push(cleanId);
      this.teamQuery.set('');
      // Clear team ID error if we now have minimum teams
      if (this.formData.teamsIds.length >= 2) {
        const errors = { ...this.fieldErrors() };
        delete errors['teamsIds'];
        this.fieldErrors.set(errors);
      }
    }
  }

  addTeamFromQuery(event: any): void {
    event.preventDefault();
    this.addTeam(this.teamQuery());
  }

  onTeamQueryChange(event: any): void {
    const value = (event.target as HTMLInputElement).value;
    this.teamQuery.set(value);
  }

  removeLastTeamOnEmpty(event: any): void {
    if (this.teamQuery() === '' && this.formData.teamsIds.length > 0) {
      event.preventDefault();
      this.formData.teamsIds.pop();
    }
  }

  filteredTeamSuggestions(): string[] {
    const source = [...new Set([...this.fallbackTeamSuggestions, ...(this.teamSuggestions() || [])])];
    const query = this.teamQuery().trim().toLowerCase();
    return source
      .filter(team => !this.formData.teamsIds.includes(team))
      .filter(team => !query || team.toLowerCase().includes(query))
      .slice(0, 8);
  }

  filteredSportSuggestions(): string[] {
    const source = [...new Set([
      'football',
      'basketball',
      'volleyball',
      'tennis',
      'handball',
      'rugby',
      ...(this.sportSuggestions() || [])
    ])];

    const query = (this.formData.sportId || '').trim().toLowerCase();
    return source
      .filter(sport => !query || sport.toLowerCase().includes(query))
      .slice(0, 8);
  }

  removeTeam(index: number): void {
    this.formData.teamsIds.splice(index, 1);
  }

  onSubmit(): void {
    this.validateDates();

    // Validate all fields
    this.validateField('nom');
    this.validateField('description');
    this.validateField('sportId');

    // Validate teams
    const errors: Record<string, string> = { ...this.fieldErrors() };
    if (this.formData.teamsIds.length < 2) {
      errors['teamsIds'] = 'At least 2 teams are required';
    } else {
      delete errors['teamsIds'];
    }
    this.fieldErrors.set(errors);

    if (!this.isFormValid()) {
      this.error.set('Please fix all errors before submitting');
      return;
    }

    this.error.set(null);
    this.loading.set(true);

    const request = this.isEditMode()
      ? this.eventService.updateEvent(this.event()!.id, this.formData)
      : this.eventService.createEvent(this.formData);

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
          this.error.set(err.error?.message || 'Failed to save event');
        }
      }
    });
  }

  onCancel(): void {
    this.resetForm();
    this.close.emit();
  }
}
