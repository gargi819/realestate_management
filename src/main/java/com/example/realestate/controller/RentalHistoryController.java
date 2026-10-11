package com.example.realestate.controller;

import com.example.realestate.dto.CheaperRentalResponse;
import com.example.realestate.entity.RentalHistory;
import com.example.realestate.entity.User;
import com.example.realestate.service.RentalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing endpoints for Rental History and Smart Rent Tracking.
 */
@RestController
@RequestMapping("/api/rental-history")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class RentalHistoryController {

    private final RentalHistoryService rentalHistoryService;

    @Autowired
    public RentalHistoryController(RentalHistoryService rentalHistoryService) {
        this.rentalHistoryService = rentalHistoryService;
    }

    // 1. GET /api/rental-history/tenants - Retrieve all tenants
    @GetMapping("/tenants")
    public ResponseEntity<List<User>> getAllTenants() {
        List<User> tenants = rentalHistoryService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }

    // 2. GET /api/rental-history/tenant/{tenantId} - Retrieve rental history of a tenant
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<RentalHistory>> getRentalHistoryByTenant(@PathVariable Long tenantId) {
        List<RentalHistory> history = rentalHistoryService.getRentalHistoryByTenantId(tenantId);
        return ResponseEntity.ok(history);
    }

    // 3. GET /api/rental-history/tenant/{tenantId}/cheaper-rentals - Compare & recommend cheaper rentals
    @GetMapping("/tenant/{tenantId}/cheaper-rentals")
    public ResponseEntity<CheaperRentalResponse> getCheaperRentals(@PathVariable Long tenantId) {
        CheaperRentalResponse response = rentalHistoryService.findCheaperRentalsForTenant(tenantId);
        return ResponseEntity.ok(response);
    }

    // 4. POST /api/rental-history - Record new rental history entry
    @PostMapping
    public ResponseEntity<RentalHistory> addRentalHistory(@RequestBody RentalHistory rentalHistory) {
        try {
            RentalHistory created = rentalHistoryService.addRentalHistory(rentalHistory);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
