package com.smartqueue.queue.repository;

import com.smartqueue.queue.entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueRepository extends JpaRepository<Queue, Long> {
}
