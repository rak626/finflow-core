package com.rakesh.finflow.identity.service;

import com.nimbusds.jose.JOSEException;
import com.rakesh.finflow.common.cache.service.RedisService;
import com.rakesh.finflow.common.dto.identity.TokenResponse;
import com.rakesh.finflow.identity.dto.TokenRequest;
import com.rakesh.finflow.identity.entity.RefreshToken;
import com.rakesh.finflow.identity.entity.RefreshTokenStatus;
import com.rakesh.finflow.identity.entity.UserCredential;
import com.rakesh.finflow.identity.props.RefreshTokenProperties;
import com.rakesh.finflow.identity.repository.RefreshTokenRepository;
import com.rakesh.finflow.identity.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProperties refreshTokenProperties;
    private final RedisService redisService;
    private final UserCredentialRepository userCredentialRepository;
    private final JwtService jwtService;

    private static final String REFRESH_PREFIX = "refresh:";
    private static final SecureRandom secureRandom = new SecureRandom();

    public String createRefreshToken(UserCredential userCredential, String deviceId, String ipAddress) {
        String rawToken = generateSecureToken();
        String hashedToken = sha256(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userCredential.getId())
                .username(userCredential.getUsername())
                .tokenHash(hashedToken)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(refreshTokenProperties.getExpiryDays(), ChronoUnit.DAYS))
                .isRevoked(false)
                .status(RefreshTokenStatus.REFRESH_TOKEN_ACTIVE)
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .build();

        refreshTokenRepository.save(refreshToken);
        redisService.save(REFRESH_PREFIX + userCredential.getUsername() + userCredential.getId() + deviceId, hashedToken, refreshTokenProperties.getExpiryDays(), TimeUnit.DAYS);
        return rawToken;
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Could not hash token", e);
        }
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String sha256(String input) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 error", e);
        }
    }


    public TokenResponse refreshToken(TokenRequest request) throws JOSEException {

        // 1️⃣ Hash and lookup existing refresh token
        String tokenHash = hashToken(request.getRefreshToken());
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // 2️⃣ Validate token status
        if (existing.getIsRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token is expired or revoked");
        }

        // 3️⃣ Revoke old token immediately (rotation)
        existing.setIsRevoked(true);
        refreshTokenRepository.save(existing);

        // 4️⃣ Load user (you have the userId from refresh token)
        UserCredential userCredential = userCredentialRepository.findById(existing.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5️⃣ Create new access and refresh tokens
        String newAccessToken = jwtService.generateAccessToken(userCredential.getUsername());
        String newRefreshToken = this.createRefreshToken(userCredential, request.getDeviceId(), request.getIpAddress());

        // 6️⃣ Return both tokens

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
//                .tokenType("Bearer")
//                .expiresIn(jwtService.getAccessTokenExpirySeconds())
                .username(userCredential.getUsername())
                .userId(userCredential.getId())
                .build();
    }
}
