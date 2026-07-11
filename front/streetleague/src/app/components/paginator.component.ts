import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Reusable pagination control.
 *
 * Stateless: the parent owns the current page (a signal) and the page size.
 * The paginator renders the range info + page buttons and emits `pageChange`
 * with the new 1-based page number. Use the exported `paginate()` helper (or
 * inline slicing) in the parent to slice the current page's items.
 */
@Component({
  selector: 'app-paginator',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (totalPages() > 1) {
      <div class="paginator">
        <div class="paginator-info">
          {{ rangeStart() }}–{{ rangeEnd() }} of {{ totalItems() }}
        </div>

        <div class="paginator-controls">
          <button class="btn btn-ghost btn-sm" [disabled]="page() === 1" (click)="go(page() - 1)">‹ Prev</button>

          @for (p of pages(); track p) {
            @if (p === -1) {
              <span class="paginator-ellipsis">…</span>
            } @else {
              <button
                class="chip"
                [class.active]="p === page()"
                (click)="go(p)"
              >{{ p }}</button>
            }
          }

          <button class="btn btn-ghost btn-sm" [disabled]="page() === totalPages()" (click)="go(page() + 1)">Next ›</button>
        </div>
      </div>
    }
  `,
  styles: [`
    .paginator {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 1rem;
      flex-wrap: wrap;
      margin-top: 1.25rem;
    }
    .paginator-info { color: var(--muted); font-size: 0.85rem; font-weight: 600; }
    .paginator-controls { display: flex; align-items: center; gap: 0.4rem; flex-wrap: wrap; }
    .paginator-ellipsis { color: var(--muted); padding: 0 0.25rem; }
  `]
})
export class PaginatorComponent {
  totalItems = input.required<number>();
  pageSize = input<number>(10);
  page = input<number>(1);

  pageChange = output<number>();

  totalPages = computed<number>(() => Math.max(1, Math.ceil(this.totalItems() / this.pageSize())));
  rangeStart = computed<number>(() => this.totalItems() === 0 ? 0 : (this.page() - 1) * this.pageSize() + 1);
  rangeEnd = computed<number>(() => Math.min(this.page() * this.pageSize(), this.totalItems()));

  /** Page numbers to show, with -1 marking an ellipsis gap. */
  pages = computed<number[]>(() => {
    const total = this.totalPages();
    const current = this.page();
    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }

    const result: number[] = [1];
    const start = Math.max(2, current - 1);
    const end = Math.min(total - 1, current + 1);

    if (start > 2) result.push(-1);
    for (let p = start; p <= end; p++) result.push(p);
    if (end < total - 1) result.push(-1);
    result.push(total);
    return result;
  });

  go(target: number): void {
    const clamped = Math.min(this.totalPages(), Math.max(1, target));
    if (clamped !== this.page()) {
      this.pageChange.emit(clamped);
    }
  }
}
