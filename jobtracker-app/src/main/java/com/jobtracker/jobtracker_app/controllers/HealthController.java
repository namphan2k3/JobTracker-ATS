package com.jobtracker.jobtracker_app.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/public/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP"
        );
    }
}

