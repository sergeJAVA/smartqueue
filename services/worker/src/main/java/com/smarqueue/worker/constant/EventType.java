package com.smarqueue.worker.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventType {

    JOINED("JOINED"),
    CALLED("CALLED"),
    SERVED("SERVED"),
    LEFT("LEFT"),
    TIMEOUT("TIMEOUT");

    private final String name;

}
