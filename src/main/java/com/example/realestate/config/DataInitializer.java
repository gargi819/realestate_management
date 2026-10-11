package com.example.realestate.config;

import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * DataInitializer executes on application startup.
 * Ensures the default Admin account exists and links existing properties to owners.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, PropertyRepository propertyRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Ensure Default Admin Account exists
        String adminEmail = "admin@realestate.com";
        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);
        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword("admin123");
            admin.setPhone("+91 99999 88888");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println(">>> [DataInitializer] Default Admin account created successfully: " + adminEmail);
        } else {
            // Ensure role is ADMIN
            User admin = existingAdmin.get();
            if (!"ADMIN".equals(admin.getRole())) {
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }
        }

        // 2. Link unassigned properties to owners where appropriate
        // If property 7 (Spacious 2BHK in Navi Mumbai) has no owner, link it to owner srushti patil (ID 4)
        Optional<User> ownerOpt = userRepository.findByEmail("srushtipatil0807@gmail.com");
        if (ownerOpt.isPresent()) {
            Long ownerId = ownerOpt.get().getId();
            Optional<Property> prop7 = propertyRepository.findById(7L);
            if (prop7.isPresent() && prop7.get().getOwnerId() == null) {
                Property p = prop7.get();
                p.setOwnerId(ownerId);
                propertyRepository.save(p);
                System.out.println(">>> [DataInitializer] Property #7 assigned to owner ID " + ownerId);
            }
        }
    }
}
