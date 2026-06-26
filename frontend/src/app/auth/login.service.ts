import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, throwError, catchError, tap } from 'rxjs';
import { PersonRegistration } from '../models/person/person-registration.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private baseUrlRegister = `${environment.apiUrl}/api/register`;
  private baseUrlLogin = `${environment.apiUrl}/api/auth`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
    ) { }

  register(name: string, email: string, password: string): Observable<any> {
    const payLoad = { name, email, password };
    return this.http.post<any>(`${this.baseUrlRegister}`, payLoad).pipe(
      catchError(error => {
        const errorMessage = error.error ? Object.values(error.error).join('\n') : 'Registration failed.';
        return throwError(() => new Error(errorMessage));
      })
    );
  }

  login(email: string, password: string): Observable<any> {
    const payLoad = { email, password };
    return this.http.post<any>(`${this.baseUrlLogin}`, payLoad, { withCredentials: true }).pipe(
      tap(() => this.authService.checkAuthToken())
    );
  }

  registerCourier(courierForm: PersonRegistration): Observable<void> {
    return this.http.post<void>(`${this.baseUrlRegister}/registerCourier`, courierForm, { withCredentials: true });
  }
}
