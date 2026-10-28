package com.example.realestate.controller;

import com.example.realestate.dto.AdminPropertyDTO;
import com.example.realestate.dto.AdminStatsDTO;
import com.example.realestate.dto.AdminUserDetailsDTO;
import com.example.realestate.dto.UserSummaryDTO;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.RentalHistory;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.RentalHistoryRepository;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Administrator portal.
 * Exposes endpoints for managing users (excluding admin from directories),
 * managing all properties across owners, inspecting rental activity across all users,
 * and monitoring accurate platform metrics.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class AdminController {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    @Autowired
    public AdminController(UserRepository userRepository,
                           PropertyRepository propertyRepository,
                           RentalHistoryRepository rentalHistoryRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.rentalHistoryRepository = rentalHistoryRepository;
    }

    // 1. GET /api/admin/stats - Overview metrics (strictly excludes ADMIN from user counts)
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        List<User> regularUsers = userRepository.findAll().stream()
                .filter(u -> !"ADMIN".equalsIgnoreCase(u.getRole()))
                .toList();

        long totalUsers = regularUsers.size();
        long totalProperties = propertyRepository.count();
        long totalRenters = regularUsers.stream().filter(u -> "TENANT".equalsIgnoreCase(u.getRole())).count();
        long totalBuyers = regularUsers.stream().filter(u -> "BUYER".equalsIgnoreCase(u.getRole())).count();
        long totalOwners = regularUsers.stream().filter(u -> "OWNER".equalsIgnoreCase(u.getRole())).count();
        long totalRentalContracts = rentalHistoryRepository.count();

        AdminStatsDTO stats = new AdminStatsDTO(
                totalUsers,
                totalProperties,
                totalRenters,
                totalBuyers,
                totalOwners,
                totalRentalContracts
        );
        return ResponseEntity.ok(stats);
    }

    // 2. GET /api/admin/users - List all registered regular users (strictly excludes ADMIN accounts)
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        List<User> regularUsers = userRepository.findAll().stream()
                .filter(u -> !"ADMIN".equalsIgnoreCase(u.getRole()))
                .toList();

        List<UserSummaryDTO> summaries = new ArrayList<>();

        for (User u : regularUsers) {
            int propCount = propertyRepository.findByOwnerId(u.getId()).size();
            int rentalCount = rentalHistoryRepository.findByTenantIdOrderByStartDateDesc(u.getId()).size();

            summaries.add(new UserSummaryDTO(
                    u.getId(),
                    u.getName(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getRole(),
                    propCount,
                    rentalCount
            ));
        }

        return ResponseEntity.ok(summaries);
    }

    // 3. GET /api/admin/users/{userId} - Detailed view of an individual user
    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserDetailsDTO> getUserDetails(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User u = userOpt.get();
        AdminUserDetailsDTO details = new AdminUserDetailsDTO(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getRole()
        );

        // Fetch properties owned by this user
        List<Property> userProperties = propertyRepository.findByOwnerId(u.getId());
        details.setProperties(userProperties);

        // Fetch rental history records if user is a tenant/renter
        List<RentalHistory> history = rentalHistoryRepository.findByTenantIdOrderByStartDateDesc(u.getId());
        details.setRentalHistory(history);

        return ResponseEntity.ok(details);
    }

    // 4. GET /api/admin/properties - Complete overview of all properties with owner details
    @GetMapping("/properties")
    public ResponseEntity<List<AdminPropertyDTO>> getAllPropertiesForAdmin() {
        List<Property> properties = propertyRepository.findAll();
        List<AdminPropertyDTO> result = new ArrayList<>();

        for (Property p : properties) {
            AdminPropertyDTO dto = new AdminPropertyDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setPropertyType(p.getPropertyType());
            dto.setLocation(p.getLocation());
            dto.setArea(p.getArea());
            dto.setBedrooms(p.getBedrooms());
            dto.setBathrooms(p.getBathrooms());
            dto.setStatus(p.getStatus());
            dto.setMonthlyRent(p.getMonthlyRent());
            dto.setSecurityDeposit(p.getSecurityDeposit());
            dto.setOwnerId(p.getOwnerId());

            if (p.getOwnerId() != null) {
                Optional<User> ownerOpt = userRepository.findById(p.getOwnerId());
                if (ownerOpt.isPresent()) {
                    User owner = ownerOpt.get();
                    dto.setOwnerName(owner.getName());
                    dto.setOwnerEmail(owner.getEmail());
                    dto.setOwnerPhone(owner.getPhone());
                } else {
                    dto.setOwnerName("Owner #" + p.getOwnerId());
                }
            } else {
                dto.setOwnerName("Platform / Unassigned");
            }

            result.add(dto);
        }

        return ResponseEntity.ok(result);
    }

    // 5. PUT /api/admin/properties/{propertyId}/status - Fast status change by Admin
    @PutMapping("/properties/{propertyId}/status")
    public ResponseEntity<Property> updatePropertyStatus(
            @PathVariable Long propertyId,
            @RequestBody Map<String, String> statusBody) {
        String newStatus = statusBody.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Property> propOpt = propertyRepository.findById(propertyId);
        if (propOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Property prop = propOpt.get();
        prop.setStatus(newStatus.trim());
        Property updated = propertyRepository.save(prop);
        return ResponseEntity.ok(updated);
    }

    // 6. DELETE /api/admin/properties/{propertyId} - Delete listing with safe cascade of rental records
    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<Void> deletePropertyByAdmin(@PathVariable Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            return ResponseEntity.notFound().build();
        }

        // Clean up any rental history foreign keys safely before deleting
        List<RentalHistory> history = rentalHistoryRepository.findByPropertyId(propertyId);
        if (history != null && !history.isEmpty()) {
            rentalHistoryRepository.deleteAll(history);
        }

        propertyRepository.deleteById(propertyId);
        return ResponseEntity.noContent().build();
    }

    // 7. GET /api/admin/rental-history - Review all rental contracts across all tenants
    @GetMapping("/rental-history")
    public ResponseEntity<List<RentalHistory>> getAllRentalContracts() {
        List<RentalHistory> contracts = rentalHistoryRepository.findAll();
        return ResponseEntity.ok(contracts);
    }
}
