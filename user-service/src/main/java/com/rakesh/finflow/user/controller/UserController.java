package com.rakesh.finflow.user.controller;

import com.rakesh.finflow.user.entity.UserProfile;
import com.rakesh.finflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserProfile createUser(@RequestBody UserProfile user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<UserProfile> getAllUsers() {
        return userService.getAllUser();
    }

//    @GetMapping("/exits/{username}")
//    boolean existsByUsername(@PathVariable("username") String username) {
//        return userService.isExistsByUsername(username);
//    }

//    @GetMapping("/{username}")
//    public User getUser
}
