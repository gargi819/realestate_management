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
  totalRentalContracts: number;
}

export interface AdminProperty {
  id: number;
  title: string;
  description: string;
  price: number;
  propertyType: string;
  location: string;
  area: number;
  bedrooms: number;
  bathrooms: number;
  status: string;
  ownerId?: number;
  ownerName?: string;
  ownerEmail?: string;
  ownerPhone?: string;
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

  getAllProperties(): Observable<AdminProperty[]> {
    return this.http.get<AdminProperty[]>(`${this.baseUrl}/properties`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  updatePropertyStatus(propertyId: number, status: string): Observable<Property> {
    return this.http.put<Property>(`${this.baseUrl}/properties/${propertyId}/status`, { status }).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  deleteProperty(propertyId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/properties/${propertyId}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  getAllRentalContracts(): Observable<RentalHistory[]> {
    return this.http.get<RentalHistory[]>(`${this.baseUrl}/rental-history`).pipe(timeout(this.REQUEST_TIMEOUT));
  }
}
