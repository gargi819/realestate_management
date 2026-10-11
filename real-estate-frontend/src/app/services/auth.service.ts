import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { timeout } from 'rxjs/operators';
import { User } from '../models/rental-history';

export interface AuthResponse {
  success: boolean;
  message: string;
  id?: number;
  name?: string;
  email?: string;
  phone?: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private baseUrl = 'http://localhost:8080/api/auth';
  private readonly REQUEST_TIMEOUT = 10000;

  private currentUserSubject = new BehaviorSubject<User | null>(this.loadStoredUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  private loadStoredUser(): User | null {
    try {
      const stored = localStorage.getItem('currentUser');
      if (stored) {
        return JSON.parse(stored);
      }
    } catch (e) {
      console.error('Error reading stored user session:', e);
    }
    // Default demo session if none stored
    return {
      id: 1,
      name: 'Rahul Sharma',
      email: 'rahul.sharma@example.com',
      role: 'TENANT'
    };
  }

  // 1. Authenticate with Email & Password
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { email, password })
      .pipe(
        timeout(this.REQUEST_TIMEOUT),
        tap((res) => {
          if (res && res.success && res.id) {
            const user: User = {
              id: res.id,
              name: res.name || 'User',
              email: res.email || email,
              phone: res.phone,
              role: res.role
            };
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
          }
        })
      );
  }

  // 2. Register New User in Database
  register(userData: { name: string; email: string; password: string; phone?: string; role?: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, userData)
      .pipe(
        timeout(this.REQUEST_TIMEOUT),
        tap((res) => {
          if (res && res.success && res.id) {
            const user: User = {
              id: res.id,
              name: res.name || userData.name,
              email: res.email || userData.email,
              phone: res.phone || userData.phone,
              role: res.role || userData.role || 'TENANT'
            };
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
          }
        })
      );
  }

  // 3. Log out
  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  // 4. Switch user session directly (for demo switcher)
  setCurrentUser(user: User): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }
}
