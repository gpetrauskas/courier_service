import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { ChangePasswordService } from "../../../service/change-password/change-password.service";
import { PasswordChange } from '../../../models/person/password-change.dto';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [MatFormFieldModule, MatIconModule, MatInputModule, MatButtonModule, ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent {
  hide = signal(true);

  newPassword = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    Validators.maxLength(20),
    Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/)
  ]);
  repeatNewPassword = new FormControl('', Validators.required);
  currentPassword = new FormControl('', Validators.required);

  constructor(private passwordService: ChangePasswordService, private errorHandler: ErrorHandlerService) {}

  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  save() {
    if (!this.newPassword.valid || !this.repeatNewPassword.valid || !this.currentPassword.valid) {
      this.errorHandler.showError("Wrong new pass.")
      return;
    }

    if (this.newPassword.value !== this.repeatNewPassword.value) {
      this.errorHandler.showError("Passwords do not match")
      return
    }

    const dto: PasswordChange = {
      currentPassword: this.currentPassword.value!,
      newPassword: this.newPassword.value!
    };

    this.errorHandler.handleRequest(this.passwordService.changePassword(dto), "Password changed successfully");
  }
}
