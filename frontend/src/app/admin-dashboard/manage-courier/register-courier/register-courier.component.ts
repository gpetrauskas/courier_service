import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { LoginService } from '../../../auth/login.service';
import { ErrorHandlerService } from "../../../service/error-handler.service";

@Component({
  selector: 'app-register-courier',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './register-courier.component.html',
  styleUrl: './register-courier.component.css'
})
export class RegisterCourierComponent {
  courierForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private loginService: LoginService,
    private errorHandler: ErrorHandlerService
  ) {
    this.courierForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(16)]],
      passwordConfirm: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(f: FormGroup) {
    const password = f.get('password')?.value;
    const passwordConfirm = f.get('passwordConfirm')?.value;
    return password === passwordConfirm ? null : { mismatch: true };
  }

  onSubmit() {
    if (this.courierForm.valid) {
      const { passwordConfirm, ...formValues } = this.courierForm.value;
      this.errorHandler.handleRequest(this.loginService.registerCourier(formValues), "Successfully registered");
      this.courierForm.reset();
    } else {
      this.errorHandler.showError("Check registration form")
    }
  }
}
