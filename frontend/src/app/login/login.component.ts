import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoginService } from '../auth/login.service';
import { ErrorHandlerService } from "../service/error-handler.service";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  constructor(
    private router: Router,
    private loginService: LoginService,
    private errorHandler: ErrorHandlerService
  ) {}

  onSubmit(loginForm: NgForm) {
    if (loginForm.valid) {
      const {username, password} = loginForm.value;

      this.errorHandler.handleRequest(this.loginService.login(username, password), '',
        () => this.router.navigate([""])
      );
    }
  }
}
