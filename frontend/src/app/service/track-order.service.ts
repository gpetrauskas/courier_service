import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TrackOrderService {
  private baseUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  getPackageStatus(trackingNumber: string): Observable<string> {
    return this.http.get<string>(`${this.baseUrl}/trackOrder/${trackingNumber}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error occurred.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client side error: ${error.error.message}`
    } else {
      errorMessage = `Backend error code: ${error.status}, body: ${error.error}`;
    }
    return throwError(errorMessage);
  }
}
