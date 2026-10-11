-- ====================================================================
-- Database Migration: User & Rental History Schema
-- Safe migration: Uses CREATE TABLE IF NOT EXISTS & INSERT IGNORE
-- ====================================================================

-- 1. Users Table (Tenants and Property Owners)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL DEFAULT 'password123',
    phone VARCHAR(50),
    role VARCHAR(50) DEFAULT 'TENANT'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Rental History Table
CREATE TABLE IF NOT EXISTS rental_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    property_id BIGINT NOT NULL,
    monthly_rent DOUBLE NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    CONSTRAINT fk_rental_tenant FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_rental_property FOREIGN KEY (property_id) REFERENCES property(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Initial Tenants Seed Data
INSERT IGNORE INTO users (id, name, email, password, phone, role) VALUES
(1, 'Rahul Sharma', 'rahul.sharma@example.com', 'password123', '+91 98765 43210', 'TENANT'),
(2, 'Priya Patel', 'priya.patel@example.com', 'password123', '+91 98123 45678', 'TENANT');

-- 4. Initial Sample Rental Properties (if not present)
INSERT IGNORE INTO property (id, title, description, price, property_type, location, area, bedrooms, bathrooms, status) VALUES
(4, 'Spacious 2BHK Furnished Flat', 'Well-ventilated 2BHK furnished apartment with 24/7 security, lift, and covered parking.', 38000.0, 'Apartment', 'Koramangala, Bengaluru', 1100.0, 2, 2, 'For Rent'),
(5, 'Cozy 1BHK Studio Apartment', 'Compact and modern studio apartment close to transit and cafes, ideal for working professionals.', 28000.0, 'Apartment', 'Bandra West, Mumbai', 550.0, 1, 1, 'For Rent'),
(6, '3BHK Luxury High-Rise Condo', 'Scenic lakeside view with access to club house, swimming pool, and community gym.', 65000.0, 'Apartment', 'Hiranandani, Powai', 1600.0, 3, 3, 'For Rent');

-- 5. Initial Rental History Records
INSERT IGNORE INTO rental_history (id, tenant_id, property_id, monthly_rent, start_date, end_date) VALUES
(1, 1, 6, 55000.0, '2024-01-01', '2024-12-31'),
(2, 1, 4, 50000.0, '2025-01-01', '2025-12-31'),
(3, 2, 5, 32000.0, '2024-06-01', '2025-05-31');
