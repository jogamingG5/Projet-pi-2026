import { Component, input, output, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { Event, EventRequest, EventType } from '../../models/event.model';
import { extractErrorMessage } from '../../utils/http-error';

@Component({
  selector: 'app-event-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    @if (isOpen()) {
      <div class="modal-overlay">
        <div class="modal-shell" style="max-width: 64rem;">
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
        this.error.set(extractErrorMessage(err, 'Échec de l\'enregistrement de l\'événement'));
      }
    });
  }

  onCancel(): void {
    this.resetForm();
    this.close.emit();
  }
}
