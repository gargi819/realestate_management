package com.example.realestate.repository;

import com.example.realestate.entity.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RentalHistory entity.
 */
@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Long> {

    // Retrieve all rental records for a tenant ordered by latest start date first
    List<RentalHistory> findByTenantIdOrderByStartDateDesc(Long tenantId);

    // Retrieve the most recent rental record for a tenant
    Optional<RentalHistory> findFirstByTenantIdOrderByStartDateDesc(Long tenantId);

    // Retrieve rental records associated with a specific property
    List<RentalHistory> findByPropertyId(Long propertyId);
}
