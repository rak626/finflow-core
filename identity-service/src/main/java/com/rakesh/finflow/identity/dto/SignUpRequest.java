package com.rakesh.finflow.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class SignUpRequest {

    //    private UUID userId;
    private String deviceId;
    private String ipAddress;
    private String username;
    private String password;
    private String email;
    private String name;
}


