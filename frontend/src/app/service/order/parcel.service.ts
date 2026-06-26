import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ParcelService {
  private baseURL = `${environment.apiUrl}/api/parcel`;
  constructor(private http: HttpClient) { }

  availableParcelsCount(): Observable<Record<string, number>> {
    return this.http.get<Record<string, number>>(`${this.baseURL}/availableCount`, { withCredentials: true });
  }
}
