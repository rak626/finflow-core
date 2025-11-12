package com.rakesh.finflow.common.kafka.producer;

import com.rakesh.finflow.common.kafka.entity.FailedMessage;
import com.rakesh.finflow.common.kafka.repository.KafkaFailedMessageRepository;
import com.rakesh.finflow.common.kafka.util.MessageType;
import com.rakesh.finflow.common.kafka.util.SerializationUtils;
import org.slf4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface MyKafkaProducer {

    default void send(String topic, String key, Object message, MessageType type) {
        byte[] msg = SerializationUtils.serialize((Serializable) message);
        send(topic, key, msg, type);
    }

    void send(String topic, String key, byte[] message, MessageType type);

    default void persistFailedMessage(String topic, String key, byte[] message, MessageType type, Logger logger, KafkaFailedMessageRepository repository) {
        // Persist the message details to a database or file for later processing
        logger.info("Persisting failed message [{}] for topic {}", message, topic);
        FailedMessage entity = new FailedMessage();
        entity.setMessage(message);
        FailedMessage failedMessage = new FailedMessage();
        failedMessage.setTopicName(topic);
        failedMessage.setMessageKey(key);
        failedMessage.setMessage(message);
        failedMessage.setMessageType(type);
        failedMessage.setFailedAt(LocalDateTime.now());
        try {
            repository.save(entity);
            logger.info("Message persisted successfully for topic {}, message [{}]", topic, message);
        } catch (Exception e) {
            logger.error("Could not persist failed message with key: {} into topic: {}", key, topic);
        }
    }
}
