package com.example.realestate.dto;

/**
 * DTO for authentication response.
 */
public class AuthResponse {

    private boolean success;
    private String message;
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;

    public AuthResponse() {
    }

    public static AuthResponse failure(String message) {
        AuthResponse res = new AuthResponse();
        res.setSuccess(false);
        res.setMessage(message);
        return res;
    }

    public static AuthResponse success(Long id, String name, String email, String phone, String role, String message) {
        AuthResponse res = new AuthResponse();
        res.setSuccess(true);
        res.setId(id);
        res.setName(name);
        res.setEmail(email);
        res.setPhone(phone);
        res.setRole(role);
        res.setMessage(message);
        return res;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
