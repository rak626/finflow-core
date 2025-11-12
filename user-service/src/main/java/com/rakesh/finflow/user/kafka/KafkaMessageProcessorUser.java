package com.rakesh.finflow.user.kafka;

import com.rakesh.finflow.common.dto.user.UserKafkaDto;
import com.rakesh.finflow.common.kafka.consumer.processor.KafkaMessageProcessor;
import com.rakesh.finflow.common.kafka.util.SerializationUtils;
import com.rakesh.finflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProcessorUser extends KafkaMessageProcessor {

    private final UserService userService;

    @Override
    protected void doProcess(byte[] message) {
        UserKafkaDto userKafkaDto;

        try {
            userKafkaDto = SerializationUtils.deserialize(message, UserKafkaDto.class);
            log.info("Processing received message of : {}", userKafkaDto.getUsername());
        } catch (Exception e) {
            log.error("Received bad message: {}", new String(message, Charset.defaultCharset()), e);
            throw e;
        }

        userService.consumeKafkaUserDataToDB(userKafkaDto);
    }
}
