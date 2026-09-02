import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import {Observable, BehaviorSubject, EMPTY, of, finalize, share} from 'rxjs';
import { catchError, tap, map } from 'rxjs/operators';
import {AuthStateModel} from "../models/security/auth-state.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<AuthStateModel>({isAuthenticated: false, role: "guest", name: null});
  private authCheckedSubject = new BehaviorSubject<boolean>(false);

  private refreshInProgress$: Observable<void> | null = null;

  isAuthenticated$ = this.isAuthenticatedSubject.asObservable().pipe(map(state => state.isAuthenticated));
  userRole$ = this.isAuthenticatedSubject.asObservable().pipe(map(role => role.role));
  userName$ = this.isAuthenticatedSubject.asObservable().pipe(map(n => n.name));
  authChecked$ = this.authCheckedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    Promise.resolve().then(() => this.initAuth().subscribe());
  }

  initAuth() {
    return this.getMe().pipe(
      tap(user => {
        this.isAuthenticatedSubject.next({isAuthenticated: true, role: user.role, name: user.name});
      }),
      catchError((err) => {
        this.clearAuthState();
        return of(null);
      }),
      finalize(() => this.authCheckedSubject.next(true))
    );
  }

  checkAuthToken(): void {
    this.getMe().subscribe({
      next: (user) => {
        this.isAuthenticatedSubject.next({isAuthenticated: true, role: user.role, name: user.name});
      },
      error: () => this.clearAuthState()
    });
  }

  private getMe(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/api/auth`);
  }

  refresh() {
    if (this.refreshInProgress$ !== null) {
      return this.refreshInProgress$;
    }

    this.refreshInProgress$ = this.http.post<void>(`${environment.apiUrl}/api/auth/refresh`, {})
      .pipe(share(),
        finalize(() => this.refreshInProgress$ = null)
      );
    return this.refreshInProgress$;
  }

  private isJwtExpired(expirationDate: number): boolean {
    return expirationDate * 1000 < Date.now();
  }

  private clearAuthState(): void {
    this.isAuthenticatedSubject.next({isAuthenticated: false, role: "guest", name: null});
  }

  getRole(): Observable<string | null> {
    return this.userRole$;
  }

  getRoleValue() {
    return this.isAuthenticatedSubject.value.role;
  }

  getUserName(): string | null {
    return this.isAuthenticatedSubject.value.name;
  }

  isAdmin(): Observable<boolean> {
    return this.userRole$.pipe(map(role => role === 'ADMIN'));
  }

  isAdminSync(): boolean {
    return this.isAuthenticatedSubject.value.role === 'ADMIN';
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
