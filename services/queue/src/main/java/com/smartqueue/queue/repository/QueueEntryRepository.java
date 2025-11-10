package com.smartqueue.queue.repository;

import com.smartqueue.queue.entity.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
}
