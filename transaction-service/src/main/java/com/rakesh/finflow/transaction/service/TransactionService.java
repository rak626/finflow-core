package com.rakesh.finflow.transaction.service;

import com.rakesh.finflow.common.dto.transaction.TransactionDto;
import com.rakesh.finflow.common.kafka.common.KafkaProperties;
import com.rakesh.finflow.common.kafka.producer.MyKafkaProducer;
import com.rakesh.finflow.common.kafka.util.MessageType;
import com.rakesh.finflow.common.spec.SpecBuilder;
import com.rakesh.finflow.transaction.dto.TransactionSearchRequest;
import com.rakesh.finflow.transaction.dto.TransactionSearchResponse;
import com.rakesh.finflow.transaction.entity.Transaction;
import com.rakesh.finflow.transaction.repository.TransactionRepository;
import com.rakesh.finflow.transaction.validator.TransactionRequestValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaProperties kafkaProperties;
    private final MyKafkaProducer producer;
    private final TransactionRequestValidator validator;

    public void addTransaction(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction saved : [{}]", savedTransaction.getId());
    }

    public void consumeKafkaData(TransactionDto dto) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(dto, transaction);
        this.addTransaction(transaction);
    }

    public void addAllTransaction(List<TransactionDto> dtoList) {
        dtoList.forEach(dto -> {
            producer.send(kafkaProperties.getTransactionTopic(), String.valueOf(dto.getUserProfileId()), dto, MessageType.TRANSACTION_DETAILS);
        });
    }

    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    public TransactionSearchResponse search(TransactionSearchRequest req,
                                            int page, int size, String sortBy, String sortingOrder,
                                            UUID userProfileId) {

        validator.validateSortParams(sortBy, sortingOrder, req);
        // Build sort
        Sort sort;
        if (sortBy == null || sortBy.isBlank()) {
            sort = Sort.by("transactionAt").descending();
        } else {
            sort = "asc".equalsIgnoreCase(sortingOrder)
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Transaction> spec =
                SpecBuilder.of(Transaction.class)
                        .eq("userProfileId", userProfileId)
                        .eq("category", req.getCategory())
                        .eq("type", req.getType())
                        .eq("sourceId", req.getSourceId())
                        .eq("sourceName", req.getSourceName())
                        .like("description", req.getKeyword())
                        .betweenInstant("transactionAt", req.getStartDate(), req.getEndDate())
                        .build();

        Page<Transaction> pageResult = transactionRepository.findAll(spec, pageable);
        return this.toResponse(pageResult);
    }

    public TransactionSearchResponse toResponse(Page<Transaction> pageResult) {
        return TransactionSearchResponse.builder()
                .transactions(pageResult.getContent())
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .empty(pageResult.isEmpty())
                .build();
    }

}
