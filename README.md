# Real Estate Management System

A beginner-friendly, production-ready **Java Full Stack** web application designed for managing and discovering real estate properties. Built using modern **Spring Boot** on the backend and **Angular** on the frontend, with **MySQL** for relational persistence.

---

## 📖 Description

The **Real Estate Management System** provides a complete end-to-end platform for exploring, listing, and managing properties. The application enables users to:
* **Add new properties** with detailed specifications and real-time form validation.
* **Browse available properties** in a responsive, modern card layout.
* **Inspect comprehensive property details** (price, specs, area, description).
* **Update existing property listings** through pre-filled reactive forms.
* **Delete property listings** with user-friendly confirmation dialogs.
* **Search properties by location** using case-insensitive database matching.
* **Filter properties** by property type (Apartment, House, Villa, Plot, Commercial) and status (Available, Sold, For Rent).

---

## 🛠️ Technology Stack

### Backend
* **Language:** Java 17+
* **Framework:** Spring Boot 3.3.3
* **Web:** Spring Web (RESTful APIs, MVC, Tomcat)
* **ORM & Database Access:** Spring Data JPA, Hibernate 6
* **Database:** MySQL 8+
* **Build Tool:** Maven

### Frontend
* **Framework:** Angular 21 (Standalone Components)
* **Language:** TypeScript 5.9+
* **Styling:** CSS3 (Custom Responsive Grid, CSS Variables, Mobile-First)
* **Forms:** Angular Reactive Forms (`FormGroup`, `FormControl`, `Validators`)
* **HTTP:** Angular `HttpClient` (`provideHttpClient`)
* **Routing:** Angular Router (`provideRouter`, `RouterLink`, `RouterLinkActive`)

---

## 🏗️ Architecture

The application implements a clean **N-Tier Layered Architecture** ensuring complete separation of concerns:

```text
┌─────────────────────────────────────────────────────────────┐
│                      Angular Frontend                       │
│  [Home]  [Property List]  [Property Details]  [Property Form]│
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP Client (JSON)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│               Spring Boot REST Controllers                  │
│       (PropertyController.java, TestController.java)         │
└──────────────────────────────┬──────────────────────────────┘
                               │ Java Method Calls
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                       Service Layer                         │
│                   (PropertyService.java)                    │
└──────────────────────────────┬──────────────────────────────┘
                               │ Spring Data JPA
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                      Repository Layer                       │
│                  (PropertyRepository.java)                  │
└──────────────────────────────┬──────────────────────────────┘
                               │ Hibernate / JDBC (SQL)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   MySQL Relational Database                 │
│                 (real_estate_db -> property)                │
└─────────────────────────────────────────────────────────────┘
```

---

## ✨ Features

* **Full CRUD Operations:** Create, Read, Update, and Delete properties smoothly.
* **Spring Boot REST Architecture:** Proper HTTP status codes (`201 Created`, `200 OK`, `204 No Content`, `404 Not Found`).
* **Instant Search & Filtering:** Case-insensitive search by city/location and instant filtering by property type and status directly in the database.
* **Reactive Forms with Validation:** Client-side validation for required fields, positive prices, and non-negative counts.
* **Feature 1 - Property Affordability Checker:**
  * Interactive EMI calculator accessible directly from any property details view.
  * Automatically uses the selected property's actual listing price as the benchmark.
  * User inputs: Down Payment, Annual Interest Rate, Loan Tenure (years), and Monthly Income.
  * Calculates Principal Loan Amount, Monthly EMI, Total Interest Payable, Total Outflow, and EMI-to-Income Debt Ratio.
  * Implements the standard banking EMI formula: `EMI = P * r * (1+r)^n / ((1+r)^n - 1)`, with zero-interest handling (`EMI = P / n`).
  * Real-time visual affordability assessment badge & progress meter (Safe <30%, Manageable 30-40%, Stretched 40-50%, High Risk >50%).
* **Feature 2 - Smart Rent Tracker:**
  * Tracks historical rental contracts across tenants and properties (`rental_history` table).
  * Tenant session switcher / profile manager allowing instant switching between tenants.
  * "Find Cheaper Rentals" engine compares the tenant's latest benchmark rent against currently available market rentals.
  * Strictly displays only properties with monthly rent lower than previous rent, sorted lowest rent first.
  * Displays previous rent, new rent, monthly savings (₹), and percentage savings (%) on each recommendation card.
  * Full search and filtering support (by location and property type) on recommended rentals.
* **Responsive Multi-Column Design:** Desktop, Tablet, and Mobile-first UI.
* **Interactive UI Feedback:** Loading spinners, empty search states, alert banners, and custom delete confirmation modals.
* **CORS Configured:** Seamless communication between Angular (`http://localhost:4200`) and Spring Boot (`http://localhost:8080`).

---

## 📁 Project Structure

