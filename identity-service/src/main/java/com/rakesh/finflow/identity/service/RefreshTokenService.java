package com.rakesh.finflow.identity.service;

import com.rakesh.finflow.identity.entity.RefreshToken;
import com.rakesh.finflow.identity.props.RefreshTokenProperties;
import com.rakesh.finflow.identity.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProperties refreshTokenProperties;
    private final RedisService redisService;

    private static final String REFRESH_PREFIX = "refresh:";

    public RefreshToken createRefreshToken(UUID userId, String deviceId, String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(refreshTokenProperties.getExpiryDays());

        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .expireAt(expiry)   // always comes from config, not user
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(token);
        redisService.save(REFRESH_PREFIX + userId, savedToken.getToken(), refreshTokenProperties.getExpiryDays(), TimeUnit.DAYS);
        return savedToken;
    }


}
