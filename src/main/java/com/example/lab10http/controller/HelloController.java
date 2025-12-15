package com.example.lab10http.controller;

import com.example.lab10http.dto.GreetingRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloController {

  @GetMapping("hello")
  public String hello() {
    return "Hello from Spring Boot";
  }

  @PostMapping("/greet")
  public String greet(@Valid @RequestBody GreetingRequest request) {
    return "Hello " + request.getName();
  }
}
