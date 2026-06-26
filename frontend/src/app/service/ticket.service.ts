import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { TicketCreate } from '../models/ticket/ticket-create.dto';
import { Ticket } from '../models/ticket/ticket.model';
import { AdminTicket } from '../models/ticket/admin-ticket.model';
import { ApiResponse } from '../models/api-response.dto';
import { HttpClient, HttpParams } from '@angular/common/http';
import { TicketUpdateRequest } from '../models/ticket/ticket-update-request.dto';
import { Observable, map } from 'rxjs';
import { PaginatedResponse } from "../models/paginated-response.model";
import {TicketComment} from "../models/ticket/ticket-comment.model";
import {AddComment} from "../models/ticket/add-comment.model";

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private adminUrl = `${environment.apiUrl}/api/admin/tickets`;
  private baseURL = `${environment.apiUrl}/api/tickets`;

  constructor(private http: HttpClient) { }

  create(newTicket: TicketCreate): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseURL}`, newTicket, { withCredentials: true });
  }

  getTicket(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.baseURL}/${id}`, { withCredentials: true });
  }

  close(ticketId: number) {
    return this.http.patch<ApiResponse>(`${this.baseURL}/${ticketId}/close`, { withCredentials: true });
  }

  updateTicketProperty(request: TicketUpdateRequest) {
    console.log('ee', request);
    return this.http.patch<ApiResponse>(`${this.baseURL}/${request.id}`, request.partTarget, { withCredentials: true });
  }

  getAllForAdmin(page: number, size: number, status?: string, personId?: number): Observable<PaginatedResponse<Ticket>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) {
      params = params.set('status', status.toString());
    }

    if (personId) {
      params = params.set('personId', personId);
    }

    return this.http.get<PaginatedResponse<AdminTicket>>(`${this.adminUrl}`, { params, withCredentials: true })
      .pipe(
        map(res => ({
          ...res,
          data: res.data.map(d => ({
            ...d.ticket,
            creator: d.creator
          }))
        }))
      );
  }

  getAllForUser(page: number, size: number, status?: string): Observable<PaginatedResponse<Ticket>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

      if (status) {
        params = params.set('status', status.toString());
      }

    return this.http.get<PaginatedResponse<Ticket>>(`${this.baseURL}/my`, { params, withCredentials: true });
  }


  getComments(ticketId: number, page: number, size: number): Observable<PaginatedResponse<TicketComment>> {
    const params = new HttpParams()
      .set('currentPage', page.toString())
      .set('pageSize', size.toString());

    return this.http.get<PaginatedResponse<TicketComment>>(`${this.baseURL}/${ticketId}/comments`, { params, withCredentials: true });
  }

  addComment(ticketId: number, request: AddComment): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseURL}/${ticketId}/comments`, request, { withCredentials: true });
  }
}
