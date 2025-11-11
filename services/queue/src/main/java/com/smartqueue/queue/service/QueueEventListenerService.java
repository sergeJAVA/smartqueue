package com.smartqueue.queue.service;

import com.smartqueue.queue.config.kafka.KafkaProperties;
import com.smartqueue.queue.entity.QueueEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Service
@RequiredArgsConstructor
@Slf4j
public class QueueEventListenerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final QueueEventService eventService;
    private final KafkaProperties kafkaProperties;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional
    public void sendEvent(QueueEvent queueEvent) {
        kafkaTemplate.send(kafkaProperties.getKafkaGeneralTopic(), KafkaProperties.KAFKA_QUEUE_EVENT_KEY, queueEvent);

        eventService.markEventPublished(queueEvent.getId());
        log.info("QueueEvent with ID {} sent to kafka and marked published.", queueEvent.getId());
    }

}
