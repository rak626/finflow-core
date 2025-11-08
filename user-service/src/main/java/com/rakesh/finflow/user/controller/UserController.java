package com.rakesh.finflow.user.controller;

import com.rakesh.finflow.common.entity.userservice.User;
import com.rakesh.finflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@RequestHeader("Authorization") String authHeader, @RequestHeader("userId") UUID userId, @RequestBody User user) {
        return userService.createUser(user);
    }
}
