package com.smarqueue.worker.repository;

import com.smarqueue.worker.entity.QueueEntry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

    @EntityGraph(attributePaths = {"queue"})
    Optional<QueueEntry> findByUserIdAndIsActiveTrueAndQueue_Id(Long userId, Long id);

}
