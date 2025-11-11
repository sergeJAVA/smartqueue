package com.smartqueue.queue.entity;

import com.smartqueue.queue.constant.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "queue_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QueueEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EventType type;

    @Column(name = "event_timestamp", nullable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Column(name = "queue_id", nullable = false)
    private Long queueId;

    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "published", nullable = false)
    @Builder.Default
    private boolean published = false;

}
