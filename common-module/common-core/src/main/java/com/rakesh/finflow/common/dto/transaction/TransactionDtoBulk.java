package com.rakesh.finflow.common.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class TransactionDtoBulk implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<TransactionDto> transactionList;
}
