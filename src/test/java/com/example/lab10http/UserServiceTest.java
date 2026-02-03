package com.example.lab10http;

import com.example.lab10http.user.AppUser;
import com.example.lab10http.user.Role;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

  @Test
  public void shouldEncodePasswordWhenSavingUser() {
    // Mock dependencies [cite: 118]
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

    // Setup behavior [cite: 119]
    String rawPassword = "password123";
    String encodedPassword = "hashed_password_abc";
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

    // Simulate logic [cite: 119]
    String result = passwordEncoder.encode(rawPassword);
    AppUser user = new AppUser("testUser", result, Role.USER);

    // Verify [cite: 112]
    assertNotNull(user.getPassword());
    assertEquals(encodedPassword, user.getPassword());
    verify(passwordEncoder).encode(rawPassword);
  }
}