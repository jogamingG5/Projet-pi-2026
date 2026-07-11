import { Component, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExportDataset, ExportService } from '../services/export.service';

/**
 * Petit menu "Export" (CSV / PDF) réutilisable dans les entêtes de page.
 * Déclenche un téléchargement réel via ExportService (backend).
 */
@Component({
  selector: 'app-export-menu',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="export-menu">
      <button type="button" class="btn btn-secondary" (click)="toggle()">
        ⬇ Export
        <span class="caret">▾</span>
      </button>
      @if (open()) {
        <div class="export-dropdown">
          <button type="button" (click)="pick('csv')">📄 CSV</button>
          <button type="button" (click)="pick('pdf')">📕 PDF</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .export-menu { position: relative; display: inline-block; }
    .caret { margin-left: 0.35rem; opacity: 0.8; }
    .export-dropdown {
      position: absolute; right: 0; top: calc(100% + 6px); z-index: 30;
      min-width: 150px; padding: 0.35rem;
      background: var(--surface-2); border: 1px solid var(--line-strong);
      border-radius: var(--radius-lg); box-shadow: var(--shadow-md);
      display: flex; flex-direction: column; gap: 0.2rem;
    }
    .export-dropdown button {
      width: 100%; text-align: left; background: transparent; color: var(--text-dim);
      padding: 0.55rem 0.7rem; border-radius: 8px; font-weight: 600;
    }
    .export-dropdown button:hover { background: rgba(255,255,255,0.06); color: var(--text); }
  `]
})
export class ExportMenuComponent {
  private exportService = inject(ExportService);

  dataset = input.required<ExportDataset>();
  filters = input<Record<string, string | undefined>>({});

  open = signal(false);

  toggle(): void {
    this.open.update(v => !v);
  }

  pick(format: 'csv' | 'pdf'): void {
    this.exportService.download(this.dataset(), format, this.filters());
    this.open.set(false);
  }
}
