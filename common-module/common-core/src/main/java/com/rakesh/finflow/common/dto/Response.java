package com.rakesh.finflow.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Response {

    private RequestStatus status;
    private Object result;
    private Set<Error> errors = new HashSet<>();

    // TODO: COMPLETE THE  flow of common response class
}
