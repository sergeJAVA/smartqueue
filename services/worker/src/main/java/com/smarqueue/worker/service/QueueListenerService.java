package com.smarqueue.worker.service;

import com.smarqueue.worker.entity.QueueEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueListenerService {

    private final KafkaMessageProcessor kafkaMessageProcessor;

    @KafkaListener(topics = "${smartqueue.kafka.topic}",
                    groupId = "${smartqueue.kafka.consumer.group-id}",
                    containerFactory = "containerFactory")
    public void handle(ConsumerRecord<String, QueueEvent> consumerRecord) {
        QueueEvent value = consumerRecord.value();
        log.info("QueueListener received a message from Kafka: {}", value);
        kafkaMessageProcessor.process(value);
    }

}
