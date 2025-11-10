package com.smartqueue.queue.dto;

import com.smartqueue.queue.constant.EntryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueEntryDto {

    private Long id;

    private Long userId;

    private EntryStatus status;

    private Instant joinedAt;

    private Long estimatedWaitTime;

}
