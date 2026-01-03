package com.example.lab10http.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestProtectedController {

    @GetMapping("/protected")
    public String protectedRoute() {
        return "You are authenticated ✅";
    }
}
