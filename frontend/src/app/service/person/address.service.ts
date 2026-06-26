import { Injectable, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Address } from '../../models/address/address.model';

@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private baseURL = `${environment.apiUrl}/api/me`;

  constructor(private http: HttpClient) { }

  fetchMySavedAddress(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.baseURL}/addresses`, { withCredentials: true });
  }

  updateAddress(addressId: number, address: Address): Observable<Address> {
    return this.http.patch<Address>(`${this.baseURL}/addresses/${addressId}`, address, { withCredentials: true });
  }

  deleteAddress(addressId: number): Observable<string> {
    return this.http.delete(`${this.baseURL}/addresses/${addressId}`, { responseType: 'text', withCredentials: true });
  }

}
