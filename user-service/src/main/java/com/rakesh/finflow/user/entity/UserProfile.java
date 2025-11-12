package com.rakesh.finflow.user.entity;

import com.rakesh.finflow.common.entity.userservice.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Currency currency;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Todo: settings:(decide Later)


    @PrePersist
    public void setDefaultCurrency() {
        if (currency == null) {
            currency = Currency.RUPEE;
        }
    }
}