package com.rakesh.finflow.common.kafka.schedular;

import com.rakesh.finflow.common.kafka.entity.FailedMessage;
import com.rakesh.finflow.common.kafka.producer.MyKafkaProducer;
import com.rakesh.finflow.common.kafka.repository.KafkaFailedMessageRepository;
import com.rakesh.finflow.util.common.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This FailedMessageRetryScheduler class creates a bean
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FailedMessageRetryScheduler {


    private KafkaFailedMessageRepository failedMessageRepository;

    @Scheduled(fixedDelay = 300000)  // Retry every 5 minutes
    public void retryFailedMessages() {
        log.debug("Retry scheduler in action, will try my best to resend all failed messages");
        List<FailedMessage> failedMessages = failedMessageRepository.findAll();

        for (FailedMessage failedMessage : failedMessages) {
            try {
                BeanUtil.getBean(MyKafkaProducer.class)
                        .send(failedMessage.getTopicName(), failedMessage.getMessageKey(), failedMessage.getMessage(),
                                failedMessage.getMessageType());
                failedMessageRepository.delete(failedMessage);  // Remove from DB after successful send
                log.info("Successfully sent failed message with key: {} for topic {}", failedMessage.getMessageKey(),
                        failedMessage.getTopicName());
            } catch (Exception e) {
                log.error("Failed to send failed message with key {} for topic {} on retry. Will retry again later.",
                        failedMessage.getMessageKey(), failedMessage.getTopicName(), e);
            }
        }
    }
}
