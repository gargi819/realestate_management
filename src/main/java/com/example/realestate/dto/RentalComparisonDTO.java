package com.example.realestate.dto;

import java.util.List;

/**
 * Data Transfer Object representing the side-by-side smart rental comparison
 * for a selected rental property.
 */
public class RentalComparisonDTO {
    // Selected baseline property details
    private Long selectedPropertyId;
    private String selectedTitle;
    private String selectedLocation;
    private String selectedPropertyType;
    private Double selectedRent;
    private Double selectedDeposit;
    private Double selectedArea;
    private Integer selectedBedrooms;
    private Integer selectedBathrooms;

    // Parsed locality & city
    private String locality;
    private String city;

    // Evaluated Alternatives
    private List<RentalAlternativeDTO> cheaperAlternatives;
    private List<RentalAlternativeDTO> sameCityAlternatives;

    private boolean hasCheaperInSameLocality;
    private boolean hasCheaperInCity;
    private String summaryMessage;

    public RentalComparisonDTO() {
    }

    public Long getSelectedPropertyId() {
        return selectedPropertyId;
    }

    public void setSelectedPropertyId(Long selectedPropertyId) {
        this.selectedPropertyId = selectedPropertyId;
    }

    public String getSelectedTitle() {
        return selectedTitle;
    }

    public void setSelectedTitle(String selectedTitle) {
        this.selectedTitle = selectedTitle;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getSelectedPropertyType() {
        return selectedPropertyType;
    }

    public void setSelectedPropertyType(String selectedPropertyType) {
        this.selectedPropertyType = selectedPropertyType;
    }

    public Double getSelectedRent() {
        return selectedRent;
    }

    public void setSelectedRent(Double selectedRent) {
        this.selectedRent = selectedRent;
    }

    public Double getSelectedDeposit() {
        return selectedDeposit;
    }

    public void setSelectedDeposit(Double selectedDeposit) {
        this.selectedDeposit = selectedDeposit;
    }

    public Double getSelectedArea() {
        return selectedArea;
    }

    public void setSelectedArea(Double selectedArea) {
        this.selectedArea = selectedArea;
    }

    public Integer getSelectedBedrooms() {
        return selectedBedrooms;
    }

    public void setSelectedBedrooms(Integer selectedBedrooms) {
        this.selectedBedrooms = selectedBedrooms;
    }

    public Integer getSelectedBathrooms() {
        return selectedBathrooms;
    }

    public void setSelectedBathrooms(Integer selectedBathrooms) {
        this.selectedBathrooms = selectedBathrooms;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<RentalAlternativeDTO> getCheaperAlternatives() {
        return cheaperAlternatives;
    }

    public void setCheaperAlternatives(List<RentalAlternativeDTO> cheaperAlternatives) {
        this.cheaperAlternatives = cheaperAlternatives;
    }

    public List<RentalAlternativeDTO> getSameCityAlternatives() {
        return sameCityAlternatives;
    }

    public void setSameCityAlternatives(List<RentalAlternativeDTO> sameCityAlternatives) {
        this.sameCityAlternatives = sameCityAlternatives;
    }

    public boolean isHasCheaperInSameLocality() {
        return hasCheaperInSameLocality;
    }

    public void setHasCheaperInSameLocality(boolean hasCheaperInSameLocality) {
        this.hasCheaperInSameLocality = hasCheaperInSameLocality;
    }

    public boolean isHasCheaperInCity() {
        return hasCheaperInCity;
    }

    public void setHasCheaperInCity(boolean hasCheaperInCity) {
        this.hasCheaperInCity = hasCheaperInCity;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }
}
