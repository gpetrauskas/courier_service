import { Component, Input } from '@angular/core';
import { AbstractControl } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component({
  selector: 'app-app-field-error',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app-field-error.component.html',
  styleUrl: './app-field-error.component.css'
})
export class AppFieldErrorComponent {
  @Input() control: AbstractControl | null = null;
}
