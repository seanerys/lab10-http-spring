package com.example.lab10http.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import com.example.lab10http.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;

        public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
                this.jwtAuthFilter = jwtAuthFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Security headers
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin())
                                                .contentTypeOptions(Customizer.withDefaults())
                                                .referrerPolicy(
                                                                referrer -> referrer.policy(
                                                                                ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; " +
                                                                                                "script-src 'self'; " +
                                                                                                "object-src 'none';")))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/error").permitAll()
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .httpBasic(Customizer.withDefaults())
                                .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
