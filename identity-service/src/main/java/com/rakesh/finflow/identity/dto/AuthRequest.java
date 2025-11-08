package com.rakesh.finflow.identity.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthRequest {

    private UUID userId;
    private String deviceId;
    private String ipAddress;
}


