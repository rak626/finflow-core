package com.rakesh.finflow.user.service;

import com.rakesh.finflow.common.dto.user.UserKafkaDto;
import com.rakesh.finflow.user.entity.UserProfile;
import com.rakesh.finflow.user.repo.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository userProfileRepo;

    @Transactional
    public UserProfile createUser(UserProfile userProfile) {
        return userProfileRepo.save(userProfile);
    }

    public List<UserProfile> getAllUser() {
        return userProfileRepo.findAll();
    }

    public void consumeKafkaUserDataToDB(UserKafkaDto userKafkaDto) {
        UserProfile userProfile = new UserProfile();
        BeanUtils.copyProperties(userKafkaDto, userProfile);
        userProfileRepo.save(userProfile);
    }
}
