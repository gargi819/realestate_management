package com.example.realestate.repository;

import com.example.realestate.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Property entity.
 * Provides out-of-the-box CRUD operations and custom query methods powered by Spring Data JPA.
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // 1. Search by location (case-insensitive partial or full match)
    List<Property> findByLocationContainingIgnoreCase(String location);

    // 2. Filter by property type (case-insensitive exact match)
    List<Property> findByPropertyTypeIgnoreCase(String propertyType);

    // 3. Filter by status (case-insensitive exact match)
    List<Property> findByStatusIgnoreCase(String status);
}
