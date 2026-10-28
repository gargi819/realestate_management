package com.example.realestate.dto;

/**
 * Data Transfer Object for Admin Dashboard platform statistics.
 * Tracks regular platform users (excluding admin) and counts for properties, tenants, owners, buyers, and rental contracts.
 */
public class AdminStatsDTO {
    private long totalUsers; // Regular registered users (excluding ADMIN)
    private long totalProperties;
    private long totalRenters;
    private long totalBuyers;
    private long totalOwners;
    private long totalRentalContracts;

    public AdminStatsDTO() {
    }

    public AdminStatsDTO(long totalUsers, long totalProperties, long totalRenters, long totalBuyers, long totalOwners, long totalRentalContracts) {
        this.totalUsers = totalUsers;
        this.totalProperties = totalProperties;
        this.totalRenters = totalRenters;
        this.totalBuyers = totalBuyers;
        this.totalOwners = totalOwners;
        this.totalRentalContracts = totalRentalContracts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public long getTotalRenters() {
        return totalRenters;
    }

    public void setTotalRenters(long totalRenters) {
        this.totalRenters = totalRenters;
    }

    public long getTotalBuyers() {
        return totalBuyers;
    }

    public void setTotalBuyers(long totalBuyers) {
        this.totalBuyers = totalBuyers;
    }

    public long getTotalOwners() {
        return totalOwners;
    }

    public void setTotalOwners(long totalOwners) {
        this.totalOwners = totalOwners;
    }

    public long getTotalRentalContracts() {
        return totalRentalContracts;
    }

    public void setTotalRentalContracts(long totalRentalContracts) {
        this.totalRentalContracts = totalRentalContracts;
    }
}
