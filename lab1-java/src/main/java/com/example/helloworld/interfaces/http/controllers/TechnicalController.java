package com.example.helloworld.interfaces.http.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TechnicalController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/test")
    public Map<String, String> hello() {
        return Map.of("message", "Hello world!");
    }
}
