package com.smarqueue.worker.repository;

import com.smarqueue.worker.entity.Queue;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    @EntityGraph(attributePaths = {"entries"})
    Optional<Queue> findById(Long id);

}
