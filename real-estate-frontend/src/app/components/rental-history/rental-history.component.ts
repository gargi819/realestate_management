import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RentalHistoryService } from '../../services/rental-history.service';
import { PropertyService } from '../../services/property.service';
import { AuthService } from '../../services/auth.service';
import { User, RentalHistory, CheaperRentalDTO, CheaperRentalResponse } from '../../models/rental-history';
import { Property } from '../../models/property';

@Component({
  selector: 'app-rental-history',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './rental-history.component.html',
  styleUrl: './rental-history.component.css'
})
export class RentalHistoryComponent implements OnInit {
  private router = inject(Router);
  private rentalService = inject(RentalHistoryService);
  private propertyService = inject(PropertyService);
  public authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  // Tenant Authentication & Selection State
  tenants: User[] = [];
  selectedTenantId: number = this.authService.getCurrentUser()?.id || 1;
  currentTenant: User | null = this.authService.getCurrentUser();

  // Rental History State
  rentalRecords: RentalHistory[] = [];
  loadingHistory = false;
  historyError = '';

  // Cheaper Rentals State
  cheaperResponse: CheaperRentalResponse | null = null;
  filteredCheaperRentals: CheaperRentalDTO[] = [];
  loadingCheaper = false;
  cheaperError = '';
  hasSearchedCheaper = false;

  // Filter & Search on Cheaper Rentals
  searchLocation = '';
  selectedPropertyType = '';

  // Add Rental Record Modal State
  showAddModal = false;
  savingRecord = false;
  addModalError = '';
  allProperties: Property[] = [];
  newRecord = {
    propertyId: 0,
    monthlyRent: 0,
    startDate: '',
    endDate: ''
  };

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user && user.role === 'ADMIN') {
      this.router.navigate(['/admin']);
      return;
    }

    if (user && user.id) {
      this.currentTenant = user;
      this.selectedTenantId = user.id;
    } else {
      this.currentTenant = { id: 1, name: 'Rahul Sharma', email: 'rahul.sharma@example.com', role: 'TENANT' };
      this.selectedTenantId = 1;
    }
    this.loadRentalHistory(this.selectedTenantId);
    this.loadPropertiesForModal();
  }

  loadTenants(): void {
    // Deprecated for user privacy
  }

  onTenantChange(newTenantId: number): void {
    // Deprecated for user privacy
  }

  // 3. Load Rental History for currently active tenant
  loadRentalHistory(tenantId: number): void {
    this.loadingHistory = true;
    this.historyError = '';
    this.cdr.detectChanges();

    this.rentalService.getRentalHistoryByTenant(tenantId).subscribe({
      next: (data) => {
        this.rentalRecords = data || [];
        this.loadingHistory = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading rental history:', err);
        this.historyError = 'Could not load rental history. Please ensure the backend is running.';
        this.loadingHistory = false;
        this.cdr.detectChanges();
      }
    });
  }

  // 4. Find Cheaper Rentals comparing to most recent rental
  findCheaperRentals(): void {
    if (!this.selectedTenantId) return;

    this.loadingCheaper = true;
    this.cheaperError = '';
    this.hasSearchedCheaper = true;
    this.cdr.detectChanges();

    this.rentalService.getCheaperRentals(this.selectedTenantId).subscribe({
      next: (response) => {
        this.cheaperResponse = response;
        this.applyCheaperFilters();
        this.loadingCheaper = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error finding cheaper rentals:', err);
        this.cheaperError = 'Failed to find cheaper rentals. Please try again.';
        this.loadingCheaper = false;
        this.cdr.detectChanges();
      }
    });
  }

  // 5. Apply filters (Location & Property Type) on cheaper recommendations
  applyCheaperFilters(): void {
    if (!this.cheaperResponse) {
      this.filteredCheaperRentals = [];
      return;
    }

    let list = [...this.cheaperResponse.cheaperRentals];

    const locQuery = this.searchLocation.trim().toLowerCase();
    if (locQuery) {
      list = list.filter(item =>
        item.property?.location?.toLowerCase().includes(locQuery) ||
        item.property?.title?.toLowerCase().includes(locQuery)
      );
    }

    if (this.selectedPropertyType) {
      list = list.filter(item =>
        item.property?.propertyType?.toLowerCase() === this.selectedPropertyType.toLowerCase()
      );
    }

    this.filteredCheaperRentals = list;
    this.cdr.detectChanges();
  }

  // Modal helpers for adding custom rental record
  loadPropertiesForModal(): void {
    this.propertyService.getAllProperties().subscribe({
      next: (props) => {
        this.allProperties = props || [];
      },
      error: (err) => console.error('Error loading properties for modal:', err)
    });
  }

  openAddModal(): void {
    this.showAddModal = true;
    this.addModalError = '';
    this.newRecord = {
      propertyId: this.allProperties.length > 0 ? this.allProperties[0].id! : 0,
      monthlyRent: 45000,
      startDate: new Date().toISOString().substring(0, 10),
      endDate: ''
    };
    this.cdr.detectChanges();
  }

  closeAddModal(): void {
    this.showAddModal = false;
    this.cdr.detectChanges();
  }

  saveRentalRecord(): void {
    if (!this.newRecord.propertyId) {
      this.addModalError = 'Please select a property.';
      return;
    }
    if (!this.newRecord.monthlyRent || this.newRecord.monthlyRent <= 0) {
      this.addModalError = 'Please enter a valid monthly rent greater than 0.';
      return;
    }
    if (!this.newRecord.startDate) {
      this.addModalError = 'Please select a rental start date.';
      return;
    }

    this.savingRecord = true;
    this.addModalError = '';

    const payload = {
      tenant: { id: this.selectedTenantId },
      property: { id: this.newRecord.propertyId },
      monthlyRent: this.newRecord.monthlyRent,
      startDate: this.newRecord.startDate,
      endDate: this.newRecord.endDate || null
    };

    this.rentalService.addRentalHistory(payload).subscribe({
      next: () => {
        this.savingRecord = false;
        this.showAddModal = false;
        this.loadRentalHistory(this.selectedTenantId);
        // Refresh cheaper recommendations if already active
        if (this.hasSearchedCheaper) {
          this.findCheaperRentals();
        }
      },
      error: (err) => {
        console.error('Error adding rental record:', err);
        this.addModalError = 'Failed to save rental record. Please try again.';
        this.savingRecord = false;
        this.cdr.detectChanges();
      }
    });
  }
}
