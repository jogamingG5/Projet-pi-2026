import { Component, input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Status / type pill mapped to the shared design-system badge classes.
 */
@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge" [ngClass]="variantClass()">
      <span class="dot" [style.background]="dotColor()"></span>
      {{ label() }}
    </span>
  `
})
export class BadgeComponent {
  label = input.required<string>();
  type = input<'status' | 'type'>('status');

  private static readonly STATUS_VARIANT: Record<string, string> = {
    SCHEDULED: 'badge-info',
    ONGOING: 'badge-success',
    COMPLETED: 'badge-neutral',
    CANCELLED: 'badge-danger'
  };

  private static readonly TYPE_VARIANT: Record<string, string> = {
    LEAGUE: 'badge-primary',
    FRIENDLY: 'badge-warning'
  };

  private static readonly DOT: Record<string, string> = {
    SCHEDULED: '#60a5fa',
    ONGOING: '#34d399',
    COMPLETED: '#94a3b8',
    CANCELLED: '#f87171',
    LEAGUE: '#2f7dff',
    FRIENDLY: '#fbbf24'
  };

  variantClass = computed<string>(() => {
    const map = this.type() === 'status' ? BadgeComponent.STATUS_VARIANT : BadgeComponent.TYPE_VARIANT;
    return map[this.label()] || 'badge-neutral';
  });

  dotColor = computed<string>(() => BadgeComponent.DOT[this.label()] || '#94a3b8');
}
