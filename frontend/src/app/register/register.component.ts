import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { LoginService } from '../auth/login.service';
import { ErrorHandlerService } from "../service/error-handler.service";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  errorMessage: string | null = null;

  constructor(
    private router: Router,
    private loginService: LoginService,
    private errorHandler: ErrorHandlerService
    ) {}

  onSubmit(registerForm: NgForm) {
    if (registerForm.valid) {
      this.errorHandler.handleRequest(this.loginService.register(
        registerForm.value.fullName,
        registerForm.value.userEmail,
        registerForm.value.password),
        "",
        () => {
        this.router.navigate(['/login']);
        this.errorMessage = null;
      });
    } else {
      this.errorMessage = "Please fix errrors in the form.";
    }
  }
}
