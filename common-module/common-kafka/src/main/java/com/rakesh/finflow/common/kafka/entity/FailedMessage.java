package com.rakesh.finflow.common.kafka.entity;

import com.rakesh.finflow.common.kafka.util.MessageType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "kafka_failed_message")
@Data
public class FailedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "message_key")
    private String messageKey;
    @Column(name = "topic_name")
    private String topicName;
    //at which end message is failed SEND or RECEIVE
    @Column(name = "last_retry_time")
    private LocalDateTime lastRetryTime;
    @Column(name = "retry_count")
    private int retryCount;
    private byte[] message;
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;
    @Column(name = "failed_at")
    private LocalDateTime failedAt = LocalDateTime.now();

}