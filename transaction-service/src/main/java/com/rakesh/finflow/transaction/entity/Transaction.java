package com.rakesh.finflow.transaction.entity;

import com.rakesh.finflow.common.entity.transaction.PaymentMethod;
import com.rakesh.finflow.common.entity.transaction.SourceType;
import com.rakesh.finflow.common.entity.transaction.TransactionType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    // ----------------------------------------------------------------------
    // Core identifiers
    // ----------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_profile_id", nullable = false, updatable = false)
    private String userProfileId;

    // ----------------------------------------------------------------------
    // Transaction attributes
    // ----------------------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType type;

    @Column(length = 100, updatable = false)
    private String category;

    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    // ----------------------------------------------------------------------
    // Source information
    // ----------------------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, updatable = false, length = 50)
    private SourceType sourceType;

    @Column(name = "source_id", length = 200, updatable = false)
    private String sourceId;

    @Column(name = "source_name", length = 200, nullable = false, updatable = false)
    private String sourceName;

    @Column(length = 500)
    private String description;

    // ----------------------------------------------------------------------
    // Payment method & metadata
    // ----------------------------------------------------------------------

    @Enumerated(EnumType.STRING)
    @Column(name = "spent_method", nullable = false, updatable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    // ----------------------------------------------------------------------
    // Flow info (optional / external receivers)
    // ----------------------------------------------------------------------

    @Column(name = "sent_to", length = 200, updatable = false)
    private String sentTo;

    @Column(name = "received_from", length = 200, updatable = false)
    private String receivedFrom;

    // ----------------------------------------------------------------------
    // Timestamps
    // ----------------------------------------------------------------------

    @Column(name = "transaction_at", nullable = false, updatable = false)
    private Instant transactionAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
