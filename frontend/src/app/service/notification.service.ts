import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../models/api-response.dto';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Notification as AppNotification } from '../models/person-notification/notification.model';
import { NotificationRequest } from '../models/notification/notification-request.model';
import { BehaviorSubject, mergeMap, Observable, of, throwError } from 'rxjs';
import { PersonNotificationPageResponse } from '../models/person-notification/person-notification-page-response.model';
import { catchError, map, tap } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<PersonNotificationPageResponse>({
    page: {
      data: [],
      currentPage: 0,
      totalItems: 0,
      totalPages: 0
    },
    unreadCount: 0
  });

  private headerNotificationsSubject = new BehaviorSubject<AppNotification[]>([]);

  notifications$ = this.notificationsSubject.asObservable();
  lastNotifications$ = this.headerNotificationsSubject.asObservable();
  unreadCount$ = this.headerNotificationsSubject.pipe(map(n => n.filter(n => !n.isRead).length));

  private adminUrl = `${environment.apiUrl}/api/notifications`;
  private baseURL = `${environment.apiUrl}/api/me/notifications`;

  constructor(private http: HttpClient) { }

  initializeNotifications(): void {
    this.myNotifications(5, 0).subscribe({
      next: (response) => {
        this.headerNotificationsSubject.next(response.page.data);
      },
      error: (err) => {
        console.error('Error fetching notifications', err);
      }
    });
  }

  clearNotifications() {
    this.notificationsSubject.next({
      page: {
        data: [],
        currentPage: 0,
        totalItems: 0,
        totalPages: 0
      },
      unreadCount: 0
    });

    this.headerNotificationsSubject.next([]);
  }

  adminDelete(id: number) {
    return this.http.delete<ApiResponse>(`${this.adminUrl}/${id}`, { withCredentials: true });
  }

  delete(id?: number) {
    const response = id
      ? this.http.delete<ApiResponse>(`${this.baseURL}/${id}`, { withCredentials: true })
      : this.http.delete<ApiResponse>(`${this.baseURL}`, { withCredentials: true });

    return response.pipe(mergeMap(() => this.myNotifications(10, 0)),
      tap(resp => {
        this.notificationsSubject.next(resp);
        this.headerNotificationsSubject.next(resp.page.data);
      }));
  }

  updateLocallyAsRead(id?: number): void {
    const matchAll = id === undefined;

    this.headerNotificationsSubject.next(
      this.headerNotificationsSubject.value.map(n => matchAll || n.id === id ? { ...n, isRead: true } : n )
    );

    const current = this.notificationsSubject.value;
    this.notificationsSubject.next({
      ...current,
      page: {
        ...current.page,
        data: current.page.data.map(n =>
          matchAll || n.id === id ? {  ...n, isRead: true } : n
        )
      },
      unreadCount: matchAll ? 0 : current.unreadCount - 1
    });
  }

  markAsRead(id: number): Observable<void> {
    if (this.headerNotificationsSubject.value.find(n => n.id === id)?.isRead) return of(undefined);
    if (this.notificationsSubject.value.page.data.find(n => n.id === id)?.isRead) return of(undefined);

    const backup = JSON.parse(JSON.stringify(this.notificationsSubject.value));
    const backupHeader = JSON.parse(JSON.stringify(this.headerNotificationsSubject.value));

    Promise.resolve().then(() => this.updateLocallyAsRead(id));

    return this.http.put<void>(`${this.baseURL}/${id}/read`, null, { withCredentials: true }).pipe(
      catchError(err => {
        this.notificationsSubject.next(backup);
        this.headerNotificationsSubject.next(backupHeader);
        return throwError(() => err);
      })
    );
  }

  markAllAsRead(): Observable<void> {
    const backup = JSON.parse(JSON.stringify(this.notificationsSubject.value));
    const backupHeader = JSON.parse(JSON.stringify(this.headerNotificationsSubject.value));

    Promise.resolve().then(() => this.updateLocallyAsRead());
    return this.http.put<void>(`${this.baseURL}/read-all`, null, { withCredentials: true }).pipe(
      catchError(err => {
        this.notificationsSubject.next(backup);
        this.headerNotificationsSubject.next(backupHeader);
        return throwError(() => err);
      })
    )
  }

  loadNotificationsPage(size: number, page: number) {
    return this.myNotifications(size, page).pipe(
      tap((response) =>
        this.notificationsSubject.next(response))
    );
  }

  myNotifications(size: number, page: number): Observable<PersonNotificationPageResponse> {
    return this.http.get<PersonNotificationPageResponse>(`${this.baseURL}?size=${size}&page=${page}`, { withCredentials: true });
  }

  getAllForAdmin(size: number, page: number, keyword: string, sortBy: string, sortDirection: string): Observable<any> {
    let params = new HttpParams()
      .set('size', size)
      .set('page', page)
      .set('sort', sortBy + ','+ sortDirection);

      if (keyword) {
        params = params.set('keyword', keyword);
      }

    return this.http.get<any>(`${this.adminUrl}`, { params, withCredentials: true });
  }

  send(request: NotificationRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.adminUrl}`, request, { withCredentials: true });
  }

  getPageContainingId(notificationId: number, pageSize: number): Observable<PersonNotificationPageResponse> {
    return this.http.get<PersonNotificationPageResponse>(`${this.baseURL}/indexed/${notificationId}?pageSize=${pageSize}`, { withCredentials: true })
      .pipe(tap(resp => this.notificationsSubject.next(resp)));
  }
}
