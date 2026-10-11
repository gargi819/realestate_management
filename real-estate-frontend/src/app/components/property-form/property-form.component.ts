import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PropertyService } from '../../services/property.service';
import { Property } from '../../models/property';

@Component({
  selector: 'app-property-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './property-form.component.html',
  styleUrl: './property-form.component.css'
})
export class PropertyFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private propertyService = inject(PropertyService);
  private cdr = inject(ChangeDetectorRef);

  propertyForm!: FormGroup;
  isEditMode = false;
  propertyId: number | null = null;
  loading = false;
  submitting = false;
  errorMessage = '';
  successMessage = '';

  propertyTypes = ['Apartment', 'House', 'Villa', 'Plot', 'Commercial'];
  statusOptions = ['Available', 'Sold', 'For Rent'];

  ngOnInit(): void {
    this.initForm();

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.propertyId = Number(idParam);
      this.loadProperty(this.propertyId);
    }
  }

  private initForm(): void {
    this.propertyForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(0.01)]],
      propertyType: ['', Validators.required],
      location: ['', Validators.required],
      area: [null, [Validators.required, Validators.min(0.01)]],
      bedrooms: [0, [Validators.required, Validators.min(0)]],
      bathrooms: [0, [Validators.required, Validators.min(0)]],
      status: ['Available', Validators.required]
    });
  }

  private loadProperty(id: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.detectChanges();

    this.propertyService.getPropertyById(id).subscribe({
      next: (property: Property) => {
        this.propertyForm.patchValue(property);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching property:', err);
        if (err?.name === 'TimeoutError' || err?.status === 0) {
          this.errorMessage = 'Cannot connect to server. Make sure the Spring Boot backend is running on port 8080.';
        } else if (err?.status === 404) {
          this.errorMessage = 'Property not found.';
        } else {
          this.errorMessage = 'Failed to load property. Please try again.';
        }
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit(): void {
    if (this.propertyForm.invalid) {
      this.propertyForm.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    this.cdr.detectChanges();
    const propertyData: Property = this.propertyForm.value;

    if (this.isEditMode && this.propertyId) {
      this.propertyService.updateProperty(this.propertyId, propertyData).subscribe({
        next: () => {
          this.submitting = false;
          this.successMessage = 'Property updated successfully!';
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/properties']), 1500);
        },
        error: (err) => {
          console.error('Error updating property:', err);
          this.errorMessage = 'Unable to save property. Please try again.';
          this.submitting = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.propertyService.addProperty(propertyData).subscribe({
        next: () => {
          this.submitting = false;
          this.successMessage = 'Property added successfully!';
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/properties']), 1500);
        },
        error: (err) => {
          console.error('Error adding property:', err);
          this.errorMessage = 'Unable to save property. Please try again.';
          this.submitting = false;
          this.cdr.detectChanges();
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/properties']);
  }
}
