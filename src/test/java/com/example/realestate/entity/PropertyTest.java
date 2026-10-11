package com.example.realestate.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyTest {

    @Test
    public void testPropertyGettersAndSetters() {
        Property property = new Property();
        property.setId(1L);
        property.setTitle("Luxury Beachside Villa");
        property.setDescription("Spacious 4 BHK villa with sea view");
        property.setPrice(1250000.0);
        property.setPropertyType("Villa");
        property.setLocation("Miami, FL");
        property.setArea(3500.0);
        property.setBedrooms(4);
        property.setBathrooms(5);
        property.setStatus("Available");

        assertEquals(1L, property.getId());
        assertEquals("Luxury Beachside Villa", property.getTitle());
        assertEquals("Spacious 4 BHK villa with sea view", property.getDescription());
        assertEquals(1250000.0, property.getPrice());
        assertEquals("Villa", property.getPropertyType());
        assertEquals("Miami, FL", property.getLocation());
        assertEquals(3500.0, property.getArea());
        assertEquals(4, property.getBedrooms());
        assertEquals(5, property.getBathrooms());
        assertEquals("Available", property.getStatus());
    }

    @Test
    public void testPropertyParameterizedConstructor() {
        Property property = new Property(
                "Modern Apartment",
                "Downtown 2 bedroom apartment",
                450000.0,
                "Apartment",
                "New York, NY",
                1200.0,
                2,
                2,
                "Available"
        );

        assertNull(property.getId());
        assertEquals("Modern Apartment", property.getTitle());
        assertEquals(450000.0, property.getPrice());
        assertEquals("Apartment", property.getPropertyType());
        assertEquals("New York, NY", property.getLocation());
        assertEquals(1200.0, property.getArea());
        assertEquals(2, property.getBedrooms());
        assertEquals(2, property.getBathrooms());
        assertEquals("Available", property.getStatus());
        assertTrue(property.toString().contains("Modern Apartment"));
    }
}
