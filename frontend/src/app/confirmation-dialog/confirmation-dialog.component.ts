import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './confirmation-dialog.component.html',
  styleUrl: './confirmation-dialog.component.css'
})
export class ConfirmationDialogComponent {
  @Input() isVisible: boolean = false;
  @Input() showReasonInput: boolean = false;
  @Input() itemType: string = '';
  @Input() itemName: string = '';
  @Input() actionType: string = 'DELETE';
  reason: string = "";

  @Output() confirmed = new EventEmitter<void>();
  @Output() canceled = new EventEmitter<void>();
  @Output() confirmWithReason = new EventEmitter<string>();

  confirm(): void {
    if (!this.showReasonInput) {
      this.confirmed.emit();
    } else {
      this.confirmWithReason.emit(this.reason);
    }

    this.reason = '';
  }

  cancel(): void {
    this.canceled.emit();
    this.reason = '';
  }

  getTitle(): string {
    return `Confirm ${this.actionType}`;
  }

  getVerb(): string {
    return this.actionType.toLowerCase();
  }

  getButtonLabel(): string {
    return this.actionType;
  }

  private capitalizeFirstLetter(text: string): string {
    if (!text) return '';
    return text.charAt(0).toUpperCase() + text.slice(1).toLowerCase();
  }
}
