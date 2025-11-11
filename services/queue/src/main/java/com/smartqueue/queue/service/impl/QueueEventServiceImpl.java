package com.smartqueue.queue.service.impl;

import com.smartqueue.queue.entity.QueueEvent;
import com.smartqueue.queue.exception.QueueEventNotFoundException;
import com.smartqueue.queue.repository.QueueEventRepository;
import com.smartqueue.queue.service.QueueEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueueEventServiceImpl implements QueueEventService {

    private final QueueEventRepository queueEventRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markEventPublished(Long eventId) {
        QueueEvent saved = queueEventRepository.findById(eventId)
                .orElseThrow(() -> new QueueEventNotFoundException("QueueEvent with ID " + eventId + " not found!"));
        saved.setPublished(true);
        queueEventRepository.save(saved);
    }

}