```text
Real Estate/
├── pom.xml                                  # Backend Maven configuration
├── .gitignore                               # Git ignore configuration
├── README.md                                # Project documentation
│
├── src/                                     # Spring Boot Backend Source
│   ├── main/
│   │   ├── java/com/example/realestate/
│   │   │   ├── RealEstateApplication.java   # Spring Boot Main Application
│   │   │   ├── controller/
│   │   │   │   ├── PropertyController.java  # Property REST Endpoints
│   │   │   │   └── TestController.java      # Healthcheck REST Endpoint
│   │   │   ├── entity/
│   │   │   │   └── Property.java            # JPA Property Entity
│   │   │   ├── repository/
│   │   │   │   └── PropertyRepository.java  # Spring Data JPA Repository
│   │   │   └── service/
│   │   │       └── PropertyService.java     # Business Logic Service
│   │   └── resources/
│   │       └── application.properties       # Database & Server settings
│   └── test/                                # JUnit 5 & MockMvc Unit Tests
│
└── real-estate-frontend/                    # Angular Frontend Source
    ├── package.json                         # Node dependencies & scripts
    ├── angular.json                         # Angular CLI configuration
    └── src/
        ├── styles.css                       # Global styles & CSS variables
        └── app/
            ├── app.ts                       # Root Component & Layout
            ├── app.html                     # Responsive Navbar & Footer
            ├── app.css                      # Navigation styling
            ├── app.config.ts                # Angular Providers (HttpClient, Router)
            ├── app.routes.ts                # Route Definitions
            ├── models/
            │   └── property.ts              # TypeScript Property Model
            ├── services/
            │   └── property.service.ts      # HTTP Service for Spring Boot API
            └── components/
                ├── home/                    # Hero Section & Quick Links
                ├── property-list/           # Card Grid, Search & Filters
                ├── property-details/        # Detailed View & Specifications
                └── property-form/           # Reusable Add/Edit Reactive Form
```

---

## 🗄️ Database Setup & Migration

1. Make sure **MySQL Server** is running on your system.
2. Open MySQL CLI or MySQL Workbench:
   ```sql
   CREATE DATABASE IF NOT EXISTS real_estate_db;
   ```
3. Run the Safe Migration Script for **Users & Rental History**:
   ```bash
   mysql -u root real_estate_db < src/main/resources/schema-rental-history.sql
   ```
   *(Hibernate automatically updates the tables, and the migration script safely establishes the `users` and `rental_history` foreign keys and sample seed data).*
4. Open `src/main/resources/application.properties` and verify your credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:real_estate_db}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

---

## 🚀 How to Run the Backend

Navigate to the project root directory:

```bash
# Build the backend
mvn clean package

# Run the Spring Boot application
mvn spring-boot:run
```

* Backend server will start at: **`http://localhost:8080`**
* Test health endpoint: **`http://localhost:8080/api/test`**

---

## 🌐 How to Run the Frontend

In a separate terminal window, navigate to the frontend directory:

```bash
cd real-estate-frontend

# Install dependencies
npm install

# Start the Angular development server
npm start
```

* Open your browser and visit: **`http://localhost:4200`**

---

## 📡 REST API Endpoints

### Properties Endpoints
| HTTP Method | Endpoint | Description | Request Body | Response Status |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/test` | Healthcheck verification | None | `200 OK` |
| **GET** | `/api/properties` | Retrieve all properties | None | `200 OK` |
| **GET** | `/api/properties/{id}` | Retrieve property by ID | None | `200 OK` / `404 Not Found` |
| **POST** | `/api/properties` | Create a new property | Property JSON | `201 Created` |
| **PUT** | `/api/properties/{id}` | Update an existing property | Property JSON | `200 OK` / `404 Not Found` |
| **DELETE** | `/api/properties/{id}` | Delete a property by ID | None | `204 No Content` / `404 Not Found` |
| **GET** | `/api/properties/search` | Search by location (`?location=...`) | None | `200 OK` |
| **GET** | `/api/properties/filter` | Filter by type (`?propertyType=...`) | None | `200 OK` |
| **GET** | `/api/properties/filter/status` | Filter by status (`?status=...`) | None | `200 OK` |

### Smart Rental History Endpoints (New Feature)
| HTTP Method | Endpoint | Description | Request Body | Response Status |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/rental-history/tenants` | Retrieve all registered tenants | None | `200 OK` |
| **GET** | `/api/rental-history/tenant/{id}` | Retrieve rental records for tenant | None | `200 OK` |
| **GET** | `/api/rental-history/tenant/{id}/cheaper-rentals` | Calculate cheaper rental recommendations | None | `200 OK` |
| **POST** | `/api/rental-history` | Record new rental contract | RentalHistory JSON | `201 Created` |

### Authentication & User Account Endpoints (Database-Linked)
| HTTP Method | Endpoint | Description | Request Body | Response Status |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Authenticate user with email and password | `{"email": "...", "password": "..."}` | `200 OK` / `401 Unauthorized` |
| **POST** | `/api/auth/register` | Register a new user in database | `{"name": "...", "email": "...", "password": "...", "role": "..."}` | `200 OK` / `409 Conflict` |
| **GET** | `/api/auth/me/{id}` | Retrieve user profile by user ID | None | `200 OK` / `404 Not Found` |

#### Seeded Demo Accounts (Pre-configured in MySQL)
* **Tenant 1:** `rahul.sharma@example.com` | Password: `password123`
* **Tenant 2:** `priya.patel@example.com` | Password: `password123`

### Sample Property JSON Payload
```json
{
  "title": "2 BHK Apartment",
  "description": "Spacious apartment near the city center",
  "price": 7500000,
  "propertyType": "Apartment",
  "location": "Navi Mumbai",
  "area": 950,
  "bedrooms": 2,
  "bathrooms": 2,
  "status": "Available"
}
```

---

## 🔮 Future Improvements

* **User Authentication & Authorization:** JWT-based login with Buyer, Seller, and Admin roles.
* **Property Image Uploads:** Multi-image gallery with cloud storage integration (AWS S3).
* **User Favorites & Wishlist:** Bookmark saved properties.
* **Agent / Owner Management:** Direct contact inquiry forms and owner profiles.
* **Property Booking & Payments:** Schedule property visits and handle online booking deposits.

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
