package com.rakesh.finflow.identity.controller;

import com.nimbusds.jose.JOSEException;
import com.rakesh.finflow.identity.dto.AuthRequest;
import com.rakesh.finflow.identity.dto.TokenResponse;
import com.rakesh.finflow.identity.entity.TokenStatus;
import com.rakesh.finflow.identity.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final TokenService tokenService;

    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody AuthRequest request) throws JOSEException {
        // Normally you'd validate user credentials here
        return ResponseEntity.ok(tokenService.generateTokens(request));
    }

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestHeader("userId") UUID userId,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(tokenService.processToken(authHeader, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse
                            .builder()
                            .status(Set.of(TokenStatus.ACCESS_TOKEN_EXPIRED))
                            .error_message("Token processing failed: " + e.getMessage())
                            .userId(userId)
                            .build());
        }
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        return null;
    }
}
