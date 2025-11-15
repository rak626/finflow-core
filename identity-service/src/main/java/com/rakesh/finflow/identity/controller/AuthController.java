package com.rakesh.finflow.identity.controller;

import com.rakesh.finflow.identity.dto.LoginRequest;
import com.rakesh.finflow.identity.dto.SignUpRequest;
import com.rakesh.finflow.identity.dto.TokenRequest;
import com.rakesh.finflow.identity.entity.UserCredential;
import com.rakesh.finflow.identity.service.AuthService;
import com.rakesh.finflow.identity.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest request) {
        try {
            authService.addUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("There is and error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }


    @PostMapping("/log-out")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        return null;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequest request) {
        try {
            return ResponseEntity.ok(refreshTokenService.refreshToken(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired refresh token");
        }
    }

    @PostMapping("/introspective")
    public ResponseEntity<Map<String, Object>> introspect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("active", false));
        }

        UserCredential user = (UserCredential) auth.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "userProfileId", user.getUserProfileId(),
                "active", true,
                "username", user.getUsername(),
                "roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        ));
    }
}
