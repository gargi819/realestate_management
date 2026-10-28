import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { Property, RentalComparisonResponse } from '../models/property';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private baseUrl = 'http://localhost:8080/api/properties';

  // Timeout duration in milliseconds (10 seconds)
  private readonly REQUEST_TIMEOUT = 10000;

  private getAuthHeaders(): { headers: HttpHeaders } {
    const user = this.authService.getCurrentUser();
    let headers = new HttpHeaders();
    if (user && user.id) {
      headers = headers.set('X-User-Id', user.id.toString());
    }
    return { headers };
  }

  // 1. Get all properties
  getAllProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(this.baseUrl).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 2. Get property by ID
  getPropertyById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.baseUrl}/${id}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 3. Add a new property (with X-User-Id for Owner/Admin authorization)
  addProperty(property: Property): Observable<Property> {
    return this.http.post<Property>(this.baseUrl, property, this.getAuthHeaders()).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 4. Update an existing property (with X-User-Id to ensure owner match or Admin)
  updateProperty(id: number, property: Property): Observable<Property> {
    return this.http.put<Property>(`${this.baseUrl}/${id}`, property, this.getAuthHeaders()).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 5. Delete a property by ID (with X-User-Id)
  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`, this.getAuthHeaders()).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 6. Search properties by location
  searchByLocation(location: string): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.baseUrl}/search?location=${encodeURIComponent(location)}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 7. Filter properties by property type
  filterByPropertyType(propertyType: string): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.baseUrl}/filter?propertyType=${encodeURIComponent(propertyType)}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 8. Filter properties by status
  filterByStatus(status: string): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.baseUrl}/filter/status?status=${encodeURIComponent(status)}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 9. Get smart cheaper rental comparisons for a rental property
  getCheaperRentals(id: number): Observable<RentalComparisonResponse> {
    return this.http.get<RentalComparisonResponse>(`${this.baseUrl}/${id}/cheaper-rentals`).pipe(timeout(this.REQUEST_TIMEOUT));
  }
}
