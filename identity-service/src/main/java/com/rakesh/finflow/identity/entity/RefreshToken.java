package com.rakesh.finflow.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private String username;

    @Column(nullable = false, updatable = false)
    private String deviceId;

    @Column(nullable = false, updatable = false)
    private String tokenHash;

    @Column(nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefreshTokenStatus status;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRevoked = Boolean.FALSE;


    @CreationTimestamp
    private LocalDateTime createdAt;

    private String ipAddress;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
