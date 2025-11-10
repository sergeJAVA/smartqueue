package com.smartqueue.queue.dto.mapper;

import com.smartqueue.queue.dto.QueueDto;
import com.smartqueue.queue.entity.Queue;
import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class QueueMapper {

    public static QueueDto toDto(Queue queue) {
        return QueueDto.builder()
                .id(queue.getId())
                .name(queue.getName())
                .createdAt(queue.getCreatedAt())
                .description(queue.getDescription())
                .entries(queue.getEntries().isEmpty()
                        ? Collections.emptyList() :
                        queue.getEntries().
                                stream()
                                .map(QueueEntryMapper::toDto)
                                .toList())
                .build();
    }

}
