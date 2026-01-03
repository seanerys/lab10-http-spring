package com.example.lab10http.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Map<Integer, String> USERS = Map.of(
            1, "Alice",
            2, "Bob"
    );

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int id) {
        String name = USERS.get(id);

        if (name == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "error", "User not found",
                    "id", id
            ));
        }

        return ResponseEntity.ok(Map.of(
                "id", id,
                "name", name
        ));
    }
}
