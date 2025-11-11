package com.smartqueue.queue.service.impl;

import com.smartqueue.queue.constant.EventType;
import com.smartqueue.queue.dto.CreateQueueRequest;
import com.smartqueue.queue.dto.QueueDto;
import com.smartqueue.queue.dto.QueueEntryDto;
import com.smartqueue.queue.dto.mapper.QueueEntryMapper;
import com.smartqueue.queue.dto.mapper.QueueMapper;
import com.smartqueue.queue.entity.Queue;
import com.smartqueue.queue.entity.QueueEntry;
import com.smartqueue.queue.entity.QueueEvent;
import com.smartqueue.queue.exception.QueueNotFoundException;
import com.smartqueue.queue.exception.UserAlreadyInQueueException;
import com.smartqueue.queue.repository.QueueEntryRepository;
import com.smartqueue.queue.repository.QueueEventRepository;
import com.smartqueue.queue.repository.QueueRepository;
import com.smartqueue.queue.service.QueueService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final QueueEntryRepository entryRepository;
    private final QueueEventRepository eventRepository;
    private final QueueRepository queueRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public QueueEntryDto joinQueue(Long queueId, Long userId) {
        QueueEntry savedEntry = saveNewQueueEntry(queueId, userId);
        QueueEvent savedEvent = saveNewQueueEvent(savedEntry);

        eventPublisher.publishEvent(savedEvent);

        return QueueEntryMapper.toDto(savedEntry);
    }

    @Override
    @Transactional
    public QueueDto createQueue(CreateQueueRequest request) {
        Queue queue = Queue.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Queue savedQueue = queueRepository.save(queue);
        return QueueMapper.toDto(savedQueue);
    }

    private QueueEntry saveNewQueueEntry(Long queueId, Long userId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new QueueNotFoundException("The queue with ID " + queueId + " not found!"));

        Optional<QueueEntry> optionalEntry = entryRepository.findByUserIdAndIsActiveTrueAndQueue_Id(userId, queueId);

        if (optionalEntry.isPresent()) {
            throw new UserAlreadyInQueueException("User with ID " + userId + " is already in the queue with ID " + queueId);
        }

        QueueEntry newQueueEntry = QueueEntry.builder()
                .userId(userId)
                .queue(queue)
                .build();

        return entryRepository.save(newQueueEntry);
    }

    private QueueEvent saveNewQueueEvent(QueueEntry entry) {
        QueueEvent event = QueueEvent.builder()
                .queueId(entry.getQueue().getId())
                .entryId(entry.getId())
                .type(EventType.JOINED)
                .build();

        return eventRepository.save(event);
    }

}
