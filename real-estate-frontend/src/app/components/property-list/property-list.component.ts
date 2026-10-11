import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PropertyService } from '../../services/property.service';
import { Property } from '../../models/property';

@Component({
  selector: 'app-property-list',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './property-list.component.html',
  styleUrl: './property-list.component.css'
})
export class PropertyListComponent implements OnInit {
  private propertyService = inject(PropertyService);
  private cdr = inject(ChangeDetectorRef);

  // Master copy of all properties fetched from server
  allProperties: Property[] = [];
  // Filtered properties currently displayed on screen
  properties: Property[] = [];

  loading = false;
  errorMessage = '';
  successMessage = '';

  // Search & Filter controls
  searchLocation = '';
  selectedPropertyType = '';
  selectedStatus = '';

  // In-app Delete Confirmation Modal State
  propertyToDelete: Property | null = null;
  deleting = false;

  ngOnInit(): void {
    this.loadAllProperties();
  }

  // 1. Load all properties from backend
  loadAllProperties(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.propertyService.getAllProperties().subscribe({
      next: (data) => {
        this.allProperties = data || [];
        this.applyCombinedFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching properties:', err);
        if (err?.name === 'TimeoutError' || err?.status === 0) {
          this.errorMessage = 'Cannot connect to server. Ensure Spring Boot is running on port 8080.';
        } else {
          this.errorMessage = 'Failed to load properties. Please try again.';
        }
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Centralized filter logic: Combines Search (title + location) + Property Type + Status
  applyCombinedFilters(): void {
    let result = [...this.allProperties];

    // Location or title text search
    const query = this.searchLocation.trim().toLowerCase();
    if (query) {
      result = result.filter((prop) => {
        const locationMatch = prop.location ? prop.location.toLowerCase().includes(query) : false;
        const titleMatch = prop.title ? prop.title.toLowerCase().includes(query) : false;
        return locationMatch || titleMatch;
      });
    }

    // Property Type filter
    if (this.selectedPropertyType) {
      result = result.filter(
        (prop) => prop.propertyType?.toLowerCase() === this.selectedPropertyType.toLowerCase()
      );
    }

    // Status filter
    if (this.selectedStatus) {
      result = result.filter(
        (prop) => prop.status?.toLowerCase() === this.selectedStatus.toLowerCase()
      );
    }

    this.properties = result;
    this.cdr.detectChanges();
  }

  // 2. Trigger search on button click or Enter key
  onSearch(): void {
    this.applyCombinedFilters();
  }

  // 3. Filter by property type dropdown
  onPropertyTypeChange(): void {
    this.applyCombinedFilters();
  }

  // 4. Filter by status dropdown
  onStatusChange(): void {
    this.applyCombinedFilters();
  }

  // 5. Clear all filters & search
  clearFilters(): void {
    this.searchLocation = '';
    this.selectedPropertyType = '';
    this.selectedStatus = '';
    this.errorMessage = '';
    this.properties = [...this.allProperties];
    this.cdr.detectChanges();
  }

  // 6. Delete Confirmation Modal
  openDeleteModal(property: Property): void {
    this.propertyToDelete = property;
    this.cdr.detectChanges();
  }

  cancelDelete(): void {
    this.propertyToDelete = null;
    this.cdr.detectChanges();
  }

  confirmDelete(): void {
    if (!this.propertyToDelete?.id) return;
    const id = this.propertyToDelete.id;
    this.deleting = true;
    this.cdr.detectChanges();

    this.propertyService.deleteProperty(id).subscribe({
      next: () => {
        this.allProperties = this.allProperties.filter((p) => p.id !== id);
        this.applyCombinedFilters();
        this.propertyToDelete = null;
        this.deleting = false;
        this.successMessage = 'Property deleted successfully!';
        this.cdr.detectChanges();
        setTimeout(() => {
          this.successMessage = '';
          this.cdr.detectChanges();
        }, 3000);
      },
      error: (err) => {
        console.error('Error deleting property:', err);
        this.errorMessage = 'Something went wrong. Please try again.';
        this.propertyToDelete = null;
        this.deleting = false;
        this.cdr.detectChanges();
      }
    });
  }
}
