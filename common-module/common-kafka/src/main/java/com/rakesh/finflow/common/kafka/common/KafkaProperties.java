package com.rakesh.finflow.common.kafka.common;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaProperties {

    @Value("${kafka.topic.user:user}")
    private String userTopic;

    @Value("${kafka.consumer.group-id.user-group:user-group}")
    private String groupUser;

    @Value("${kafka.topic.transaction:transaction}")
    private String transactionTopic;

    @Value("${kafka.consumer.group-id.transaction-group:transaction-group}")
    private String groupTransaction;

}
