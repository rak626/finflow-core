package com.rakesh.finflow.identity.service;

import com.nimbusds.jose.JOSEException;
import com.rakesh.finflow.common.dto.identity.TokenResponse;
import com.rakesh.finflow.common.dto.user.UserKafkaDto;
import com.rakesh.finflow.common.entity.userservice.UserStatus;
import com.rakesh.finflow.common.kafka.common.KafkaProperties;
import com.rakesh.finflow.common.kafka.producer.MyKafkaProducer;
import com.rakesh.finflow.common.kafka.util.MessageType;
import com.rakesh.finflow.identity.dto.LoginRequest;
import com.rakesh.finflow.identity.dto.SignUpRequest;
import com.rakesh.finflow.identity.entity.UserCredential;
import com.rakesh.finflow.identity.repository.UserCredentialRepository;
import com.rakesh.finflow.util.common.UserProfileIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserCredentialRepository userCredentialRepository;
    private final MyKafkaProducer kafkaProducer;
    private final KafkaProperties kafkaProperties;


    public void addUser(SignUpRequest request) {
        // 1️⃣ Validate username availability
        if (userCredentialRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exits..");
        }
        log.debug("Generating User Profile ID for new user: {}", request.getUsername());
        String userProfileId = UserProfileIdGenerator.generate();
        UserCredential userCredential =
                UserCredential.builder()
                        .username(request.getUsername())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role("ROLE_USER")
                        .status(UserStatus.ACTIVE)
                        .userProfileId(userProfileId)
                        .build();
        UserCredential savedUserCredential = userCredentialRepository.save(userCredential);
        log.info("User Credential registered successfully with username: {}", savedUserCredential.getId());

        UserKafkaDto userKafkaDto = UserKafkaDto
                .builder()
                .userProfileId(userProfileId)
                .username(request.getUsername())
                .email(request.getEmail())
                .name(request.getName())
                .build();

        // call kafka to save
        kafkaProducer.send(kafkaProperties.getUserTopic(), userKafkaDto.getUsername(), userKafkaDto, MessageType.USER_DETAILS);
    }

    public TokenResponse login(LoginRequest request) throws JOSEException {
        // 1️⃣ Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2️⃣ Extract authenticated principal (your UserCredentials)
        UserCredential userCredential = (UserCredential) authentication.getPrincipal();

        // 3️⃣ Generate access and refresh tokens
        String accessToken = tokenService.generateAccessToken(userCredential, request);
        String refreshToken = refreshTokenService.createRefreshToken(userCredential, request.getDeviceId(), request.getIpAddress());

        // 4️⃣ Build response
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
//                    .tokenType("Bearer")
//                    .expiresIn(jwtService.getAccessTokenExpirySeconds())
                .username(userCredential.getUsername())
                .userId(userCredential.getId())
                .build();
    }
}
