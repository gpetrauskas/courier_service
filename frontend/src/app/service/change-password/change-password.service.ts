import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { PasswordChange } from "../../models/person/password-change.dto";
import { Observable } from "rxjs";
import { ApiResponse } from "../../models/api-response.dto";

@Injectable({
  providedIn: 'root'
})
export class ChangePasswordService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  changePassword(pass: PasswordChange): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(`${this.apiUrl}/api/password`, pass, { withCredentials: true });
  }
}
