package com.example.realestate.service;

import com.example.realestate.entity.Property;
import com.example.realestate.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for Property operations.
 */
@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    // 1. Add a new property
    public Property addProperty(Property property) {
        return propertyRepository.save(property);
    }

    // 2. Retrieve all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // 3. Retrieve a single property by its ID
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    // 4. Update an existing property
    public Property updateProperty(Long id, Property propertyDetails) {
        Optional<Property> optionalProperty = propertyRepository.findById(id);
        if (optionalProperty.isPresent()) {
            Property existingProperty = optionalProperty.get();
            existingProperty.setTitle(propertyDetails.getTitle());
            existingProperty.setDescription(propertyDetails.getDescription());
            existingProperty.setPrice(propertyDetails.getPrice());
            existingProperty.setPropertyType(propertyDetails.getPropertyType());
            existingProperty.setLocation(propertyDetails.getLocation());
            existingProperty.setArea(propertyDetails.getArea());
            existingProperty.setBedrooms(propertyDetails.getBedrooms());
            existingProperty.setBathrooms(propertyDetails.getBathrooms());
            existingProperty.setStatus(propertyDetails.getStatus());
            return propertyRepository.save(existingProperty);
        }
        return null;
    }

    // 5. Delete a property by its ID
    public boolean deleteProperty(Long id) {
        if (propertyRepository.existsById(id)) {
            propertyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 6. Search properties by location (case-insensitive)
    public List<Property> searchByLocation(String location) {
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    // 7. Filter properties by property type (case-insensitive)
    public List<Property> filterByPropertyType(String propertyType) {
        return propertyRepository.findByPropertyTypeIgnoreCase(propertyType);
    }

    // 8. Filter properties by status (case-insensitive)
    public List<Property> filterByStatus(String status) {
        return propertyRepository.findByStatusIgnoreCase(status);
    }
}
