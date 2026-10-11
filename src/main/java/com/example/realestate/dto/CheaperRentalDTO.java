package com.example.realestate.dto;

import com.example.realestate.entity.Property;

/**
 * Data Transfer Object representing a cheaper rental recommendation with detailed savings metrics.
 */
public class CheaperRentalDTO {

    private Property property;
    private Double previousRent;
    private Double currentRent;
    private Double monthlySavings;
    private Double percentageSavings;

    public CheaperRentalDTO() {
    }

    public CheaperRentalDTO(Property property, Double previousRent, Double currentRent,
                            Double monthlySavings, Double percentageSavings) {
        this.property = property;
        this.previousRent = previousRent;
        this.currentRent = currentRent;
        this.monthlySavings = monthlySavings;
        this.percentageSavings = percentageSavings;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Double getPreviousRent() {
        return previousRent;
    }

    public void setPreviousRent(Double previousRent) {
        this.previousRent = previousRent;
    }

    public Double getCurrentRent() {
        return currentRent;
    }

    public void setCurrentRent(Double currentRent) {
        this.currentRent = currentRent;
    }

    public Double getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(Double monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public Double getPercentageSavings() {
        return percentageSavings;
    }

    public void setPercentageSavings(Double percentageSavings) {
        this.percentageSavings = percentageSavings;
    }
}
