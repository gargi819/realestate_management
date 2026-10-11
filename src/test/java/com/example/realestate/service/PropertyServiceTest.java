package com.example.realestate.service;

import com.example.realestate.entity.Property;
import com.example.realestate.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;

    private Property property;

    @BeforeEach
    public void setUp() {
        property = new Property(
                "3 BHK Villa",
                "Spacious garden villa",
                12000000.0,
                "Villa",
                "Pune",
                2500.0,
                3,
                3,
                "Available"
        );
        property.setId(1L);
    }

    @Test
    public void testAddProperty() {
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        Property saved = propertyService.addProperty(property);
        assertNotNull(saved);
        assertEquals("3 BHK Villa", saved.getTitle());
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    public void testGetAllProperties() {
        when(propertyRepository.findAll()).thenReturn(Arrays.asList(property));

        List<Property> list = propertyService.getAllProperties();
        assertEquals(1, list.size());
        assertEquals("3 BHK Villa", list.get(0).getTitle());
    }

    @Test
    public void testGetPropertyByIdFound() {
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        Optional<Property> found = propertyService.getPropertyById(1L);
        assertTrue(found.isPresent());
        assertEquals("3 BHK Villa", found.get().getTitle());
    }

    @Test
    public void testGetPropertyByIdNotFound() {
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Property> found = propertyService.getPropertyById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    public void testUpdatePropertySuccess() {
        Property updateInfo = new Property(
                "Updated Villa",
                "Updated description",
                13000000.0,
                "Villa",
                "Pune",
                2600.0,
                4,
                4,
                "Sold"
        );

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Property updated = propertyService.updateProperty(1L, updateInfo);
        assertNotNull(updated);
        assertEquals("Updated Villa", updated.getTitle());
        assertEquals("Sold", updated.getStatus());
        assertEquals(13000000.0, updated.getPrice());
    }

    @Test
    public void testUpdatePropertyNotFound() {
        Property updateInfo = new Property();
        when(propertyRepository.findById(999L)).thenReturn(Optional.empty());

        Property updated = propertyService.updateProperty(999L, updateInfo);
        assertNull(updated);
        verify(propertyRepository, never()).save(any());
    }

    @Test
    public void testDeletePropertySuccess() {
        when(propertyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(propertyRepository).deleteById(1L);

        boolean result = propertyService.deleteProperty(1L);
        assertTrue(result);
        verify(propertyRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeletePropertyNotFound() {
        when(propertyRepository.existsById(999L)).thenReturn(false);

        boolean result = propertyService.deleteProperty(999L);
        assertFalse(result);
        verify(propertyRepository, never()).deleteById(any());
    }

    @Test
    public void testSearchByLocation() {
        when(propertyRepository.findByLocationContainingIgnoreCase("pune")).thenReturn(Arrays.asList(property));

        List<Property> results = propertyService.searchByLocation("pune");
        assertEquals(1, results.size());
        assertEquals("Pune", results.get(0).getLocation());
        verify(propertyRepository, times(1)).findByLocationContainingIgnoreCase("pune");
    }

    @Test
    public void testFilterByPropertyType() {
        when(propertyRepository.findByPropertyTypeIgnoreCase("Villa")).thenReturn(Arrays.asList(property));

        List<Property> results = propertyService.filterByPropertyType("Villa");
        assertEquals(1, results.size());
        assertEquals("Villa", results.get(0).getPropertyType());
        verify(propertyRepository, times(1)).findByPropertyTypeIgnoreCase("Villa");
    }

    @Test
    public void testFilterByStatus() {
        when(propertyRepository.findByStatusIgnoreCase("Available")).thenReturn(Arrays.asList(property));

        List<Property> results = propertyService.filterByStatus("Available");
        assertEquals(1, results.size());
        assertEquals("Available", results.get(0).getStatus());
        verify(propertyRepository, times(1)).findByStatusIgnoreCase("Available");
    }
}
