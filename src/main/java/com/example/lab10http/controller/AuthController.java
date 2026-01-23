package com.example.lab10http.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.lab10http.dto.AuthResponse;
import com.example.lab10http.dto.LoginRequest;
import com.example.lab10http.dto.RegisterRequest;
import com.example.lab10http.security.JwtService;
import com.example.lab10http.user.AppUser;
import com.example.lab10http.user.Role;
import com.example.lab10http.user.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(409).build();
        }
        Role role = "admin".equalsIgnoreCase(request.getUsername()) ? Role.ADMIN : Role.USER;
        AppUser user = new AppUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role);
        userRepository.save(user);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            logger.info("Successful login for user: {}", request.getUsername());
        } catch (AuthenticationException e) {
            // Lab 13: Log failed logins without sensitive data [cite: 48, 83, 86]
            logger.warn("Failed login attempt for user: {}", request.getUsername());
            return ResponseEntity.status(401).build();
        }

        AppUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null)
            return ResponseEntity.status(400).build();

        String username = jwtService.extractUsername(refreshToken);
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lab 13: Log suspicious/unauthorized access attempts [cite: 49, 50, 84]
        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            logger.error("SUSPICIOUS ACTIVITY: Invalid refresh token used for user: {}", username);
            return ResponseEntity.status(403).build();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(refreshToken, userDetails)) {
            // Lab 13: Token Rotation
            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
        }

        return ResponseEntity.status(403).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null) {
            String username = jwtService.extractUsername(refreshToken);
            userRepository.findByUsername(username).ifPresent(user -> {
                // Lab 13: Session invalidation on logout
                user.setRefreshToken(null);
                userRepository.save(user);
                logger.info("User {} logged out successfully", username);
            });
        }
        return ResponseEntity.status(204).build();
    }
}