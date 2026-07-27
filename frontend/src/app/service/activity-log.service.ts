import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import { environment } from "../../environments/environment";
import { ActivityModel } from "../models/activity.model";
import {Observable} from "rxjs";
import {PaginatedResponse} from "../models/paginated-response.model";

@Injectable({
  providedIn: 'root'
})
export class ActivityLogService {
  private apiUrl = `${environment.apiUrl}/api/activity`;

  constructor(private http: HttpClient) { }

  getAll(page: number, size: number, sortBy: string, role: string, keyword: string): Observable<PaginatedResponse<ActivityModel>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sortBy)
      .set('role', role)
      .set('keyword', keyword);

    return this.http.get<PaginatedResponse<ActivityModel>>(`${this.apiUrl}`, { params });
  }
}
