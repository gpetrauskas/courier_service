import { Injectable } from '@angular/core';
import { PersonEditDTO } from "../../models/person/person-edit.dto";
import { Observable } from "rxjs";
import { ApiResponse } from "../../models/api-response.dto";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { PasswordChange } from "../../models/person/password-change.dto";
import {MyInfoUser} from "../../models/person/my-info-user.model";
import {MyInfoCourier} from "../../models/person/my-info-courier.model";
import {MyInfoAdmin} from "../../models/person/my-info-admin.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/api/me`;

  constructor(private http: HttpClient) { }

  me(): Observable<MyInfoUser | MyInfoCourier | MyInfoAdmin> {
    return this.http.get<MyInfoUser | MyInfoCourier | MyInfoAdmin>(`${this.apiUrl}`, { withCredentials: true });
  }

  updateMyInfo(updatedData: PersonEditDTO): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiUrl}/my-info`, updatedData, { withCredentials: true });
  }

  changePassword(pass: PasswordChange): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiUrl}/changePassword`, pass, { withCredentials: true });
  }
}
