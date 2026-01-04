// src/main/java/com/example/lab10http/controller/UserHelloController.java
package com.example.lab10http.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserHelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello User 🙂";
    }
}
