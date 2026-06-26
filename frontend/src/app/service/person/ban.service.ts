import { Injectable } from '@angular/core';
import { BanActionRequestDTO } from "../../models/person/ban-action-request-dto.model";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class BanService {
  private apiUrl = `${environment.apiUrl}/api/ban`;

  constructor(private http: HttpClient) { }

  banUnban(personId: number, request: BanActionRequestDTO): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${personId}`, request, { responseType: 'text' as 'json', withCredentials: true });
  }
}
