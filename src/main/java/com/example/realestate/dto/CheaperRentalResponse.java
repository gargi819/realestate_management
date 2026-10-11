package com.example.realestate.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response wrapper for cheaper rental recommendations for a tenant.
 */
public class CheaperRentalResponse {

    private Long tenantId;
    private String tenantName;
    private Double previousRent;
    private List<CheaperRentalDTO> cheaperRentals = new ArrayList<>();
    private String message;

    public CheaperRentalResponse() {
    }

    public CheaperRentalResponse(Long tenantId, String tenantName, Double previousRent,
                                 List<CheaperRentalDTO> cheaperRentals, String message) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.previousRent = previousRent;
        this.cheaperRentals = cheaperRentals;
        this.message = message;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Double getPreviousRent() {
        return previousRent;
    }

    public void setPreviousRent(Double previousRent) {
        this.previousRent = previousRent;
    }

    public List<CheaperRentalDTO> getCheaperRentals() {
        return cheaperRentals;
    }

    public void setCheaperRentals(List<CheaperRentalDTO> cheaperRentals) {
        this.cheaperRentals = cheaperRentals;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
