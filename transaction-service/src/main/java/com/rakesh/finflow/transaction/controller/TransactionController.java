package com.rakesh.finflow.transaction.controller;

import com.rakesh.finflow.common.dto.transaction.TransactionDto;
import com.rakesh.finflow.transaction.dto.TransactionSearchRequest;
import com.rakesh.finflow.transaction.dto.TransactionSearchResponse;
import com.rakesh.finflow.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> addTransactions(@RequestBody List<TransactionDto> dtoList) {
        try {
            transactionService.addAllTransaction(dtoList);
            return ResponseEntity.ok("All Transactions added Successfully");
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTransaction() {
        try {
            return ResponseEntity.ok(transactionService.getAllTransaction());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public TransactionSearchResponse search(
            @RequestBody TransactionSearchRequest request,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortingOrder", required = false) String sortingOrder,
            @RequestHeader("user-profile-id") UUID userProfileId) {

        return transactionService.search(request, page, size, sortBy, sortingOrder, userProfileId);
    }


}
