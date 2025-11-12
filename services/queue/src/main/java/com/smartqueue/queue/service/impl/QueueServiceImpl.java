package com.smartqueue.queue.service.impl;

import com.smartqueue.queue.constant.EntryStatus;
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
import com.smartqueue.queue.service.QueueEventListenerService;
import com.smartqueue.queue.service.QueueService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueServiceImpl implements QueueService {

    private final QueueEntryRepository entryRepository;
    private final QueueEventRepository eventRepository;
    private final QueueRepository queueRepository;
    private final QueueEventListenerService queueEventListenerService;

    @Override
    @Transactional
    public QueueEntryDto joinQueue(Long queueId, Long userId) {
        QueueEntry savedEntry = saveNewQueueEntry(queueId, userId);
        QueueEvent savedEvent = saveNewQueueEvent(savedEntry);

        queueEventListenerService.sendEvent(savedEvent);
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

    @Override
    @Transactional
    public QueueEntryDto callNext(Long queueId) {
        QueueEntry queueEntry = entryRepository.findFirstByQueue_IdAndIsActiveTrueAndStatusOrderByJoinedAtAsc(queueId, EntryStatus.WAITING)
                .orElseThrow(() -> new QueueNotFoundException("Queue with ID " + queueId + " was not found" +
                        " or there are no active entries in the queue."));
        QueueEvent event = saveCalledQueueEvent(queueEntry);
        queueEventListenerService.sendEvent(event);
        return QueueEntryMapper.toDto(queueEntry);
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

    private QueueEvent saveCalledQueueEvent(QueueEntry entry) {
        QueueEvent event = QueueEvent.builder()
                .queueId(entry.getQueue().getId())
                .entryId(entry.getId())
                .type(EventType.CALLED)
                .build();

        return eventRepository.save(event);
    }

}
