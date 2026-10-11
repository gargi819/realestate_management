package com.example.realestate.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Administrator features.
 * Exposes endpoints for managing users, inspecting individual profiles,
 * and viewing system metrics.
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

    // 1. GET /api/admin/stats - Overview metrics
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long totalProperties = propertyRepository.count();
        long totalRenters = users.stream().filter(u -> "TENANT".equalsIgnoreCase(u.getRole())).count();
        long totalBuyers = users.stream().filter(u -> "BUYER".equalsIgnoreCase(u.getRole())).count();
        long totalOwners = users.stream().filter(u -> "OWNER".equalsIgnoreCase(u.getRole())).count();

        AdminStatsDTO stats = new AdminStatsDTO(totalUsers, totalProperties, totalRenters, totalBuyers, totalOwners);
        return ResponseEntity.ok(stats);
    }

    // 2. GET /api/admin/users - List all registered users with summary counts
    @GetMapping("/users")
    public ResponseEntity<List<UserSummaryDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserSummaryDTO> summaries = new ArrayList<>();

        for (User u : users) {
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
}
