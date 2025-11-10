package com.rakesh.finflow.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginRequest {
    private String username;
    private String password;
    private String deviceId;
    private String ipAddress;
}
