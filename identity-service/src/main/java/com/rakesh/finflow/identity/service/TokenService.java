package com.rakesh.finflow.identity.service;

import com.nimbusds.jose.JOSEException;
import com.rakesh.finflow.identity.dto.AuthRequest;
import com.rakesh.finflow.identity.dto.TokenResponse;
import com.rakesh.finflow.identity.entity.RefreshToken;
import com.rakesh.finflow.identity.entity.TokenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final JwtService jwtService;
    private final RedisService redisService;

    private static final String ACCESS_PREFIX = "access:";

    private final RefreshTokenService refreshTokenService;

    public TokenResponse generateTokens(AuthRequest request) throws JOSEException {
        String access = jwtService.generateAccessToken(String.valueOf(request.getUserId()));
        // Todo: Can be changed into rotating refresh tokens
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUserId(), request.getDeviceId(), request.getIpAddress());

        redisService.save(ACCESS_PREFIX + request.getUserId(), access, 15, TimeUnit.MINUTES);

        return TokenResponse
                .builder()
                .accessToken(access)
                .refreshToken(refreshToken.getToken())
                .userId(request.getUserId())
                .status(Set.of(TokenStatus.ACCESS_TOKEN_ACTIVE, TokenStatus.REFRESH_TOKEN_ACTIVE))
                .build();
    }

    public TokenResponse processToken(String authHeader, UUID userId) {

        this.validateAuthHeader(authHeader);
        String token = authHeader.substring(7);
        boolean isAccess = jwtService.validateAccessToken(token);
        if (isAccess) {
            // Renew TTL for active user session
            log.info("Access Verified for token...");
            redisService.save(ACCESS_PREFIX + userId, token, 15, TimeUnit.MINUTES);
            return TokenResponse
                    .builder()
                    .status(Set.of(TokenStatus.ACCESS_TOKEN_ACTIVE))
                    .accessToken(token)
                    .userId(userId)
                    .build();
        }
        throw new RuntimeException("Invalid Auth Access request");
    }

    private void validateAuthHeader(String authHeader) {
        log.info("Auth Header Validation in progress... ");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid auth request");
        }
    }
}
