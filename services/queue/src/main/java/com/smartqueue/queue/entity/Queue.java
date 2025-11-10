package com.smartqueue.queue.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "queues")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "max_slots", nullable = false)
    private Integer maxSlots;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QueueEntry> entries = new ArrayList<>();

}
