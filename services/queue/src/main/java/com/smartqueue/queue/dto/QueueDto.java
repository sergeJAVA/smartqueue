package com.smartqueue.queue.dto;

import com.smartqueue.queue.entity.QueueEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueDto {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime createdAt;

    @Builder.Default
    private List<QueueEntryDto> entries = new ArrayList<>();

}
