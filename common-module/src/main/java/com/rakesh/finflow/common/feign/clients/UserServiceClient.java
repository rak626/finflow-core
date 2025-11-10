package com.rakesh.finflow.common.feign.clients;

import com.rakesh.finflow.common.entity.userservice.UserProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/user/api/v1/user")
public interface UserServiceClient {

    @PostMapping
    UserProfile createUser(@RequestBody UserProfile user);
}
