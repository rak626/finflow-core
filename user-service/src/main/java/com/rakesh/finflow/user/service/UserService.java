package com.rakesh.finflow.user.service;

import com.rakesh.finflow.common.entity.userservice.User;
import com.rakesh.finflow.common.repo.userservice.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    @Transactional
    public User createUser(User user) {
        return userRepo.save(user);
    }
}
