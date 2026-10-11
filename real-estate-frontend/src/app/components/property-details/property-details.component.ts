import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PropertyService } from '../../services/property.service';
import { Property } from '../../models/property';

@Component({
  selector: 'app-property-details',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './property-details.component.html',
  styleUrl: './property-details.component.css'
})
export class PropertyDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private propertyService = inject(PropertyService);
  private cdr = inject(ChangeDetectorRef);

  property: Property | null = null;
  propertyId: number | null = null;
  loading = true;
  deleting = false;
  showDeleteModal = false;
  errorMessage = '';
  successMessage = '';

  // Feature 1: Affordability Checker State
  showAffordability = false;
  downPayment: number = 0;
  annualInterestRate: number = 8.5;
  tenureYears: number = 20;
  monthlyIncome: number = 100000;

  // Affordability Outputs
  loanAmount: number = 0;
  monthlyEMI: number = 0;
  totalInterest: number = 0;
  totalRepayment: number = 0;
  totalCost: number = 0;
  emiToIncomeRatio: number = 0;
  affordabilityStatus: string = '';
  affordabilityClass: string = '';
  affordabilityAdvice: string = '';
  affordabilityIcon: string = '';

  // Input Validation Errors
  affordabilityErrors: { [key: string]: string } = {};

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.propertyId = Number(idParam);
      this.loadProperty(this.propertyId);
    } else {
      this.loading = false;
      this.errorMessage = 'Property not found.';
      this.cdr.detectChanges();
    }
  }

  loadProperty(id: number): void {
    this.loading = true;
    this.errorMessage = '';
    this.propertyService.getPropertyById(id).subscribe({
      next: (data) => {
        this.property = data;
        this.loading = false;
        // Default down payment to 20% of property price
        if (this.property && this.property.price > 0) {
          this.downPayment = Math.round(this.property.price * 0.2);
          this.calculateAffordability();
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching property details:', err);
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

  // Toggle Affordability Section
  toggleAffordability(): void {
    this.showAffordability = !this.showAffordability;
    if (this.showAffordability) {
      if (this.property && this.downPayment === 0) {
        this.downPayment = Math.round(this.property.price * 0.2);
      }
      this.calculateAffordability();
    }
    this.cdr.detectChanges();
  }

  // Quick preset helpers
  setDownPaymentPercent(percentage: number): void {
    if (!this.property) return;
    this.downPayment = Math.round(this.property.price * (percentage / 100));
    this.calculateAffordability();
  }

  setInterestRatePreset(rate: number): void {
    this.annualInterestRate = rate;
    this.calculateAffordability();
  }

  setTenurePreset(years: number): void {
    this.tenureYears = years;
    this.calculateAffordability();
  }

  // Standard Home Loan EMI and Affordability Calculation
  calculateAffordability(): void {
    this.affordabilityErrors = {};
    if (!this.property) return;

    const price = this.property.price || 0;

    // 1. Validations
    if (this.downPayment < 0) {
      this.affordabilityErrors['downPayment'] = 'Down payment cannot be negative.';
    } else if (this.downPayment > price) {
      this.affordabilityErrors['downPayment'] = `Down payment cannot exceed the property price of ₹${price.toLocaleString()}.`;
    }

    if (this.annualInterestRate < 0 || this.annualInterestRate > 100) {
      this.affordabilityErrors['interestRate'] = 'Interest rate must be between 0% and 100%.';
    }

    if (this.tenureYears <= 0) {
      this.affordabilityErrors['tenure'] = 'Loan tenure must be at least 1 year.';
    } else if (this.tenureYears > 40) {
      this.affordabilityErrors['tenure'] = 'Loan tenure cannot exceed 40 years.';
    }

    if (this.monthlyIncome <= 0) {
      this.affordabilityErrors['income'] = 'Monthly income must be greater than zero.';
    }

    // Stop if validation errors exist
    if (Object.keys(this.affordabilityErrors).length > 0) {
      this.cdr.detectChanges();
      return;
    }

    // 2. Calculations
    const P = Math.max(0, price - this.downPayment);
    this.loanAmount = P;
    const n = Math.round(this.tenureYears * 12);

    if (P === 0) {
      // 100% Down Payment
      this.monthlyEMI = 0;
      this.totalInterest = 0;
      this.totalRepayment = 0;
      this.totalCost = this.downPayment;
      this.emiToIncomeRatio = 0;
      this.affordabilityStatus = 'Full Cash Purchase (100% Down Payment)';
      this.affordabilityClass = 'status-affordable';
      this.affordabilityIcon = '🎉';
      this.affordabilityAdvice = 'No home loan required! The down payment covers the entire property purchase price.';
    } else if (this.annualInterestRate === 0) {
      // Zero-Interest Loan Scheme
      this.monthlyEMI = Math.round(P / n);
      this.totalInterest = 0;
      this.totalRepayment = P;
      this.totalCost = this.downPayment + P;
      this.emiToIncomeRatio = Math.round((this.monthlyEMI / this.monthlyIncome) * 1000) / 10;
      this.evaluateRatio();
    } else {
      // Standard Home Loan EMI Formula: EMI = P * r * (1 + r)^n / ((1 + r)^n - 1)
      const r = (this.annualInterestRate / 12) / 100;
      const factor = Math.pow(1 + r, n);
      this.monthlyEMI = Math.round((P * r * factor) / (factor - 1));
      this.totalRepayment = Math.round(this.monthlyEMI * n);
      this.totalInterest = Math.max(0, this.totalRepayment - P);
      this.totalCost = this.downPayment + this.totalRepayment;
      this.emiToIncomeRatio = Math.round((this.monthlyEMI / this.monthlyIncome) * 1000) / 10;
      this.evaluateRatio();
    }

    this.cdr.detectChanges();
  }

  private evaluateRatio(): void {
    if (this.emiToIncomeRatio <= 30) {
      this.affordabilityStatus = 'Highly Affordable';
      this.affordabilityClass = 'status-affordable';
      this.affordabilityIcon = '✅';
      this.affordabilityAdvice = 'Outstanding! Your estimated EMI is under 30% of your income. Financial institutions consider this low-risk and optimal for approvals.';
    } else if (this.emiToIncomeRatio <= 40) {
      this.affordabilityStatus = 'Moderate & Manageable';
      this.affordabilityClass = 'status-moderate';
      this.affordabilityIcon = '👍';
      this.affordabilityAdvice = 'Manageable! Your EMI is between 30% and 40% of your monthly income. This is within standard banking lending thresholds.';
    } else if (this.emiToIncomeRatio <= 50) {
      this.affordabilityStatus = 'Stretched Budget';
      this.affordabilityClass = 'status-stretched';
      this.affordabilityIcon = '⚠️';
      this.affordabilityAdvice = 'Caution: EMI consumes 40% to 50% of your monthly income. You may want to increase your down payment or extend the loan tenure to reduce monthly pressure.';
    } else {
      this.affordabilityStatus = 'High Risk / Unaffordable';
      this.affordabilityClass = 'status-risky';
      this.affordabilityIcon = '🚨';
      this.affordabilityAdvice = 'High risk: EMI exceeds 50% of your monthly income. Lenders typically restrict debt-to-income above 50%. A higher down payment is strongly recommended.';
    }
  }

  openDeleteModal(): void {
    this.showDeleteModal = true;
    this.cdr.detectChanges();
  }

  cancelDelete(): void {
    this.showDeleteModal = false;
    this.cdr.detectChanges();
  }

  confirmDelete(): void {
    if (!this.propertyId) return;
    this.deleting = true;
    this.propertyService.deleteProperty(this.propertyId).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.deleting = false;
        this.successMessage = 'Property deleted successfully!';
        this.cdr.detectChanges();
        setTimeout(() => {
          this.router.navigate(['/properties']);
        }, 1200);
      },
      error: (err) => {
        console.error('Error deleting property:', err);
        this.errorMessage = 'Something went wrong. Please try again.';
        this.showDeleteModal = false;
        this.deleting = false;
        this.cdr.detectChanges();
      }
    });
  }
}
