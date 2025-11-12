package com.rakesh.finflow.common.kafka.producer;

import com.rakesh.finflow.common.kafka.repository.KafkaFailedMessageRepository;
import com.rakesh.finflow.common.kafka.schedular.FailedMessageRetryScheduler;
import com.rakesh.finflow.common.kafka.util.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//@Profile({"local", "rsdev", "rsqa", "rsk8s"})
@Service
@Slf4j
@RequiredArgsConstructor
public class MyKafkaProducerDefault implements MyKafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final KafkaFailedMessageRepository repository;
    private final FailedMessageRetryScheduler retryScheduler;

    @Override
    public void send(String topic, String key, byte[] message, MessageType type) {
        CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(topic, key, message);

        // Asynchronous callback to handle success or failure of message sending
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Message [{}] sent to topic {} partition {} with offset {}", key, metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                log.error("Failed to send message [{}] to topic {}. Retrying...", key, topic, ex);
                handleFailure(topic, key, message, type, ex);
            }
        });
    }

    private void handleFailure(String topic, String key, byte[] message, MessageType type, Throwable ex) {
        try {
            // Retry sending message synchronously
            kafkaTemplate.send(topic, key, message).get();
            log.info("Message [{}] successfully resent to topic {}", message, topic);
        } catch (InterruptedException | ExecutionException e) {
            // Handle persistent failures here, such as saving to a database, notifying a service, etc.
            log.error("Failed to resend message [{}] to topic {} after retrying. Message will be persisted for future retries or manual intervention.", message, topic, e);
            persistFailedMessage(topic, key, message, type, log, repository);
        }
    }
}
