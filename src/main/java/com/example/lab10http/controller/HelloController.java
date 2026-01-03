package com.example.lab10http.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab10http.dto.GreetingRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class HelloController {

    @PostMapping("/greet")
    public ResponseEntity<Map<String, String>> greet(@Valid @RequestBody GreetingRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Hello, " + body.getName() + "!"));
    }

    @GetMapping("/greet")
    public ResponseEntity<Map<String, String>> greetGet(
            @RequestParam(defaultValue = "en") String lang,
            @RequestHeader(value = "X-Client", defaultValue = "unknown") String client
    ) {
        String msg = "pl".equalsIgnoreCase(lang) ? "Czesc" : "Hello";
        return ResponseEntity.ok(Map.of(
                "message", msg,
                "client", client
        ));
    }
}
