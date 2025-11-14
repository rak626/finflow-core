package com.rakesh.finflow.common.dto.transaction;

import com.rakesh.finflow.common.entity.transaction.SourceType;
import com.rakesh.finflow.common.entity.transaction.PaymentMethod;
import com.rakesh.finflow.common.entity.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class TransactionDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userProfileId;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private SourceType sourceType;
    private String sourceId;
    private String sourceName;
    private String description;
    private PaymentMethod paymentMethod;
    private Map<String, Object> metadata;
    private String sentTo;
    private String receivedFrom;
    private Instant transactionAt;

}
