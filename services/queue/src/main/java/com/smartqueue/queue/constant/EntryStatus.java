package com.smartqueue.queue.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntryStatus {

    WAITING("WAITING"),
    CALLED("CALLED"),
    SERVED("SERVED"),
    TIMEOUT("TIMEOUT");

    private final String name;

}
