package com.rakesh.finflow.common.repo.userservice;

import com.rakesh.finflow.common.entity.userservice.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {


}
