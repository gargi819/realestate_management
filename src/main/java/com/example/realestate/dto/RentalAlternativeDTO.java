package com.example.realestate.dto;

/**
 * Data Transfer Object for an alternative rental property compared against a selected property.
 */
public class RentalAlternativeDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String propertyType;
    private Double monthlyRent;
    private Double securityDeposit;
    private Double area;
    private Integer bedrooms;
    private Integer bathrooms;
    private String status;

    // Comparison metrics against the selected property
    private Double monthlySavings;      // Positive value = money saved per month
    private Double areaDifference;      // Positive value = more sq.ft than selected property
    private Double depositDifference;   // Positive value = lower deposit than selected property
    private Double savingsPercentage;

    private boolean sameLocality;
    private boolean sameCity;
    private boolean cheaper;

    public RentalAlternativeDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public Double getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(Double securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(Double monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public Double getAreaDifference() {
        return areaDifference;
    }

    public void setAreaDifference(Double areaDifference) {
        this.areaDifference = areaDifference;
    }

    public Double getDepositDifference() {
        return depositDifference;
    }

    public void setDepositDifference(Double depositDifference) {
        this.depositDifference = depositDifference;
    }

    public Double getSavingsPercentage() {
        return savingsPercentage;
    }

    public void setSavingsPercentage(Double savingsPercentage) {
        this.savingsPercentage = savingsPercentage;
    }

    public boolean isSameLocality() {
        return sameLocality;
    }

    public void setSameLocality(boolean sameLocality) {
        this.sameLocality = sameLocality;
    }

    public boolean isSameCity() {
        return sameCity;
    }

    public void setSameCity(boolean sameCity) {
        this.sameCity = sameCity;
    }

    public boolean isCheaper() {
        return cheaper;
    }

    public void setCheaper(boolean cheaper) {
        this.cheaper = cheaper;
    }
}
