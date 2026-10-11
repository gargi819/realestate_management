package com.example.realestate.controller;

import com.example.realestate.entity.Property;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller exposing CRUD, search, and filter endpoints for Property management.
 */
@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class PropertyController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // 1. POST /api/properties - Create a new property (201 Created)
    @PostMapping
    public ResponseEntity<Property> addProperty(@RequestBody Property property) {
        Property createdProperty = propertyService.addProperty(property);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    // 2. GET /api/properties - Retrieve all properties (200 OK)
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    // 3. GET /api/properties/search?location=Mumbai - Search by location (200 OK)
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchPropertiesByLocation(@RequestParam String location) {
        List<Property> properties = propertyService.searchByLocation(location);
        return ResponseEntity.ok(properties);
    }

    // 4. GET /api/properties/filter?propertyType=Apartment - Filter by property type (200 OK)
    @GetMapping("/filter")
    public ResponseEntity<List<Property>> filterPropertiesByType(@RequestParam String propertyType) {
        List<Property> properties = propertyService.filterByPropertyType(propertyType);
        return ResponseEntity.ok(properties);
    }

    // 5. GET /api/properties/filter/status?status=Available - Filter by status (200 OK)
    @GetMapping("/filter/status")
    public ResponseEntity<List<Property>> filterPropertiesByStatus(@RequestParam String status) {
        List<Property> properties = propertyService.filterByStatus(status);
        return ResponseEntity.ok(properties);
    }

    // 6. GET /api/properties/{id} - Retrieve property by ID (200 OK or 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        Optional<Property> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 7. PUT /api/properties/{id} - Update property (200 OK or 404 Not Found)
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails) {
        Property updatedProperty = propertyService.updateProperty(id, propertyDetails);
        if (updatedProperty != null) {
            return ResponseEntity.ok(updatedProperty);
        }
        return ResponseEntity.notFound().build();
    }

    // 8. DELETE /api/properties/{id} - Delete property (204 No Content or 404 Not Found)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        boolean isDeleted = propertyService.deleteProperty(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
