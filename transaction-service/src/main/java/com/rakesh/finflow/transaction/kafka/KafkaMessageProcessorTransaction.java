package com.rakesh.finflow.transaction.kafka;

import com.rakesh.finflow.common.dto.transaction.TransactionDto;
import com.rakesh.finflow.common.kafka.consumer.processor.KafkaMessageProcessor;
import com.rakesh.finflow.common.kafka.util.SerializationUtils;
import com.rakesh.finflow.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProcessorTransaction extends KafkaMessageProcessor {

    private final TransactionService transactionService;

    @Override
    protected void doProcess(byte[] message) {
        TransactionDto dto;
        try {
            dto = SerializationUtils.deserialize(message, TransactionDto.class);
            log.info("Processing received Transaction message of : {}", dto.getUserProfileId());
        } catch (Exception e) {
            log.error("Received bad message: {}", new String(message, Charset.defaultCharset()), e);
            throw e;
        }

        transactionService.consumeKafkaData(dto);
    }
}
