import { Component, OnInit, signal, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.model';
import { BadgeComponent } from '../../components/badge.component';
import { LoadingSpinnerComponent } from '../../components/loading-spinner.component';
import { ConfirmDialogComponent } from '../../components/confirm-dialog.component';
import { EventModalComponent } from './event-modal.component';
import { PaginatorComponent } from '../../components/paginator.component';
import { ExportMenuComponent } from '../../components/export-menu.component';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    BadgeComponent,
    LoadingSpinnerComponent,
    ConfirmDialogComponent,
    EventModalComponent,
    PaginatorComponent,
    ExportMenuComponent
  ],
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.css']
})
export class EventListComponent implements OnInit {
  private eventService = inject(EventService);
  @ViewChild(EventModalComponent) eventModal!: EventModalComponent;

  events = signal<Event[]>([]);
  allEvents = signal<Event[]>([]);      // full set (team/sport suggestions)
  pagedEventsList = signal<Event[]>([]); // current server page (grid)
  totalEvents = signal(0);
  loading = signal(false);
  error = signal<string | null>(null);
  showConfirmDelete = signal(false);
  eventToDelete = signal<string | null>(null);
  showModal = signal(false);
  selectedEvent = signal<Event | null>(null);
  searchQuery = signal('');
  selectedFilter = signal<'ALL' | 'LEAGUE' | 'FRIENDLY'>('ALL');
  selectedSport = signal('ALL');

  // Pagination (server-side)
  page = signal(1);
  readonly pageSize = 9;

  ngOnInit(): void {
    this.loadPage();
    this.loadAllEvents();
  }

  /** Fetch the current page from the server, honoring active filters. */
  loadPage(): void {
    this.loading.set(true);
    this.error.set(null);

    const filters: { sportId?: string; type?: string; search?: string } = {};
    if (this.selectedSport() !== 'ALL') filters.sportId = this.selectedSport();
    if (this.selectedFilter() !== 'ALL') filters.type = this.selectedFilter();
    if (this.searchQuery().trim()) filters.search = this.searchQuery().trim();

    this.eventService.getEventsPaged(this.page(), this.pageSize, filters).subscribe({
      next: (res) => {
        this.pagedEventsList.set(res.content);
        this.totalEvents.set(res.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load events');
        this.loading.set(false);
      }
    });
  }

  /** Full set for team/sport suggestions in the modal. */
  loadAllEvents(): void {
    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.allEvents.set(data);
        this.events.set(data);
      },
      error: () => this.allEvents.set([])
    });
  }

  /** Active filters passed to the export endpoint. */
  exportFilters(): Record<string, string | undefined> {
    return {
      sportId: this.selectedSport() !== 'ALL' ? this.selectedSport() : undefined,
      type: this.selectedFilter() !== 'ALL' ? this.selectedFilter() : undefined
    };
  }

  pagedEvents() {
    return this.pagedEventsList();
  }

  totalFilteredEvents(): number {
    return this.totalEvents();
  }

  onPageChange(page: number): void {
    this.page.set(page);
    this.loadPage();
  }

  onSearchInputChange(event: any): void {
    const value = (event.target as HTMLInputElement).value;
    this.page.set(1);
    this.searchQuery.set(value);
    this.loadPage();
  }

  onSportChange(event: globalThis.Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.page.set(1);
    this.selectedSport.set(value);
    this.loadPage();
  }

  setTypeFilter(type: 'ALL' | 'LEAGUE' | 'FRIENDLY'): void {
    this.page.set(1);
    this.selectedFilter.set(type);
    this.loadPage();
  }

  clearFilters(): void {
    this.page.set(1);
    this.searchQuery.set('');
    this.selectedFilter.set('ALL');
    this.selectedSport.set('ALL');
    this.loadPage();
  }

  availableTeamSuggestions(): string[] {
    return [...new Set(this.allEvents().flatMap(event => event.teamsIds || []).filter(Boolean))].sort();
  }

  availableSports(): string[] {
    return [...new Set(this.allEvents().map(event => event.sportId).filter(Boolean))].sort();
  }

  /** Refresh both the current page and the full suggestion set (after save/delete). */
  loadEvents(): void {
    this.loadPage();
    this.loadAllEvents();
  }

  openModal(event?: Event): void {
    if (event) {
      this.selectedEvent.set(event);
    } else {
      this.selectedEvent.set(null);
    }
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.selectedEvent.set(null);
  }

  onEventSaved(): void {
    this.closeModal();
    this.loadEvents();
  }

  onEditEvent(event: Event): void {
    this.openModal(event);
  }

  onDeleteEvent(id: string): void {
    this.eventToDelete.set(id);
    this.showConfirmDelete.set(true);
  }

  confirmDelete(): void {
    const id = this.eventToDelete();
    if (!id) return;

    this.eventService.deleteEvent(id).subscribe({
      next: () => {
        this.loadEvents();
        this.showConfirmDelete.set(false);
        this.eventToDelete.set(null);
      },
      error: () => {
        this.showConfirmDelete.set(false);
      }
    });
  }

  cancelDelete(): void {
    this.showConfirmDelete.set(false);
    this.eventToDelete.set(null);
  }

  formatDate(dateValue: any): string {
    try {
      // Handle null and undefined
      if (dateValue === null || dateValue === undefined || dateValue === '') {
        return '--';
      }
      
      let dateObj: Date;
      
      // Handle array format [year, month, day]
      if (Array.isArray(dateValue)) {
        const [year, month, day] = dateValue;
        dateObj = new Date(year, month - 1, day);
      } 
      // Handle string format
      else if (typeof dateValue === 'string') {
        dateObj = new Date(dateValue + 'T00:00:00Z');
      } 
      // Handle Date object
      else if (dateValue instanceof Date) {
        dateObj = dateValue;
      } 
      else {
        return '--';
      }
      
      return dateObj.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch (error) {
      return '--';
    }
  }
}
