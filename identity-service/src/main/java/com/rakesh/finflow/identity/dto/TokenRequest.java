package com.rakesh.finflow.identity.dto;

import lombok.Data;

@Data
public class TokenRequest {
    private String refreshToken;
    private String deviceId;
    private String ipAddress;
}
