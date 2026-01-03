package com.example.lab10http.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GreetingRequest {

  @NotBlank(message = "name is required")
  @Size(min = 2, max = 30, message = "name must be 2-30 characters")
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
