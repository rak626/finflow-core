package com.rakesh.finflow.transaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rakesh.finflow.transaction.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionSearchResponse {

    private List<Transaction> transactions;

    private int page;               // current page number (0-based)
    private int size;               // page size requested
    private long totalElements;     // total records matching search
    private int totalPages;         // total pages available
    private boolean first;          // is this the first page?
    private boolean last;           // is this the last page?
    private boolean empty;          // is this page empty?
}

