package com.rakesh.finflow.identity.security;

import com.rakesh.finflow.identity.entity.UserCredential;
import com.rakesh.finflow.identity.repository.UserCredentialRepository;
import com.rakesh.finflow.identity.utils.UserNameValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder encoder;
    private final UserCredentialRepository userCredentialRepository;
    @Value("${app.security.login-attempts:5}")
    private int allowedPasswordIncorrectCount;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder encoder, UserCredentialRepository userCredentialRepository) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
        this.userCredentialRepository = userCredentialRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        if (!UserNameValidator.userNameNotEmpty(username)) {
            throw new RuntimeException("Invalid username format");
        }
        String rawPassword = (String) authentication.getCredentials();
        UUID userId = UUID.randomUUID(); // default userId if not exists
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails instanceof UserCredential) {
                userId = ((UserCredential) userDetails).getId();
            } else {
                throw new RuntimeException("UserDetails Object is invalid");
            }

            if (encoder.matches(rawPassword, userDetails.getPassword())) {
                // todo: if matches do something on user table like make failed attempt to zero
                userCredentialRepository.updateFailedLoginAttemptToZero(userDetails.getUsername());
                return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
            } else {
                int failedAttemptCount = ((UserCredential) userDetails).getFailedLoginAttempt();

                if (failedAttemptCount >= (allowedPasswordIncorrectCount - 1)) {
                    // Lock the account if the failed attempts exceed the limit
                    userCredentialRepository.updateFailedLoginAttemptAndStatus(userDetails.getUsername());
                    throw new BadCredentialsException(
                            "Your account has been locked. Please contact support team.."
                    );
                } else {
                    // Increment failed attempts
                    userCredentialRepository.updateFailedLoginAttempt(userDetails.getUsername());
                    int remainingAttempts = allowedPasswordIncorrectCount - failedAttemptCount - 1;
                    throw new BadCredentialsException("Please provide a valid password. You have "
                            + remainingAttempts + (remainingAttempts > 1 ? " attempts" : " attempt") + " left");
                }
            }
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Incorrect username");
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
