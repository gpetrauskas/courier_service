import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { Observable, BehaviorSubject, EMPTY } from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private userRoleSubject = new BehaviorSubject<string | null>(null);
  private userNameSubject = new BehaviorSubject<string | null>(null);

  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  userRole$ = this.userRoleSubject.asObservable();
  userName$ = this.userNameSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
  }

  checkAuthToken(): void {
    this.getMe().subscribe({
      next: (user) => {
        this.isAuthenticatedSubject.next(true);
        this.userRoleSubject.next(user.role);
        this.userNameSubject.next(user.name);
      },
      error: () => this.clearAuthState()
    });
  }

  private getMe(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/api/auth`);
  }

  refresh() {
    console.log("called");
    return this.http.post<any>(`${environment.apiUrl}/api/auth/refresh`, {});
  }

  private isJwtExpired(expirationDate: number): boolean {
    return expirationDate * 1000 < Date.now();
  }

  private clearAuthState(): void {
    this.isAuthenticatedSubject.next(false);
    this.userRoleSubject.next(null);
    this.userNameSubject.next(null);
  }

  getRole(): Observable<string | null> {
    return this.userRole$;
  }

  getRoleValue() {
    return this.userRoleSubject.value;
  }

  getUserName(): string | null {
    return this.userNameSubject.value;
  }

  isAdmin(): Observable<boolean> {
    return this.userRole$.pipe(map(role => role === 'ADMIN'));
  }

  isAdminSync(): boolean {
    return this.userRoleSubject.value === 'ADMIN';
  }

  logout(): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/api/auth/logout`, {})
    .pipe(
      tap(() => {
        this.clearAuthState();
        this.router.navigate(['/']);
      }),
      catchError(() => {
        this.clearAuthState();
        this.router.navigate(['/']);
        return EMPTY;
      })
    );
  }
}
