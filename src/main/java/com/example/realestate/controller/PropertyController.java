package com.example.realestate.controller;

import com.example.realestate.dto.RentalComparisonDTO;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller exposing CRUD, search, filter, and smart rental comparison endpoints.
 * Strictly enforces role-based authorization for creating, editing, and deleting listings.
 */
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class PropertyController {

    private final PropertyService propertyService;
    private final UserRepository userRepository;

    @Autowired
    public PropertyController(PropertyService propertyService, UserRepository userRepository) {
        this.propertyService = propertyService;
        this.userRepository = userRepository;
    }

    // 1. POST /api/properties - Create a new property (Protected: OWNER or ADMIN)
    @PostMapping
    public ResponseEntity<?> addProperty(
            @RequestHeader(value = "X-User-Id", required = false) Long callerId,
            @RequestBody Property property) {

        if (callerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required: Please sign in to list a property.");
        }

        Optional<User> callerOpt = userRepository.findById(callerId);
        if (callerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid user session. Please sign in again.");
        }

        User caller = callerOpt.get();
        if (!"OWNER".equalsIgnoreCase(caller.getRole()) && !"ADMIN".equalsIgnoreCase(caller.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Only Property Owners and Administrators can list properties.");
        }

        // Validate area
        if (property.getArea() == null || property.getArea() <= 0) {
            return ResponseEntity.badRequest().body("Validation failed: Area must be a positive number.");
        }

        // Validate rental amounts
        if ("For Rent".equalsIgnoreCase(property.getStatus())) {
            Double rent = property.getMonthlyRent() != null ? property.getMonthlyRent() : property.getPrice();
            if (rent == null || rent <= 0) {
                return ResponseEntity.badRequest().body("Validation failed: A valid positive monthly rent is required for rental listings.");
            }
            if (property.getSecurityDeposit() != null && property.getSecurityDeposit() < 0) {
                return ResponseEntity.badRequest().body("Validation failed: Security deposit cannot be negative.");
            }
            property.setMonthlyRent(rent);
            property.setPrice(rent);
            if (property.getSecurityDeposit() == null) {
                property.setSecurityDeposit(0.0);
            }
        } else {
            if (property.getPrice() == null || property.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Validation failed: A valid positive selling price is required.");
            }
        }

        // Bind owner ID
        if (!"ADMIN".equalsIgnoreCase(caller.getRole()) || property.getOwnerId() == null) {
            property.setOwnerId(caller.getId());
        }

        Property createdProperty = propertyService.addProperty(property);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    // 2. GET /api/properties - Retrieve all properties (Public)
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    // 3. GET /api/properties/search?location=Mumbai - Search by location
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchPropertiesByLocation(@RequestParam String location) {
        List<Property> properties = propertyService.searchByLocation(location);
        return ResponseEntity.ok(properties);
    }

    // 4. GET /api/properties/filter?propertyType=Apartment - Filter by property type
    @GetMapping("/filter")
    public ResponseEntity<List<Property>> filterPropertiesByType(@RequestParam String propertyType) {
        List<Property> properties = propertyService.filterByPropertyType(propertyType);
        return ResponseEntity.ok(properties);
    }

    // 5. GET /api/properties/filter/status?status=Available - Filter by status
    @GetMapping("/filter/status")
    public ResponseEntity<List<Property>> filterPropertiesByStatus(@RequestParam String status) {
        List<Property> properties = propertyService.filterByStatus(status);
        return ResponseEntity.ok(properties);
    }

    // 6. GET /api/properties/{id} - Retrieve property by ID
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 7. GET /api/properties/{id}/cheaper-rentals - Smart Rental Comparison Based on Location, Area, and Rent
    @GetMapping("/{id}/cheaper-rentals")
    public ResponseEntity<RentalComparisonDTO> getCheaperRentals(@PathVariable Long id) {
        RentalComparisonDTO comparison = propertyService.compareCheaperRentals(id);
        if (comparison != null) {
            return ResponseEntity.ok(comparison);
        }
        return ResponseEntity.notFound().build();
    }

    // 8. PUT /api/properties/{id} - Update property (Protected: Owner of property or ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long callerId,
            @RequestBody Property propertyDetails) {

        Optional<Property> existingOpt = propertyService.getPropertyById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Property existing = existingOpt.get();

        if (callerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required: Please sign in to edit this property.");
        }

        Optional<User> callerOpt = userRepository.findById(callerId);
        if (callerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid user session. Please sign in again.");
        }

        User caller = callerOpt.get();

        // Enforce role-based editing permissions
        if (!"ADMIN".equalsIgnoreCase(caller.getRole())) {
            if (!"OWNER".equalsIgnoreCase(caller.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: Tenants and Buyers are not permitted to edit properties.");
            }
            if (existing.getOwnerId() == null || !existing.getOwnerId().equals(caller.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: You are only authorized to edit properties that you own.");
            }
        }

        // Validate area
        if (propertyDetails.getArea() == null || propertyDetails.getArea() <= 0) {
            return ResponseEntity.badRequest().body("Validation failed: Area must be a positive number.");
        }

        // Validate rental amounts if status is For Rent
        if ("For Rent".equalsIgnoreCase(propertyDetails.getStatus())) {
            Double rent = propertyDetails.getMonthlyRent() != null ? propertyDetails.getMonthlyRent() : propertyDetails.getPrice();
            if (rent == null || rent <= 0) {
                return ResponseEntity.badRequest().body("Validation failed: A valid positive monthly rent is required.");
            }
            if (propertyDetails.getSecurityDeposit() != null && propertyDetails.getSecurityDeposit() < 0) {
                return ResponseEntity.badRequest().body("Validation failed: Security deposit cannot be negative.");
            }
            propertyDetails.setMonthlyRent(rent);
            propertyDetails.setPrice(rent);
            if (propertyDetails.getSecurityDeposit() == null) {
                propertyDetails.setSecurityDeposit(0.0);
            }
        } else {
            if (propertyDetails.getPrice() == null || propertyDetails.getPrice() <= 0) {
                return ResponseEntity.badRequest().body("Validation failed: A valid positive selling price is required.");
            }
        }

        Property updatedProperty = propertyService.updateProperty(id, propertyDetails);
        return ResponseEntity.ok(updatedProperty);
    }

    // 9. DELETE /api/properties/{id} - Delete property (Protected: Owner of property or ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long callerId) {

        Optional<Property> existingOpt = propertyService.getPropertyById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Property existing = existingOpt.get();

        if (callerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required.");
        }

        Optional<User> callerOpt = userRepository.findById(callerId);
        if (callerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid user session.");
        }

        User caller = callerOpt.get();
        if (!"ADMIN".equalsIgnoreCase(caller.getRole())) {
            if (!"OWNER".equalsIgnoreCase(caller.getRole()) || existing.getOwnerId() == null || !existing.getOwnerId().equals(caller.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: You can only delete properties that you own.");
            }
        }

        boolean isDeleted = propertyService.deleteProperty(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 10. GET /api/properties/owner/{ownerId} - Retrieve properties listed by an owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Property>> getPropertiesByOwner(@PathVariable Long ownerId) {
        List<Property> properties = propertyService.getPropertiesByOwnerId(ownerId);
        return ResponseEntity.ok(properties);
    }
}
