import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { PropertyListComponent } from './components/property-list/property-list.component';
import { PropertyDetailsComponent } from './components/property-details/property-details.component';
import { PropertyFormComponent } from './components/property-form/property-form.component';
import { RentalHistoryComponent } from './components/rental-history/rental-history.component';
import { LoginComponent } from './components/login/login.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'properties', component: PropertyListComponent },
  { path: 'properties/:id', component: PropertyDetailsComponent },
  { path: 'rental-history', component: RentalHistoryComponent },
  { path: 'add-property', component: PropertyFormComponent },
  { path: 'edit-property/:id', component: PropertyFormComponent },
  { path: '**', redirectTo: '' }
];
