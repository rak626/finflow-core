package com.rakesh.finflow.user.service;

import com.rakesh.finflow.common.entity.userservice.UserProfile;
import com.rakesh.finflow.common.repo.userservice.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository userRepo;

    @Transactional
    public UserProfile createUser(UserProfile userProfile) {
        return userRepo.save(userProfile);
    }

    public List<UserProfile> getAllUser() {
        return userRepo.findAll();
    }

}
