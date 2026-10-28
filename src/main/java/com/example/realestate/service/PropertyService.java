package com.example.realestate.service;

import com.example.realestate.dto.RentalAlternativeDTO;
import com.example.realestate.dto.RentalComparisonDTO;
import com.example.realestate.entity.Property;
import com.example.realestate.entity.RentalHistory;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for Property operations,
 * including Smart Rental Comparison and safe property lifecycle updates.
 */
@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final RentalHistoryRepository rentalHistoryRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository,
                           RentalHistoryRepository rentalHistoryRepository) {
        this.propertyRepository = propertyRepository;
        this.rentalHistoryRepository = rentalHistoryRepository;
    }

    // 1. Add a new property
    public Property addProperty(Property property) {
        // Synchronize price and monthlyRent for rental listings
        if ("For Rent".equalsIgnoreCase(property.getStatus())) {
            if (property.getMonthlyRent() != null && property.getMonthlyRent() > 0) {
                property.setPrice(property.getMonthlyRent());
            } else if (property.getPrice() != null && property.getPrice() > 0) {
                property.setMonthlyRent(property.getPrice());
            }
            if (property.getSecurityDeposit() == null) {
                property.setSecurityDeposit(0.0);
            }
        }
        return propertyRepository.save(property);
    }

    // 2. Retrieve all properties
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // 3. Retrieve a single property by its ID
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    // 4. Update an existing property
    public Property updateProperty(Long id, Property propertyDetails) {
        Optional<Property> optionalProperty = propertyRepository.findById(id);
        if (optionalProperty.isPresent()) {
            Property existingProperty = optionalProperty.get();
            existingProperty.setTitle(propertyDetails.getTitle());
            existingProperty.setDescription(propertyDetails.getDescription());
            existingProperty.setPrice(propertyDetails.getPrice());
            existingProperty.setPropertyType(propertyDetails.getPropertyType());
            existingProperty.setLocation(propertyDetails.getLocation());
            existingProperty.setArea(propertyDetails.getArea());
            existingProperty.setBedrooms(propertyDetails.getBedrooms());
            existingProperty.setBathrooms(propertyDetails.getBathrooms());
            existingProperty.setStatus(propertyDetails.getStatus());

            // Update rental-specific fields
            existingProperty.setMonthlyRent(propertyDetails.getMonthlyRent());
            existingProperty.setSecurityDeposit(propertyDetails.getSecurityDeposit());

            // Ensure price is aligned if it's a rental property
            if ("For Rent".equalsIgnoreCase(propertyDetails.getStatus()) && propertyDetails.getMonthlyRent() != null && propertyDetails.getMonthlyRent() > 0) {
                existingProperty.setPrice(propertyDetails.getMonthlyRent());
            }

            if (propertyDetails.getOwnerId() != null) {
                existingProperty.setOwnerId(propertyDetails.getOwnerId());
            }
            return propertyRepository.save(existingProperty);
        }
        return null;
    }

    // 5. Delete a property by its ID
    public boolean deleteProperty(Long id) {
        if (propertyRepository.existsById(id)) {
            List<RentalHistory> history = rentalHistoryRepository.findByPropertyId(id);
            if (history != null && !history.isEmpty()) {
                rentalHistoryRepository.deleteAll(history);
            }
            propertyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 6. Search properties by location (case-insensitive)
    public List<Property> searchByLocation(String location) {
        return propertyRepository.findByLocationContainingIgnoreCase(location);
    }

    // 7. Filter properties by property type (case-insensitive)
    public List<Property> filterByPropertyType(String propertyType) {
        return propertyRepository.findByPropertyTypeIgnoreCase(propertyType);
    }

    // 8. Filter properties by status (case-insensitive)
    public List<Property> filterByStatus(String status) {
        return propertyRepository.findByStatusIgnoreCase(status);
    }

    // 9. Retrieve properties by owner ID
    public List<Property> getPropertiesByOwnerId(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }

    // 10. Smart Rental Comparison Based on Location, Area, and Rent
    public RentalComparisonDTO compareCheaperRentals(Long propertyId) {
        Optional<Property> selectedOpt = propertyRepository.findById(propertyId);
        if (selectedOpt.isEmpty()) {
            return null;
        }

        Property selected = selectedOpt.get();
        Double selectedRent = selected.getMonthlyRent() != null ? selected.getMonthlyRent() : selected.getPrice();
        Double selectedDeposit = selected.getSecurityDeposit() != null ? selected.getSecurityDeposit() : 0.0;
        Double selectedArea = selected.getArea() != null ? selected.getArea() : 0.0;

        String rawLocation = selected.getLocation() != null ? selected.getLocation() : "";
        String locality = extractLocality(rawLocation);
        String city = extractCity(rawLocation);

        RentalComparisonDTO result = new RentalComparisonDTO();
        result.setSelectedPropertyId(selected.getId());
        result.setSelectedTitle(selected.getTitle());
        result.setSelectedLocation(selected.getLocation());
        result.setSelectedPropertyType(selected.getPropertyType());
        result.setSelectedRent(selectedRent);
        result.setSelectedDeposit(selectedDeposit);
        result.setSelectedArea(selectedArea);
        result.setSelectedBedrooms(selected.getBedrooms());
        result.setSelectedBathrooms(selected.getBathrooms());
        result.setLocality(locality);
        result.setCity(city);

        List<Property> allRentals = propertyRepository.findAll().stream()
                .filter(p -> !p.getId().equals(propertyId))
                .filter(p -> "For Rent".equalsIgnoreCase(p.getStatus()))
                .toList();

        List<RentalAlternativeDTO> cheaperInSameLocality = new ArrayList<>();
        List<RentalAlternativeDTO> cheaperInSameCity = new ArrayList<>();
        List<RentalAlternativeDTO> otherInCity = new ArrayList<>();

        for (Property p : allRentals) {
            String pLoc = p.getLocation() != null ? p.getLocation().toLowerCase() : "";
            boolean isSameLoc = !locality.isEmpty() && (pLoc.contains(locality.toLowerCase()) || locality.toLowerCase().contains(extractLocality(pLoc).toLowerCase()));
            boolean isSameCity = !city.isEmpty() && (pLoc.contains(city.toLowerCase()) || city.toLowerCase().contains(extractCity(pLoc).toLowerCase()));

            if (!isSameLoc && !isSameCity) {
                for (String part : rawLocation.toLowerCase().split("[,\\s]+")) {
                    if (part.length() > 3 && pLoc.contains(part)) {
                        isSameCity = true;
                        break;
                    }
                }
            }

            Double rent = p.getMonthlyRent() != null ? p.getMonthlyRent() : p.getPrice();
            Double deposit = p.getSecurityDeposit() != null ? p.getSecurityDeposit() : 0.0;
            Double area = p.getArea() != null ? p.getArea() : 0.0;

            Double savings = selectedRent - rent;
            Double areaDiff = area - selectedArea;
            Double depositDiff = selectedDeposit - deposit;
            Double savingsPct = selectedRent > 0 ? (savings / selectedRent) * 100.0 : 0.0;
            boolean isCheaper = savings > 0;

            RentalAlternativeDTO dto = new RentalAlternativeDTO();
            dto.setId(p.getId());
            dto.setTitle(p.getTitle());
            dto.setDescription(p.getDescription());
            dto.setLocation(p.getLocation());
            dto.setPropertyType(p.getPropertyType());
            dto.setMonthlyRent(rent);
            dto.setSecurityDeposit(deposit);
            dto.setArea(area);
            dto.setBedrooms(p.getBedrooms());
            dto.setBathrooms(p.getBathrooms());
            dto.setStatus(p.getStatus());
            dto.setMonthlySavings(savings);
            dto.setAreaDifference(areaDiff);
            dto.setDepositDifference(depositDiff);
            dto.setSavingsPercentage(savingsPct);
            dto.setSameLocality(isSameLoc);
            dto.setSameCity(isSameCity);
            dto.setCheaper(isCheaper);

            if (isSameLoc && isCheaper) {
                cheaperInSameLocality.add(dto);
            } else if (isSameCity && isCheaper) {
                cheaperInSameCity.add(dto);
            } else if (isSameCity || isSameLoc) {
                otherInCity.add(dto);
            }
        }

        // Rank by lowest rent first, then higher area
        Comparator<RentalAlternativeDTO> rankComparator = Comparator
                .comparing(RentalAlternativeDTO::getMonthlyRent)
                .thenComparing((a, b) -> Double.compare(b.getArea(), a.getArea()));

        cheaperInSameLocality.sort(rankComparator);
        cheaperInSameCity.sort(rankComparator);
        otherInCity.sort(rankComparator);

        List<RentalAlternativeDTO> allCheaper = new ArrayList<>();
        allCheaper.addAll(cheaperInSameLocality);
        allCheaper.addAll(cheaperInSameCity);

        result.setCheaperAlternatives(allCheaper);
        result.setSameCityAlternatives(otherInCity);
        result.setHasCheaperInSameLocality(!cheaperInSameLocality.isEmpty());
        result.setHasCheaperInCity(!cheaperInSameCity.isEmpty());

        if (!cheaperInSameLocality.isEmpty()) {
            double maxSavings = cheaperInSameLocality.stream().mapToDouble(RentalAlternativeDTO::getMonthlySavings).max().orElse(0.0);
            result.setSummaryMessage("Found " + cheaperInSameLocality.size() + " cheaper rental alternative(s) in " + locality + ", saving up to ₹" + String.format("%,.0f", maxSavings) + " per month.");
        } else if (!cheaperInSameCity.isEmpty()) {
            double maxSavings = cheaperInSameCity.stream().mapToDouble(RentalAlternativeDTO::getMonthlySavings).max().orElse(0.0);
            result.setSummaryMessage("No cheaper rentals found directly in " + locality + ". Showing " + cheaperInSameCity.size() + " cheaper alternative(s) in " + city + ", saving up to ₹" + String.format("%,.0f", maxSavings) + "/month.");
        } else {
            result.setSummaryMessage("Best Rental Value! No cheaper rental properties found in " + (city.isEmpty() ? "this area" : city) + ". This property currently offers the most competitive monthly rate.");
        }

        return result;
    }

    private String extractLocality(String location) {
        if (location == null || location.trim().isEmpty()) return "";
        String[] parts = location.split(",");
        return parts[0].trim();
    }

    private String extractCity(String location) {
        if (location == null || location.trim().isEmpty()) return "";
        String[] parts = location.split(",");
        if (parts.length > 1) {
            return parts[parts.length - 1].trim();
        }
        String trimmed = location.trim();
        int spaceIdx = trimmed.lastIndexOf(" ");
        if (spaceIdx > 0 && spaceIdx < trimmed.length() - 1) {
            return trimmed.substring(0, spaceIdx).trim();
        }
        return trimmed;
    }
}
