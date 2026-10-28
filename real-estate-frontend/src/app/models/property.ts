export interface Property {
  id?: number;
  title: string;
  description: string;
  price: number;
  propertyType: string;
  location: string;
  area: number;
  bedrooms: number;
  bathrooms: number;
  status: string;
  ownerId?: number;
  monthlyRent?: number;
  securityDeposit?: number;
}

export interface RentalAlternative {
  id: number;
  title: string;
  description: string;
  location: string;
  propertyType: string;
  monthlyRent: number;
  securityDeposit: number;
  area: number;
  bedrooms: number;
  bathrooms: number;
  status: string;
  monthlySavings: number;
  areaDifference: number;
  depositDifference: number;
  savingsPercentage: number;
  sameLocality: boolean;
  sameCity: boolean;
  cheaper: boolean;
}

export interface RentalComparisonResponse {
  selectedPropertyId: number;
  selectedTitle: string;
  selectedLocation: string;
  selectedPropertyType: string;
  selectedRent: number;
  selectedDeposit: number;
  selectedArea: number;
  selectedBedrooms: number;
  selectedBathrooms: number;
  locality: string;
  city: string;
  cheaperAlternatives: RentalAlternative[];
  sameCityAlternatives: RentalAlternative[];
  hasCheaperInSameLocality: boolean;
  hasCheaperInCity: boolean;
  summaryMessage: string;
}
