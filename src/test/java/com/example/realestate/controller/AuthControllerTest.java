package com.example.realestate.controller;

import com.example.realestate.dto.AuthRequest;
import com.example.realestate.dto.RegisterRequest;
import com.example.realestate.entity.User;
import com.example.realestate.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;

    @BeforeEach
    public void setUp() {
        sampleUser = new User("Rahul Sharma", "rahul@example.com", "password123", "9876543210", "TENANT");
        sampleUser.setId(1L);
    }

    @Test
    public void testLoginSuccessReturns200OK() throws Exception {
        when(userRepository.findByEmail("rahul@example.com")).thenReturn(Optional.of(sampleUser));

        AuthRequest request = new AuthRequest("rahul@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.name").value("Rahul Sharma"));
    }

    @Test
    public void testLoginWrongPasswordReturns401Unauthorized() throws Exception {
        when(userRepository.findByEmail("rahul@example.com")).thenReturn(Optional.of(sampleUser));

        AuthRequest request = new AuthRequest("rahul@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testRegisterSuccessReturns201Created() throws Exception {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "pass123", "9999999999", "TENANT");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }
}
