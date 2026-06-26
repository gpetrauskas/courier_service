import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";
import { environment } from "../../../environments/environment";
import { PersonDetailsDTO } from "../../models/person/person-details.model";
import { PersonResponseDTO } from "../../models/person/person-basic.model";
import { AdminPersonUpdate } from "../../models/person/admin-person-update.dto";
import { PaginatedResponse } from "../../models/paginated-response.model";

@Injectable({
  providedIn: 'root'
})
export class PersonService {
  private apiUrl = `${environment.apiUrl}/api/person`;

  constructor(private http: HttpClient) { }

  findPersonById(personId: number): Observable<PersonDetailsDTO> {
    return this.http.get<PersonDetailsDTO>(`${this.apiUrl}/${personId}`, { withCredentials: true });
  }

  fetchAllPersons(page: number, size: number, role?: string, searchKeyword?: string): Observable<PaginatedResponse<PersonResponseDTO>> {

    let params = this.createInitialParamsWithPageAndSize(page, size);
    if (role) {
      params = params.set("role", role);
    }

    if (searchKeyword) {
      params = params.set("searchKey", `${encodeURIComponent(searchKeyword)}`);
    }

    return this.http.get<PaginatedResponse<PersonResponseDTO>>(`${this.apiUrl}`, { params, withCredentials: true });
  }

  updatePersonDetails(personId: number, updateUserDetails: AdminPersonUpdate): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/update/${personId}`, updateUserDetails, {responseType: 'text' as 'json', withCredentials: true });
  }

  deletePerson(personId: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/delete/${personId}`, { responseType: 'text' as 'json', withCredentials: true })
  }

  createInitialParamsWithPageAndSize(page: number, size: number) {
    return new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString());
  }
}
