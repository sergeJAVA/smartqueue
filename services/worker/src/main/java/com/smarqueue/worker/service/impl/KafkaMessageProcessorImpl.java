package com.smarqueue.worker.service.impl;

import com.smarqueue.worker.constant.EntryStatus;
import com.smarqueue.worker.entity.QueueEntry;
import com.smarqueue.worker.entity.QueueEvent;
import com.smarqueue.worker.repository.QueueEntryRepository;
import com.smarqueue.worker.service.KafkaMessageProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProcessorImpl implements KafkaMessageProcessor {

    private final QueueEntryRepository queueEntryRepository;

    @Override
    @Transactional
    public void process(QueueEvent event) {
        processType(event);
    }

    private void processType(QueueEvent event) {
        switch (event.getType()) {
            case CALLED -> changeQueueEntryStatusAndWaitTime(event, EntryStatus.CALLED);
            case SERVED -> changeQueueEntryStatus(event, EntryStatus.SERVED);
            case JOINED -> log.info("The message received from Kafka is" +
                    " of the JOINED type and does not require processing.");
            default -> log.warn("Received a message with an unexpected type!");
        }
    }

    private void changeQueueEntryStatus(QueueEvent event, EntryStatus status) {
        Optional<QueueEntry> optionalEntry = queueEntryRepository.findById(event.getEntryId());
        optionalEntry.ifPresent(entry -> {
            entry.setStatus(status);
        });
    }

    private void changeQueueEntryStatusAndWaitTime(QueueEvent event, EntryStatus status) {
        Optional<QueueEntry> optionalEntry = queueEntryRepository.findById(event.getEntryId());
        optionalEntry.ifPresent(entry -> {
            entry.setStatus(status);
            long waitSeconds = ChronoUnit.SECONDS.between(entry.getJoinedAt(), Instant.now());
            entry.setEstimatedWaitTime(waitSeconds);
        });
    }

}
