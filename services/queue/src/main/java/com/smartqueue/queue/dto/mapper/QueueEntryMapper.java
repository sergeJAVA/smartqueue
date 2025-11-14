package com.smartqueue.queue.dto.mapper;

import com.smartqueue.queue.dto.QueueEntryDto;
import com.smartqueue.queue.entity.QueueEntry;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QueueEntryMapper {

    public static QueueEntryDto toDto(QueueEntry entry) {
        return QueueEntryDto.builder()
                .id(entry.getId())
                .status(entry.getStatus())
                .userId(entry.getUserId())
                .joinedAt(entry.getJoinedAt())
                .queueId(entry.getQueue().getId())
                .build();
    }

}
