import { Property } from './property';

export interface User {
  id?: number;
  name: string;
  email: string;
  phone?: string;
  role?: string;
}

export interface RentalHistory {
  id?: number;
  tenant: User;
  property: Property;
  monthlyRent: number;
  startDate: string;
  endDate?: string;
}

export interface CheaperRentalDTO {
  property: Property;
  previousRent: number;
  currentRent: number;
  monthlySavings: number;
  percentageSavings: number;
}

export interface CheaperRentalResponse {
  tenantId: number;
  tenantName: string;
  previousRent: number;
  cheaperRentals: CheaperRentalDTO[];
  message: string;
}
