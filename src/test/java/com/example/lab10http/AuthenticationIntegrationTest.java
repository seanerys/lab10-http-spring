package com.example.lab10http;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void shouldReturnUnauthorizedForProtectedResourceWithoutToken() throws Exception {
    // Lab 14 Requirement: Verify unauthorized user --> access denied
    mockMvc.perform(get("/api/user/hello"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void shouldReturnUnauthorizedForInvalidEndpoint() throws Exception {
    // Updated for Lab 14: In a hardened app, unauthenticated requests to
    // non-existent paths should still be blocked with 401, not 404.
    mockMvc.perform(get("/api/invalid/path"))
        .andExpect(status().isUnauthorized());
  }
}