import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { Property } from '../models/property';
import { RentalHistory } from '../models/rental-history';

export interface UserSummary {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: string;
  propertyCount: number;
  rentalCount: number;
}

export interface AdminStats {
  totalUsers: number;
  totalProperties: number;
  totalRenters: number;
  totalBuyers: number;
  totalOwners: number;
}

export interface AdminUserDetails {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: string;
  properties: Property[];
  rentalHistory: RentalHistory[];
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/admin';
  private readonly REQUEST_TIMEOUT = 10000;

  getStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.baseUrl}/stats`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  getAllUsers(): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.baseUrl}/users`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  getUserDetails(userId: number): Observable<AdminUserDetails> {
    return this.http.get<AdminUserDetails>(`${this.baseUrl}/users/${userId}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }
}
