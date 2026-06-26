import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { Courier } from "../../models/person/courier.model";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CourierService {
  private apiUrl = `${environment.apiUrl}/api/couriers`;

  constructor(private http: HttpClient) { }

  availableCouriers(): Observable<Courier[]> {
    return this.http.get<Courier[]>(`${this.apiUrl}/available`, { withCredentials: true });
  }

}
