package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/public/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.<Map<String, String>>builder()
                .message("OK")
                .data(Map.of("status", "UP"))
                .build();
    }
}
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

