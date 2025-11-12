package com.rakesh.finflow.identity.service;

import com.nimbusds.jose.JOSEException;
import com.rakesh.finflow.common.cache.service.RedisService;
import com.rakesh.finflow.identity.dto.LoginRequest;
import com.rakesh.finflow.identity.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final JwtService jwtService;
    private final RedisService redisService;

    private static final String ACCESS_PREFIX = "access:";


    public String generateAccessToken(UserCredential userCredential, LoginRequest request) throws JOSEException {
        String accessToken = jwtService.generateAccessToken(userCredential.getUsername());
        redisService.save(ACCESS_PREFIX + userCredential.getUsername() + request.getDeviceId(), accessToken, 15, TimeUnit.MINUTES);
        return accessToken;
    }

//    public TokenResponse processToken(String authHeader, UUID userId) {
//
//        this.validateAuthHeader(authHeader);
//        String token = authHeader.substring(7);
//        boolean isAccess = jwtService.validateAccessToken(token);
//        if (isAccess) {
//            // Renew TTL for active user session
//            log.info("Access Verified for token...");
//            redisService.save(ACCESS_PREFIX + userId, token, 15, TimeUnit.MINUTES);
//            return TokenResponse
//                    .builder()
//                    .status(Set.of(TokenStatus.ACCESS_TOKEN_ACTIVE))
//                    .accessToken(token)
//                    .userId(userId)
//                    .build();
//        }
//        throw new RuntimeException("Invalid Auth Access request");
//    }
//
//    private void validateAuthHeader(String authHeader) {
//        log.info("Auth Header Validation in progress... ");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Invalid auth request");
//        }
//    }

    public boolean validateToken(String token) {
        return jwtService.validateAccessToken(token);
    }

    public String getUsernameFromJWT(String token) {
        return jwtService.getUsernameFromJWT(token);
    }
}
