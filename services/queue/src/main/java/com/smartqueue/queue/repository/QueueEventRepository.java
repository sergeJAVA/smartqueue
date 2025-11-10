package com.smartqueue.queue.repository;

import com.smartqueue.queue.entity.QueueEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEventRepository extends JpaRepository<QueueEvent, Long> {
}
