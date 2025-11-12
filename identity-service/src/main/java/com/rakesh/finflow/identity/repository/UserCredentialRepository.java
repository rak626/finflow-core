package com.rakesh.finflow.identity.repository;

import com.rakesh.finflow.identity.entity.UserCredential;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

    @Transactional
    @Modifying
    @Query("UPDATE UserCredential u SET u.failedLoginAttempt = 0 WHERE u.username = :username")
    void updateFailedLoginAttemptToZero(String username);

    @Transactional
    @Modifying
    @Query("UPDATE UserCredential u SET u.lastLoginTime = CURRENT_TIMESTAMP  WHERE u.username = :username")
    int updateLastLoginTime(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("UPDATE UserCredential u SET u.failedLoginAttempt = u.failedLoginAttempt + 1 WHERE u.username = :username")
    int updateFailedLoginAttempt(@Param("username") String username);

    @Transactional
    @Modifying
    @Query("UPDATE UserCredential u SET u.failedLoginAttempt = u.failedLoginAttempt + 1 , u.status = 'DISABLED', u.isUserLocked = true, u.isResetRequired = true WHERE u.username = :username")
    int updateFailedLoginAttemptAndStatus(@Param("username") String username);

    Optional<UserCredential> findByUsername(String username);

    boolean existsByUsername(String username);
}
