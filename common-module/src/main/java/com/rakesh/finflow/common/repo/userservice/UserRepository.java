package com.rakesh.finflow.common.repo.userservice;

import com.rakesh.finflow.common.entity.userservice.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

}
