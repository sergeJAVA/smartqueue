package com.smartqueue.notification.config.kafka;

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

    @Value("${smartqueue.kafka.topic}")
    private String kafkaGeneralTopic;

    @Value("${smartqueue.kafka.consumer.group-id}")
    private String kafkaConsumerGroupId;

    @Value("${smartqueue.kafka.bootstrap-server:localhost:9092}")
    private String kafkaBootstrapServer;

}
