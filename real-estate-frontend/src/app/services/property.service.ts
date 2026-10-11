import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { Property } from '../models/property';

@Injectable({
  providedIn: 'root'
})
export class PropertyService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/properties';

  // Timeout duration in milliseconds (10 seconds)
  private readonly REQUEST_TIMEOUT = 10000;

  // 1. Get all properties
  getAllProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(this.baseUrl).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 2. Get property by ID
  getPropertyById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.baseUrl}/${id}`).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 3. Add a new property
  addProperty(property: Property): Observable<Property> {
    return this.http.post<Property>(this.baseUrl, property).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 4. Update an existing property
  updateProperty(id: number, property: Property): Observable<Property> {
    return this.http.put<Property>(`${this.baseUrl}/${id}`, property).pipe(timeout(this.REQUEST_TIMEOUT));
  }

  // 5. Delete a property by ID
  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(timeout(this.REQUEST_TIMEOUT));
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
}
