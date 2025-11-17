package com.smarqueue.worker.service.impl;

import com.smarqueue.worker.config.KafkaProperties;
import com.smarqueue.worker.constant.EntryStatus;
import com.smarqueue.worker.entity.QueueEntry;
import com.smarqueue.worker.entity.QueueEvent;
import com.smarqueue.worker.repository.QueueEntryRepository;
import com.smarqueue.worker.service.KafkaMessageProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProcessorImpl implements KafkaMessageProcessor {

    private final QueueEntryRepository queueEntryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public void process(QueueEvent event) {
        processType(event);
    }

    private void processType(QueueEvent event) {
        switch (event.getType()) {
            case CALLED -> changeQueueEntryStatusToCalled(event);
            case SERVED -> changeQueueEntryStatusToServed(event);
            case JOINED -> processJoinedStatus(event);
            default -> log.warn("Received a message with an unexpected type!");
        }
    }

    private void changeQueueEntryStatus(QueueEvent event, EntryStatus status) {
        Optional<QueueEntry> optionalEntry = queueEntryRepository.findById(event.getEntryId());
        optionalEntry.ifPresent(entry -> {
            entry.setStatus(status);

            String key = String.valueOf(entry.getUserId());
            kafkaTemplate.send(KafkaProperties.KAFKA_NOTIFICATION_TOPIC, key, event);
            log.info("The status of the entry has been changed to {} and notification-service has been notified.", key);
        });
    }

    private void changeQueueEntryStatusToCalled(QueueEvent event) {
        Optional<QueueEntry> optionalEntry = queueEntryRepository.findById(event.getEntryId());
        optionalEntry.ifPresent(entry -> {
            entry.setStatus(EntryStatus.CALLED);
            long waitSeconds = ChronoUnit.SECONDS.between(entry.getJoinedAt(), Instant.now());
            entry.setEstimatedWaitTime(waitSeconds);

            String key = String.valueOf(entry.getUserId());
            kafkaTemplate.send(KafkaProperties.KAFKA_NOTIFICATION_TOPIC, key, event);
            log.info("The status of the entry has been changed to {} and notification-service has been notified.", key);
        });
    }

    private void changeQueueEntryStatusToServed(QueueEvent event) {
        Optional<QueueEntry> optionalEntry = queueEntryRepository.findById(event.getEntryId());
        optionalEntry.ifPresent(entry -> {
            entry.setStatus(EntryStatus.SERVED);
            entry.setActive(false);

            String key = String.valueOf(entry.getUserId());
            kafkaTemplate.send(KafkaProperties.KAFKA_NOTIFICATION_TOPIC, key, event);
            log.info("The status of the entry has been changed to {} and notification-service has been notified.", key);
        });
    }

    private void processJoinedStatus(QueueEvent event) {
        Optional<QueueEntry> optionalEntry = queueEntryRepository.findById(event.getEntryId());
        optionalEntry.ifPresent(entry -> {
            String key = String.valueOf(entry.getUserId());
            kafkaTemplate.send(KafkaProperties.KAFKA_NOTIFICATION_TOPIC, key, event);
            log.info("The entry has been added to the queue and notification-service has been notified.");
        });
    }

}
