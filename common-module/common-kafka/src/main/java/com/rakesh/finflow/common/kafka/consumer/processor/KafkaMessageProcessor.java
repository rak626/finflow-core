package com.rakesh.finflow.common.kafka.consumer.processor;

public abstract class KafkaMessageProcessor {

    public void process(byte[] message) {
        doProcess(message);
    }

    protected abstract void doProcess(byte[] message);
}
