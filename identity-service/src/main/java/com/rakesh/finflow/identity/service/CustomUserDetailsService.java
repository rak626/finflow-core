package com.rakesh.finflow.identity.service;

import com.rakesh.finflow.identity.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userCredentialRepository.findByUsername(username).orElseThrow(() -> {
            log.info("User not find.. ");
            return new UsernameNotFoundException("Unable to find UserId");
        });
    }
}
