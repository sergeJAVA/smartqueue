package com.smartqueue.queue.service.impl;

import com.smartqueue.queue.entity.QueueEvent;
import com.smartqueue.queue.exception.QueueEventNotFoundException;
import com.smartqueue.queue.repository.QueueEventRepository;
import com.smartqueue.queue.service.QueueEventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueEventServiceImpl implements QueueEventService {

    private final QueueEventRepository queueEventRepository;

    @Override
    @Transactional
    public void markEventPublished(Long eventId) {
        QueueEvent saved = queueEventRepository.findById(eventId)
                .orElseThrow(() -> new QueueEventNotFoundException("QueueEvent with ID " + eventId + " not found!"));
        saved.setPublished(true);
        queueEventRepository.save(saved);
    }

}
