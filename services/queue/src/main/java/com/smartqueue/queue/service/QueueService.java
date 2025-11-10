package com.smartqueue.queue.service;

import com.smartqueue.queue.dto.CreateQueueRequest;
import com.smartqueue.queue.dto.QueueDto;
import com.smartqueue.queue.dto.QueueEntryDto;

public interface QueueService {

    QueueEntryDto joinQueue(Long queueId, Long userId);

    QueueDto createQueue(CreateQueueRequest request);

}
