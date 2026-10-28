import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AdminService, AdminStats, UserSummary, AdminUserDetails, AdminProperty } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { RentalHistory } from '../../models/rental-history';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  public authService = inject(AuthService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  // Authorization check
  isAdmin = false;

  // Active Section / Tab: 'overview' | 'properties' | 'users' | 'rentals'
  activeTab: 'overview' | 'properties' | 'users' | 'rentals' = 'overview';

  // Stats
  stats: AdminStats | null = null;
  loadingStats = true;

  // Users Directory (Non-Admin only)
  users: UserSummary[] = [];
  filteredUsers: UserSummary[] = [];
  loadingUsers = false;
  userSearchTerm = '';
  selectedRoleFilter = 'ALL';

  // Properties Management
  properties: AdminProperty[] = [];
  filteredProperties: AdminProperty[] = [];
  loadingProperties = false;
  propSearchTerm = '';
  propTypeFilter = '';
  propStatusFilter = '';
  statusUpdatingId: number | null = null;

  // Property Delete Confirmation Modal
  propertyToDelete: AdminProperty | null = null;
  deletingProperty = false;

  // Rental Activity Contracts
  rentalContracts: RentalHistory[] = [];
  filteredRentals: RentalHistory[] = [];
  loadingRentals = false;
  rentalSearchTerm = '';
  rentalStatusFilter = 'ALL'; // 'ALL' | 'ACTIVE' | 'COMPLETED'

  // Individual User Details Modal
  selectedUser: AdminUserDetails | null = null;
  loadingDetails = false;
  detailsError = '';
  showDetailsModal = false;

  // Global Alerts
  errorMessage = '';
  successMessage = '';

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    this.isAdmin = user?.role === 'ADMIN';

    if (this.isAdmin) {
      this.loadAllData();
    } else {
      this.loadingStats = false;
    }
  }

  loadAllData(): void {
    this.errorMessage = '';
    this.loadStats();
    this.loadUsers();
    this.loadProperties();
    this.loadRentalContracts();
  }

  // 1. Fetch Stats
  loadStats(): void {
    this.loadingStats = true;
    this.adminService.getStats().subscribe({
      next: (s) => {
        this.stats = s;
        this.loadingStats = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching admin stats:', err);
        this.loadingStats = false;
        this.cdr.detectChanges();
      }
    });
  }

  // 2. Fetch Users (Non-Admin only)
  loadUsers(): void {
    this.loadingUsers = true;
    this.adminService.getAllUsers().subscribe({
      next: (uList) => {
        // Enforce exclusion of any ADMIN account on the frontend as well
        this.users = (uList || []).filter(u => u.role?.toUpperCase() !== 'ADMIN');
        this.applyUserFilters();
        this.loadingUsers = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching users:', err);
        this.loadingUsers = false;
        this.cdr.detectChanges();
      }
    });
  }

  // 3. Fetch Properties with Owner Details
  loadProperties(): void {
    this.loadingProperties = true;
    this.adminService.getAllProperties().subscribe({
      next: (pList) => {
        this.properties = pList || [];
        this.applyPropertyFilters();
        this.loadingProperties = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching admin properties:', err);
        this.loadingProperties = false;
        this.cdr.detectChanges();
      }
    });
  }

  // 4. Fetch Rental Contracts
  loadRentalContracts(): void {
    this.loadingRentals = true;
    this.adminService.getAllRentalContracts().subscribe({
      next: (rList) => {
        this.rentalContracts = rList || [];
        this.applyRentalFilters();
        this.loadingRentals = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching rental contracts:', err);
        this.loadingRentals = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Tab Switcher with Optional Filter Preset
  setActiveTab(tab: 'overview' | 'properties' | 'users' | 'rentals', userFilter?: string): void {
    this.activeTab = tab;
    if (userFilter) {
      this.selectedRoleFilter = userFilter;
      this.applyUserFilters();
    }
    this.cdr.detectChanges();
  }

  // Users Filter & Search
  setUserRoleFilter(role: string): void {
    this.selectedRoleFilter = role;
    this.applyUserFilters();
  }

  onUserSearch(): void {
    this.applyUserFilters();
  }

  applyUserFilters(): void {
    let list = this.users;

    if (this.selectedRoleFilter !== 'ALL') {
      list = list.filter(u => u.role?.toUpperCase() === this.selectedRoleFilter);
    }

    if (this.userSearchTerm && this.userSearchTerm.trim() !== '') {
      const q = this.userSearchTerm.toLowerCase().trim();
      list = list.filter(u =>
        u.name?.toLowerCase().includes(q) ||
        u.email?.toLowerCase().includes(q) ||
        (u.phone && u.phone.toLowerCase().includes(q))
      );
    }

    this.filteredUsers = list;
    this.cdr.detectChanges();
  }

  // Properties Filter & Search
  onPropSearch(): void {
    this.applyPropertyFilters();
  }

  onPropFilterChange(): void {
    this.applyPropertyFilters();
  }

  applyPropertyFilters(): void {
    let list = this.properties;

    if (this.propSearchTerm && this.propSearchTerm.trim() !== '') {
      const q = this.propSearchTerm.toLowerCase().trim();
      list = list.filter(p =>
        p.title?.toLowerCase().includes(q) ||
        p.location?.toLowerCase().includes(q) ||
        (p.ownerName && p.ownerName.toLowerCase().includes(q)) ||
        (p.ownerEmail && p.ownerEmail.toLowerCase().includes(q))
      );
    }

    if (this.propTypeFilter) {
      list = list.filter(p => p.propertyType?.toLowerCase() === this.propTypeFilter.toLowerCase());
    }

    if (this.propStatusFilter) {
      list = list.filter(p => p.status?.toLowerCase() === this.propStatusFilter.toLowerCase());
    }

    this.filteredProperties = list;
    this.cdr.detectChanges();
  }

  clearPropertyFilters(): void {
    this.propSearchTerm = '';
    this.propTypeFilter = '';
    this.propStatusFilter = '';
    this.filteredProperties = [...this.properties];
    this.cdr.detectChanges();
  }

  // Quick Status Change on Property
  changePropertyStatus(prop: AdminProperty, newStatus: string): void {
    if (!newStatus || newStatus === prop.status) return;

    this.statusUpdatingId = prop.id;
    this.adminService.updatePropertyStatus(prop.id, newStatus).subscribe({
      next: (updated) => {
        prop.status = updated.status;
        this.statusUpdatingId = null;
        this.showToast(`Status of "${prop.title}" updated to ${newStatus}.`);
        this.loadStats();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error updating property status:', err);
        this.errorMessage = 'Failed to update property status.';
        this.statusUpdatingId = null;
        this.cdr.detectChanges();
      }
    });
  }

  // Delete Property Confirmation Modal
  openDeletePropertyModal(prop: AdminProperty): void {
    this.propertyToDelete = prop;
    this.cdr.detectChanges();
  }

  cancelDeleteProperty(): void {
    this.propertyToDelete = null;
    this.cdr.detectChanges();
  }

  confirmDeleteProperty(): void {
    if (!this.propertyToDelete) return;
    const id = this.propertyToDelete.id;
    const title = this.propertyToDelete.title;
    this.deletingProperty = true;

    this.adminService.deleteProperty(id).subscribe({
      next: () => {
        this.properties = this.properties.filter(p => p.id !== id);
        this.applyPropertyFilters();
        this.propertyToDelete = null;
        this.deletingProperty = false;
        this.showToast(`Property "${title}" deleted successfully.`);
        this.loadStats();
        this.loadUsers();
        this.loadRentalContracts();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error deleting property:', err);
        this.errorMessage = 'Failed to delete property listing.';
        this.propertyToDelete = null;
        this.deletingProperty = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Rental Activity Filter & Search
  onRentalSearch(): void {
    this.applyRentalFilters();
  }

  setRentalStatusFilter(status: string): void {
    this.rentalStatusFilter = status;
    this.applyRentalFilters();
  }

  isLeaseActive(record: RentalHistory): boolean {
    if (!record.endDate) return true;
    const end = new Date(record.endDate);
    return end >= new Date();
  }

  applyRentalFilters(): void {
    let list = this.rentalContracts;

    if (this.rentalStatusFilter === 'ACTIVE') {
      list = list.filter(r => this.isLeaseActive(r));
    } else if (this.rentalStatusFilter === 'COMPLETED') {
      list = list.filter(r => !this.isLeaseActive(r));
    }

    if (this.rentalSearchTerm && this.rentalSearchTerm.trim() !== '') {
      const q = this.rentalSearchTerm.toLowerCase().trim();
      list = list.filter(r =>
        (r.tenant?.name && r.tenant.name.toLowerCase().includes(q)) ||
        (r.tenant?.email && r.tenant.email.toLowerCase().includes(q)) ||
        (r.property?.title && r.property.title.toLowerCase().includes(q)) ||
        (r.property?.location && r.property.location.toLowerCase().includes(q))
      );
    }

    this.filteredRentals = list;
    this.cdr.detectChanges();
  }

  // Individual User Details Modal
  openUserDetails(userId?: number): void {
    if (!userId) return;
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

  // Navigate from User Detail modal to tab
  viewOwnerPropertiesInTab(ownerName: string): void {
    this.closeUserDetails();
    this.activeTab = 'properties';
    this.propSearchTerm = ownerName;
    this.applyPropertyFilters();
  }

  viewTenantRentalsInTab(tenantName: string): void {
    this.closeUserDetails();
    this.activeTab = 'rentals';
    this.rentalSearchTerm = tenantName;
    this.applyRentalFilters();
  }

  showToast(msg: string): void {
    this.successMessage = msg;
    this.cdr.detectChanges();
    setTimeout(() => {
      this.successMessage = '';
      this.cdr.detectChanges();
    }, 4000);
  }
}
