import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (isOpen()) {
      <div class="modal-overlay">
        <div class="modal-shell" style="max-width: 420px;">
          <div style="padding: 1.5rem;">
            <h2 class="modal-title" style="font-size: 1.25rem; margin-bottom: 0.5rem;">{{ title() }}</h2>
            <p style="margin-bottom: 1.5rem;">{{ message() }}</p>
            <div class="modal-actions" style="margin-top: 0;">
              <button (click)="onCancel()" class="btn btn-ghost">Cancel</button>
              <button (click)="onConfirm()" class="btn btn-danger">{{ confirmText() }}</button>
            </div>
          </div>
        </div>
      </div>
    }
  `,
  styles: []
})
export class ConfirmDialogComponent {
  isOpen = input(false);
  title = input('Confirm Action');
  message = input('Are you sure?');
  confirmText = input('Delete');
  
  confirmed = output<void>();
  cancelled = output<void>();

  onConfirm() {
    this.confirmed.emit();
  }

  onCancel() {
    this.cancelled.emit();
  }
}
