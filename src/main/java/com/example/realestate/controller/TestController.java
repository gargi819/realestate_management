package com.example.realestate.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "*"})
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Real Estate Management System Backend is Running!";
    }
}
