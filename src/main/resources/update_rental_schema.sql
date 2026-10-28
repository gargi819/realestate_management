UPDATE property SET monthly_rent = price WHERE status = 'For Rent' AND (monthly_rent IS NULL OR monthly_rent = 0);
UPDATE property SET security_deposit = 50000 WHERE id = 9;
UPDATE property SET security_deposit = 80000 WHERE id = 7;
UPDATE property SET security_deposit = 75000 WHERE id = 4;
UPDATE property SET security_deposit = 60000 WHERE id = 5;
UPDATE property SET security_deposit = 150000 WHERE id = 6;

-- Add 2 realistic cheaper alternative properties in Thane for testing comparison
INSERT INTO property (title, description, price, monthly_rent, security_deposit, property_type, location, area, bedrooms, bathrooms, status, owner_id)
SELECT '1 BHK Budget Apartment in Vartak Nagar', 'Well-ventilated rental flat near Korum Mall, 24x7 water and elevator.', 16000, 16000, 40000, 'Apartment', 'Vartak Nagar, Thane', 600, 1, 1, 'For Rent', 4
WHERE NOT EXISTS (SELECT 1 FROM property WHERE title = '1 BHK Budget Apartment in Vartak Nagar');

INSERT INTO property (title, description, price, monthly_rent, security_deposit, property_type, location, area, bedrooms, bathrooms, status, owner_id)
SELECT 'Spacious 1 BHK in Panchpakhadi', 'Prime residential locality in Thane with balcony and parking.', 18000, 18000, 45000, 'Apartment', 'Panchpakhadi, Thane West', 700, 1, 1, 'For Rent', 4
WHERE NOT EXISTS (SELECT 1 FROM property WHERE title = 'Spacious 1 BHK in Panchpakhadi');

INSERT INTO property (title, description, price, monthly_rent, security_deposit, property_type, location, area, bedrooms, bathrooms, status, owner_id)
SELECT 'Modern Studio Flat in Ghodbunder Road', 'Compact, semi-furnished studio flat with modern amenities in Thane.', 15000, 15000, 35000, 'Apartment', 'Ghodbunder Road, Thane', 580, 1, 1, 'For Rent', 4
WHERE NOT EXISTS (SELECT 1 FROM property WHERE title = 'Modern Studio Flat in Ghodbunder Road');
