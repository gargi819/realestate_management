package com.example.realestate.controller;

import com.example.realestate.dto.AuthRequest;
import com.example.realestate.dto.AuthResponse;
import com.example.realestate.dto.RegisterRequest;
import com.example.realestate.entity.User;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST Controller for user authentication and session management.
 * Verifies email and password credentials directly against the MySQL database.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. POST /api/auth/login - Authenticate with email & password against MySQL
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(AuthResponse.failure("Email and password are required."));
        }

        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.failure("User with this email not found. Please register or check your email."));
        }

        User user = userOpt.get();

        // Verify password
        if (!request.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.failure("Incorrect password. Please try again."));
        }

        AuthResponse success = AuthResponse.success(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                "Login successful!"
        );
        return ResponseEntity.ok(success);
    }

    // 2. POST /api/auth/register - Create new user account in MySQL
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(AuthResponse.failure("Full name is required."));
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(AuthResponse.failure("Email address is required."));
        }
        if (request.getPassword() == null || request.getPassword().trim().length() < 4) {
            return ResponseEntity.badRequest().body(AuthResponse.failure("Password must be at least 4 characters."));
        }

        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.failure("An account with this email address already exists."));
        }

        User newUser = new User();
        newUser.setName(request.getName().trim());
        newUser.setEmail(email);
        newUser.setPassword(request.getPassword());
        newUser.setPhone(request.getPhone());
        newUser.setRole(request.getRole() != null && !request.getRole().trim().isEmpty() ? request.getRole() : "TENANT");

        User saved = userRepository.save(newUser);

        AuthResponse response = AuthResponse.success(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getRole(),
                "Account created successfully!"
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 3. GET /api/auth/me/{id} - Get user profile by ID
    @GetMapping("/me/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
