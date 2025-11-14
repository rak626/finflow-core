package com.rakesh.finflow.transaction.dto;

import com.rakesh.finflow.common.entity.transaction.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class TransactionSearchRequest {
    private Instant startDate;
    private Instant endDate;
    private String category;
    private TransactionType type;
    private String sourceId;
    private String sourceName;
    private String keyword;        // fuzzy match on description or merchant
    private Map<String, Object> metadata;  // optional future use
}

