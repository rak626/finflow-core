package com.rakesh.finflow.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    private String deviceId;

    @Column(nullable = false, updatable = false)
    private String token;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expireAt;  // set only in service layer

    private String ipAddress;

    @PrePersist
    private void generateToken() {
        this.token = UUID.randomUUID().toString();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expireAt);
    }
}
