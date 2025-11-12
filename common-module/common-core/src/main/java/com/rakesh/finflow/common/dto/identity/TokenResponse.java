package com.rakesh.finflow.common.dto.identity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rakesh.finflow.common.entity.identity.TokenStatus;
import lombok.Builder;
import lombok.Data;

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
    private String username;
}
