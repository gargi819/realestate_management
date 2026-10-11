package com.example.realestate.service;

import com.example.realestate.dto.CheaperRentalDTO;
import com.example.realestate.dto.CheaperRentalResponse;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.RentalHistory;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.RentalHistoryRepository;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for Rental History and Smart Rent Tracking.
 */
@Service
public class RentalHistoryService {

    private final RentalHistoryRepository rentalHistoryRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Autowired
    public RentalHistoryService(RentalHistoryRepository rentalHistoryRepository,
                                PropertyRepository propertyRepository,
                                UserRepository userRepository) {
        this.rentalHistoryRepository = rentalHistoryRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    // 1. Retrieve all rental records for a specific tenant (ordered latest first)
    public List<RentalHistory> getRentalHistoryByTenantId(Long tenantId) {
        return rentalHistoryRepository.findByTenantIdOrderByStartDateDesc(tenantId);
    }

    // 2. Retrieve the most recent rental record for a tenant
    public Optional<RentalHistory> getMostRecentRental(Long tenantId) {
        return rentalHistoryRepository.findFirstByTenantIdOrderByStartDateDesc(tenantId);
    }

    // 3. Find available rentals strictly cheaper than the tenant's most recent rent
    public CheaperRentalResponse findCheaperRentalsForTenant(Long tenantId) {
        Optional<User> tenantOpt = userRepository.findById(tenantId);
        String tenantName = tenantOpt.map(User::getName).orElse("Tenant #" + tenantId);

        Optional<RentalHistory> mostRecentOpt = getMostRecentRental(tenantId);

        if (mostRecentOpt.isEmpty()) {
            return new CheaperRentalResponse(
                    tenantId,
                    tenantName,
                    0.0,
                    new ArrayList<>(),
                    "No rental history found for this tenant. Please record a rental to find cheaper alternatives."
            );
        }

        RentalHistory mostRecent = mostRecentOpt.get();
        Double previousRent = mostRecent.getMonthlyRent();

        // Retrieve all properties
        List<Property> allProperties = propertyRepository.findAll();
        List<CheaperRentalDTO> cheaperList = new ArrayList<>();

        for (Property p : allProperties) {
            // Must have price strictly lower than previous rent
            if (p.getPrice() == null || p.getPrice() >= previousRent || p.getPrice() <= 0) {
                continue;
            }

            // Must be currently available or for rent (not sold)
            String status = p.getStatus() != null ? p.getStatus().trim().toLowerCase() : "";
            if (status.equals("sold")) {
                continue;
            }

            // Exclude if it's the exact property currently rented in this record
            if (mostRecent.getProperty() != null && mostRecent.getProperty().getId() != null
                    && mostRecent.getProperty().getId().equals(p.getId())) {
                continue;
            }

            double currentRent = p.getPrice();
            double monthlySavings = Math.round((previousRent - currentRent) * 100.0) / 100.0;
            double percentageSavings = Math.round(((previousRent - currentRent) / previousRent * 100.0) * 10.0) / 10.0;

            cheaperList.add(new CheaperRentalDTO(p, previousRent, currentRent, monthlySavings, percentageSavings));
        }

        // Sort recommended properties strictly by lowest rent first
        cheaperList.sort(Comparator.comparingDouble(CheaperRentalDTO::getCurrentRent));

        String message;
        if (cheaperList.isEmpty()) {
            message = String.format(
                    "Great news! Your recent rent of ₹%,.0f is already lower than or equal to all currently available rentals.",
                    previousRent
            );
        } else {
            message = String.format(
                    "Found %d available rental property(ies) cheaper than your previous rent of ₹%,.0f.",
                    cheaperList.size(),
                    previousRent
            );
        }

        return new CheaperRentalResponse(tenantId, tenantName, previousRent, cheaperList, message);
    }

    // 4. Record a new rental history entry
    public RentalHistory addRentalHistory(RentalHistory rentalHistory) {
        if (rentalHistory.getMonthlyRent() == null || rentalHistory.getMonthlyRent() <= 0) {
            throw new IllegalArgumentException("Monthly rent must be greater than zero.");
        }
        if (rentalHistory.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        return rentalHistoryRepository.save(rentalHistory);
    }

    // 5. Retrieve all registered tenants
    public List<User> getAllTenants() {
        return userRepository.findAll();
    }

    // 6. Retrieve a specific tenant
    public Optional<User> getTenantById(Long tenantId) {
        return userRepository.findById(tenantId);
    }
}
