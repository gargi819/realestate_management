import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  // Tab: 'login' | 'register'
  activeTab: 'login' | 'register' = 'login';

  // Login Form
  email = '';
  password = '';
  loginError = '';
  loading = false;
  successMessage = '';

  // Register Form
  regName = '';
  regEmail = '';
  regPassword = '';
  regPhone = '';
  regRole = 'TENANT';
  regError = '';

  ngOnInit(): void {
    // If already logged in, show current user email
    const current = this.authService.getCurrentUser();
    if (current && current.email) {
      this.email = current.email;
    }
  }

  // Quick Demo Autofill Helper
  fillDemoCredentials(demoEmail: string, demoPass: string): void {
    this.email = demoEmail;
    this.password = demoPass;
    this.loginError = '';
    this.cdr.detectChanges();
  }

  // Handle Login
  onLogin(): void {
    this.loginError = '';
    this.successMessage = '';

    if (!this.email.trim() || !this.password.trim()) {
      this.loginError = 'Please provide both email address and password.';
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    this.authService.login(this.email.trim(), this.password).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.successMessage = `Welcome back, ${res.name}! Redirecting...`;
          this.cdr.detectChanges();
          setTimeout(() => {
            this.router.navigate(['/rental-history']);
          }, 900);
        } else {
          this.loginError = res.message || 'Login failed.';
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('Login error:', err);
        this.loading = false;
        if (err.status === 401) {
          this.loginError = err.error?.message || 'Invalid email or password.';
        } else if (err.status === 0 || err.name === 'TimeoutError') {
          this.loginError = 'Cannot connect to backend server. Make sure Spring Boot is running on port 8080.';
        } else {
          this.loginError = err.error?.message || 'Authentication failed. Please check your credentials.';
        }
        this.cdr.detectChanges();
      }
    });
  }

  // Handle Register
  onRegister(): void {
    this.regError = '';
    this.successMessage = '';

    if (!this.regName.trim() || !this.regEmail.trim() || !this.regPassword.trim()) {
      this.regError = 'Please fill out all required fields.';
      return;
    }

    if (this.regPassword.length < 4) {
      this.regError = 'Password must be at least 4 characters long.';
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    this.authService.register({
      name: this.regName.trim(),
      email: this.regEmail.trim(),
      password: this.regPassword,
      phone: this.regPhone.trim(),
      role: this.regRole
    }).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.successMessage = `Account created successfully! Welcome, ${res.name}!`;
          this.cdr.detectChanges();
          setTimeout(() => {
            this.router.navigate(['/rental-history']);
          }, 900);
        } else {
          this.regError = res.message || 'Registration failed.';
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.loading = false;
        if (err.status === 409) {
          this.regError = 'An account with this email already exists. Please sign in instead.';
        } else {
          this.regError = err.error?.message || 'Registration failed. Please try again.';
        }
        this.cdr.detectChanges();
      }
    });
  }
}
