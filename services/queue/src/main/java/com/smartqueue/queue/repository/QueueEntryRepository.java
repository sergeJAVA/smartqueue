package com.smartqueue.queue.repository;

import com.smartqueue.queue.entity.QueueEntry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    @EntityGraph(attributePaths = {"queue"})
    Optional<QueueEntry> findByUserIdAndIsActiveTrueAndQueue_Id(Long userId, Long id);

}
