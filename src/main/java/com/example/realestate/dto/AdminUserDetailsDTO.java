package com.example.realestate.dto;

import com.example.realestate.entity.Property;
import com.example.realestate.entity.RentalHistory;

import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailsDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private List<Property> properties = new ArrayList<>();
    private List<RentalHistory> rentalHistory = new ArrayList<>();

    public AdminUserDetailsDTO() {
    }

    public AdminUserDetailsDTO(Long id, String name, String email, String phone, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<RentalHistory> getRentalHistory() {
        return rentalHistory;
    }

    public void setRentalHistory(List<RentalHistory> rentalHistory) {
        this.rentalHistory = rentalHistory;
    }
}
