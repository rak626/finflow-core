package com.rakesh.finflow.common.kafka.consumer;

import com.rakesh.finflow.common.kafka.condition.ConditionUser;
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
@Conditional(ConditionUser.class)
@Getter
@Setter
@Slf4j
public class KafkaConsumerUser {


    private final KafkaMessageProcessor processor;
    private final String topicUser;

    public KafkaConsumerUser(
            @Qualifier("kafkaMessageProcessorUser") KafkaMessageProcessor processor,
            @Value("${kafka.topic.user}") String topicUser) {
        this.processor = processor;
        this.topicUser = topicUser;
    }

    @KafkaListener(
            topics = {"#{__listener.topicUser}"},
            groupId = "${kafka.consumer.group-id.user-group}",
            containerFactory = "myKafkaListenerContainerFactory"
    )
    public void listen(byte[] message, Acknowledgment acknowledgment) {
        try {
            log.info("User consumer received message from topic: {}", topicUser);
            processor.process(message);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process message", e);
        }
    }
}
