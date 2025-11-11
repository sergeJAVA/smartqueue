package com.smartqueue.queue.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaProperties {

    public final static String KAFKA_QUEUE_EVENT_KEY = "event";

    @Value("${smartqueue.kafka.topic}")
    private String kafkaGeneralTopic;

    @Value("${smartqueue.kafka.bootstrap-server:localhost:9092}")
    private String kafkaBootstrapServer;

}
