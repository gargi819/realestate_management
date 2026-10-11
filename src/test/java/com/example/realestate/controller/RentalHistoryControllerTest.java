package com.example.realestate.controller;

import com.example.realestate.dto.CheaperRentalDTO;
import com.example.realestate.dto.CheaperRentalResponse;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.RentalHistory;
import com.example.realestate.entity.User;
import com.example.realestate.service.RentalHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentalHistoryController.class)
public class RentalHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalHistoryService rentalHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleTenant;
    private Property sampleProperty;
    private RentalHistory sampleHistory;

    @BeforeEach
    public void setUp() {
        sampleTenant = new User("Rahul Sharma", "rahul@example.com", "9876543210", "TENANT");
        sampleTenant.setId(1L);

        sampleProperty = new Property("2 BHK Flat", "Desc", 40000.0, "Apartment", "Bengaluru", 1000.0, 2, 2, "For Rent");
        sampleProperty.setId(4L);

        sampleHistory = new RentalHistory(sampleTenant, sampleProperty, 50000.0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        sampleHistory.setId(1L);
    }

    @Test
    public void testGetTenantsReturns200OK() throws Exception {
        when(rentalHistoryService.getAllTenants()).thenReturn(Collections.singletonList(sampleTenant));

        mockMvc.perform(get("/api/rental-history/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rahul Sharma"));
    }

    @Test
    public void testGetRentalHistoryByTenantReturns200OK() throws Exception {
        when(rentalHistoryService.getRentalHistoryByTenantId(1L))
                .thenReturn(Collections.singletonList(sampleHistory));

        mockMvc.perform(get("/api/rental-history/tenant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].monthlyRent").value(50000.0));
    }

    @Test
    public void testGetCheaperRentalsReturns200OK() throws Exception {
        CheaperRentalDTO dto = new CheaperRentalDTO(sampleProperty, 50000.0, 40000.0, 10000.0, 20.0);
        CheaperRentalResponse response = new CheaperRentalResponse(1L, "Rahul Sharma", 50000.0,
                Collections.singletonList(dto), "Found 1 cheaper rental");

        when(rentalHistoryService.findCheaperRentalsForTenant(1L)).thenReturn(response);

        mockMvc.perform(get("/api/rental-history/tenant/1/cheaper-rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previousRent").value(50000.0))
                .andExpect(jsonPath("$.cheaperRentals[0].monthlySavings").value(10000.0));
    }

    @Test
    public void testAddRentalHistoryReturns201Created() throws Exception {
        when(rentalHistoryService.addRentalHistory(any(RentalHistory.class))).thenReturn(sampleHistory);

        mockMvc.perform(post("/api/rental-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleHistory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monthlyRent").value(50000.0));
    }
}
