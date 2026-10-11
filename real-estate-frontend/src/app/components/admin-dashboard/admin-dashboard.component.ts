import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService, AdminStats, UserSummary, AdminUserDetails } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  public authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  // Authorization check
  isAdmin = false;

  // Stats & User Lists
  stats: AdminStats | null = null;
  users: UserSummary[] = [];
  filteredUsers: UserSummary[] = [];

  // Loading & Error States
  loading = true;
  errorMessage = '';

  // Filter & Search Controls
  searchTerm = '';
  selectedRoleFilter = 'ALL';

  // Individual User Details Modal
  selectedUser: AdminUserDetails | null = null;
  loadingDetails = false;
  detailsError = '';
  showDetailsModal = false;

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.isAdmin = user?.role === 'ADMIN';

    if (this.isAdmin) {
      this.loadDashboardData();
    } else {
      this.loading = false;
    }
  }

  loadDashboardData(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    // 1. Fetch Stats
    this.adminService.getStats().subscribe({
      next: (s) => {
        this.stats = s;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error fetching stats:', err)
    });

    // 2. Fetch Users
    this.adminService.getAllUsers().subscribe({
      next: (uList) => {
        this.users = uList || [];
        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching users:', err);
        this.errorMessage = 'Failed to load users from the server.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  setRoleFilter(role: string): void {
    this.selectedRoleFilter = role;
    this.applyFilters();
  }

  onSearch(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let list = this.users;

    // Filter by role
    if (this.selectedRoleFilter !== 'ALL') {
      list = list.filter(u => u.role?.toUpperCase() === this.selectedRoleFilter);
    }

    // Filter by search query
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const q = this.searchTerm.toLowerCase().trim();
      list = list.filter(u =>
        u.name?.toLowerCase().includes(q) ||
        u.email?.toLowerCase().includes(q) ||
        (u.phone && u.phone.toLowerCase().includes(q))
      );
    }

    this.filteredUsers = list;
    this.cdr.detectChanges();
  }

  openUserDetails(userId: number): void {
    this.showDetailsModal = true;
    this.loadingDetails = true;
    this.detailsError = '';
    this.selectedUser = null;
    this.cdr.detectChanges();

    this.adminService.getUserDetails(userId).subscribe({
      next: (details) => {
        this.selectedUser = details;
        this.loadingDetails = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching user details:', err);
        this.detailsError = 'Could not load details for this user.';
        this.loadingDetails = false;
        this.cdr.detectChanges();
      }
    });
  }

  closeUserDetails(): void {
    this.showDetailsModal = false;
    this.selectedUser = null;
    this.cdr.detectChanges();
  }
}
