package com.example.realestate.dto;

public class AdminStatsDTO {
    private long totalUsers;
    private long totalProperties;
    private long totalRenters;
    private long totalBuyers;
    private long totalOwners;

    public AdminStatsDTO() {
    }

    public AdminStatsDTO(long totalUsers, long totalProperties, long totalRenters, long totalBuyers, long totalOwners) {
        this.totalUsers = totalUsers;
        this.totalProperties = totalProperties;
        this.totalRenters = totalRenters;
        this.totalBuyers = totalBuyers;
        this.totalOwners = totalOwners;
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
}
