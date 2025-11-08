package com.rakesh.finflow.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rakesh.finflow.identity.entity.TokenStatus;
import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Set<TokenStatus> status;
    private String error_message;
    private UUID userId;
}
