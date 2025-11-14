package com.rakesh.finflow.common.kafka.consumer;

import com.rakesh.finflow.common.kafka.condition.ConditionTransaction;
import com.rakesh.finflow.common.kafka.consumer.processor.KafkaMessageProcessor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Conditional(ConditionTransaction.class)
@Getter
@Setter
@Slf4j
public class KafkaConsumerTransaction {


    private final KafkaMessageProcessor processor;
    private final String topicTransaction;

    public KafkaConsumerTransaction(
            @Qualifier("kafkaMessageProcessorTransaction") KafkaMessageProcessor processor,
            @Value("${kafka.topic.transaction}") String topicTransaction) {
        this.processor = processor;
        this.topicTransaction = topicTransaction;
    }

    @KafkaListener(
            topics = {"#{__listener.topicTransaction}"},
            groupId = "${kafka.consumer.group-id.transaction-group}",
            containerFactory = "myKafkaListenerContainerFactory"
    )
    public void listen(byte[] message, Acknowledgment acknowledgment) {
        try {
            log.info("Transaction consumer received message from topic: {}", topicTransaction);
            processor.process(message);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process message", e);
        }
    }
}
