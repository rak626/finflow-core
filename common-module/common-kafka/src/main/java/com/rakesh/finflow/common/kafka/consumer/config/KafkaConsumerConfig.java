package com.rakesh.finflow.common.kafka.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

//@Profile({"local", "rsdev", "rsqa", "rsk8s"})
@Configuration
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final String bootstrapServers;
    private final String dlqTopic;
    private final int concurrency;

    public KafkaConsumerConfig(
            KafkaTemplate<String, byte[]> kafkaTemplate,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.consumer.dlq.topic:dlq}") String dlqTopic,
            @Value("${kafka.consumer.concurrency:1}") int concurrency) {
        this.kafkaTemplate = kafkaTemplate;
        this.bootstrapServers = bootstrapServers;
        this.dlqTopic = dlqTopic;
        this.concurrency = concurrency;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.VALUE_FUNCTION, FailedDeserializationInfo.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    @Bean
    public ConsumerFactory<String, byte[]> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, byte[]> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (consumerRecord, e) -> new org.apache.kafka.common.TopicPartition(dlqTopic, consumerRecord.partition()));

        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L)); // Retry up to 3 times with a 1-second backoff
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, byte[]> myKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency); // Number of threads (concurrent listeners)
        factory.setCommonErrorHandler(errorHandler(kafkaTemplate)); // Set the error handler
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Manual acknowledgment

        return factory;
    }
}

