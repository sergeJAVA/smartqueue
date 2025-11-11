package com.smartqueue.queue.repository;

import com.smartqueue.queue.entity.Queue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    @EntityGraph(attributePaths = {"entries"})
    Optional<Queue> findById(Long id);

}
