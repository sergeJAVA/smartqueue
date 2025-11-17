package com.smartqueue.notification.event;

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
public class QueueEvent {

    private Long id;

    private EventType type;

    private Instant timestamp;

    private Long queueId;

    private Long entryId;

    @Builder.Default
    private boolean published = false;

}
