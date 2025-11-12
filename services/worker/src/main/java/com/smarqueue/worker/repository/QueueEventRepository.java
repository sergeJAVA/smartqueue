package com.smarqueue.worker.repository;

import com.smarqueue.worker.entity.QueueEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEventRepository extends JpaRepository<QueueEvent, Long> {
}
