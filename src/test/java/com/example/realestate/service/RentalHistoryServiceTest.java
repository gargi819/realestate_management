package com.example.realestate.service;

import com.example.realestate.dto.CheaperRentalDTO;
import com.example.realestate.dto.CheaperRentalResponse;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.RentalHistory;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.RentalHistoryRepository;
import com.example.realestate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentalHistoryServiceTest {

    @Mock
    private RentalHistoryRepository rentalHistoryRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentalHistoryService rentalHistoryService;

    private User sampleTenant;
    private Property propertyHigh;
    private Property propertyMid;
    private Property propertyLow;
    private RentalHistory sampleHistory;

    @BeforeEach
    public void setUp() {
        sampleTenant = new User("Rahul Sharma", "rahul@example.com", "9876543210", "TENANT");
        sampleTenant.setId(1L);

        propertyHigh = new Property("High-Rise Flat", "Desc", 65000.0, "Apartment", "Mumbai", 1200.0, 3, 3, "For Rent");
        propertyHigh.setId(6L);

        propertyMid = new Property("Mid Flat", "Desc", 38000.0, "Apartment", "Bengaluru", 1000.0, 2, 2, "For Rent");
        propertyMid.setId(4L);

        propertyLow = new Property("Studio Flat", "Desc", 28000.0, "Apartment", "Mumbai", 500.0, 1, 1, "For Rent");
        propertyLow.setId(5L);

        sampleHistory = new RentalHistory(sampleTenant, propertyHigh, 50000.0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        sampleHistory.setId(10L);
    }

    @Test
    public void testGetRentalHistoryByTenantId() {
        when(rentalHistoryRepository.findByTenantIdOrderByStartDateDesc(1L))
                .thenReturn(Collections.singletonList(sampleHistory));

        List<RentalHistory> history = rentalHistoryService.getRentalHistoryByTenantId(1L);

        assertEquals(1, history.size());
        assertEquals(50000.0, history.get(0).getMonthlyRent());
        verify(rentalHistoryRepository).findByTenantIdOrderByStartDateDesc(1L);
    }

    @Test
    public void testFindCheaperRentalsForTenant_WhenCheaperPropertiesExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTenant));
        when(rentalHistoryRepository.findFirstByTenantIdOrderByStartDateDesc(1L)).thenReturn(Optional.of(sampleHistory));
        // Return 3 properties: 65,000 (higher), 38,000 (cheaper), 28,000 (cheapest)
        when(propertyRepository.findAll()).thenReturn(Arrays.asList(propertyHigh, propertyMid, propertyLow));

        CheaperRentalResponse response = rentalHistoryService.findCheaperRentalsForTenant(1L);

        assertNotNull(response);
        assertEquals(50000.0, response.getPreviousRent());
        assertEquals(2, response.getCheaperRentals().size());

        // Check sorted by lowest rent first
        CheaperRentalDTO first = response.getCheaperRentals().get(0);
        assertEquals(28000.0, first.getCurrentRent());
        assertEquals(22000.0, first.getMonthlySavings());
        assertEquals(44.0, first.getPercentageSavings());

        CheaperRentalDTO second = response.getCheaperRentals().get(1);
        assertEquals(38000.0, second.getCurrentRent());
        assertEquals(12000.0, second.getMonthlySavings());
        assertEquals(24.0, second.getPercentageSavings());
    }

    @Test
    public void testFindCheaperRentalsForTenant_WhenNoCheaperProperties() {
        sampleHistory.setMonthlyRent(20000.0); // Very low previous rent
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTenant));
        when(rentalHistoryRepository.findFirstByTenantIdOrderByStartDateDesc(1L)).thenReturn(Optional.of(sampleHistory));
        when(propertyRepository.findAll()).thenReturn(Arrays.asList(propertyHigh, propertyMid, propertyLow));

        CheaperRentalResponse response = rentalHistoryService.findCheaperRentalsForTenant(1L);

        assertNotNull(response);
        assertTrue(response.getCheaperRentals().isEmpty());
        assertTrue(response.getMessage().contains("already lower than or equal"));
    }

    @Test
    public void testFindCheaperRentalsForTenant_WhenNoRentalHistory() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTenant));
        when(rentalHistoryRepository.findFirstByTenantIdOrderByStartDateDesc(1L)).thenReturn(Optional.empty());

        CheaperRentalResponse response = rentalHistoryService.findCheaperRentalsForTenant(1L);

        assertNotNull(response);
        assertTrue(response.getCheaperRentals().isEmpty());
        assertTrue(response.getMessage().contains("No rental history found"));
    }

    @Test
    public void testAddRentalHistory_Success() {
        when(rentalHistoryRepository.save(any(RentalHistory.class))).thenReturn(sampleHistory);

        RentalHistory created = rentalHistoryService.addRentalHistory(sampleHistory);

        assertNotNull(created);
        assertEquals(50000.0, created.getMonthlyRent());
    }

    @Test
    public void testAddRentalHistory_InvalidMonthlyRent() {
        sampleHistory.setMonthlyRent(-500.0);

        assertThrows(IllegalArgumentException.class, () -> {
            rentalHistoryService.addRentalHistory(sampleHistory);
        });
    }
}
