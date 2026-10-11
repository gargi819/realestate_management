package com.example.realestate.controller;

import com.example.realestate.entity.Property;
import com.example.realestate.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyController.class)
public class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Property sampleProperty;

    @BeforeEach
    public void setUp() {
        sampleProperty = new Property(
                "2 BHK Apartment",
                "Spacious apartment in a good location",
                7500000.0,
                "Apartment",
                "Mumbai",
                950.0,
                2,
                2,
                "Available"
        );
        sampleProperty.setId(1L);
    }

    // 1. Test POST /api/properties -> 201 Created
    @Test
    public void testAddPropertyReturns201Created() throws Exception {
        when(propertyService.addProperty(any(Property.class))).thenReturn(sampleProperty);

        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProperty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("2 BHK Apartment"))
                .andExpect(jsonPath("$.location").value("Mumbai"));
    }

    // 2. Test GET /api/properties -> 200 OK with list
    @Test
    public void testGetAllPropertiesReturnsList() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(sampleProperty));

        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("2 BHK Apartment"));
    }

    // 3. Test GET /api/properties/search?location=Mumbai
    @Test
    public void testSearchPropertiesByLocation() throws Exception {
        when(propertyService.searchByLocation("Mumbai")).thenReturn(Arrays.asList(sampleProperty));

        mockMvc.perform(get("/api/properties/search").param("location", "Mumbai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].location").value("Mumbai"));
    }

    // 4. Test GET /api/properties/filter?propertyType=Apartment
    @Test
    public void testFilterPropertiesByType() throws Exception {
        when(propertyService.filterByPropertyType("Apartment")).thenReturn(Arrays.asList(sampleProperty));

        mockMvc.perform(get("/api/properties/filter").param("propertyType", "Apartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].propertyType").value("Apartment"));
    }

    // 5. Test GET /api/properties/filter/status?status=Available
    @Test
    public void testFilterPropertiesByStatus() throws Exception {
        when(propertyService.filterByStatus("Available")).thenReturn(Arrays.asList(sampleProperty));

        mockMvc.perform(get("/api/properties/filter/status").param("status", "Available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("Available"));
    }

    // 6. Test GET /api/properties/{id} -> 200 OK
    @Test
    public void testGetPropertyByIdFound() throws Exception {
        when(propertyService.getPropertyById(1L)).thenReturn(Optional.of(sampleProperty));

        mockMvc.perform(get("/api/properties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("2 BHK Apartment"));
    }

    // 6b. Test GET /api/properties/{id} -> 404 Not Found
    @Test
    public void testGetPropertyByIdNotFound() throws Exception {
        when(propertyService.getPropertyById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/properties/999"))
                .andExpect(status().isNotFound());
    }

    // 7. Test PUT /api/properties/{id} -> 200 OK
    @Test
    public void testUpdatePropertySuccess() throws Exception {
        Property updatedProperty = new Property(
                "Updated 2 BHK Apartment",
                "Fully renovated",
                8000000.0,
                "Apartment",
                "Mumbai",
                1000.0,
                2,
                2,
                "Sold"
        );
        updatedProperty.setId(1L);

        when(propertyService.updateProperty(eq(1L), any(Property.class))).thenReturn(updatedProperty);

        mockMvc.perform(put("/api/properties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProperty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated 2 BHK Apartment"))
                .andExpect(jsonPath("$.status").value("Sold"));
    }

    // 7b. Test PUT /api/properties/{id} -> 404 Not Found
    @Test
    public void testUpdatePropertyNotFound() throws Exception {
        when(propertyService.updateProperty(eq(999L), any(Property.class))).thenReturn(null);

        mockMvc.perform(put("/api/properties/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProperty)))
                .andExpect(status().isNotFound());
    }

    // 8. Test DELETE /api/properties/{id} -> 204 No Content
    @Test
    public void testDeletePropertySuccess() throws Exception {
        when(propertyService.deleteProperty(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/properties/1"))
                .andExpect(status().isNoContent());
    }

    // 8b. Test DELETE /api/properties/{id} -> 404 Not Found
    @Test
    public void testDeletePropertyNotFound() throws Exception {
        when(propertyService.deleteProperty(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/properties/999"))
                .andExpect(status().isNotFound());
    }
}
