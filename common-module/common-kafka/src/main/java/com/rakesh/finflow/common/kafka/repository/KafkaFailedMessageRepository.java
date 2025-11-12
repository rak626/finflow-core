package com.rakesh.finflow.common.kafka.repository;

import com.rakesh.finflow.common.kafka.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KafkaFailedMessageRepository extends JpaRepository<FailedMessage, Long> {

}