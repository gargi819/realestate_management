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
}
