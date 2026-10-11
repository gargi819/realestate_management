import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { User, RentalHistory, CheaperRentalResponse } from '../models/rental-history';

@Injectable({
  providedIn: 'root'
})
export class RentalHistoryService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/rental-history';
  private readonly REQUEST_TIMEOUT = 10000;

  // 1. Get all registered tenants
  getTenants(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/tenants`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 2. Get rental history for a specific tenant
  getRentalHistoryByTenant(tenantId: number): Observable<RentalHistory[]> {
    return this.http.get<RentalHistory[]>(`${this.baseUrl}/tenant/${tenantId}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 3. Find cheaper available rentals compared to tenant's most recent rent
  getCheaperRentals(tenantId: number): Observable<CheaperRentalResponse> {
    return this.http.get<CheaperRentalResponse>(`${this.baseUrl}/tenant/${tenantId}/cheaper-rentals`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 4. Add a new rental history record
  addRentalHistory(record: any): Observable<RentalHistory> {
    return this.http.post<RentalHistory>(this.baseUrl, record).pipe(timeout(this.REQUEST_TIMEOUT));
  }
}
