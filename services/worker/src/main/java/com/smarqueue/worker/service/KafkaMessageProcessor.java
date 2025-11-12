package com.smarqueue.worker.service;

import com.smarqueue.worker.entity.QueueEvent;

public interface KafkaMessageProcessor {

    void process(QueueEvent event);

}
