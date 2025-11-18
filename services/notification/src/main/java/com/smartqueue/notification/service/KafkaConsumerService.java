package com.smartqueue.notification.service;

import com.smartqueue.notification.entity.Notification;
import com.smartqueue.notification.event.EventType;
import com.smartqueue.notification.event.QueueEvent;
import com.smartqueue.notification.repository.NotificationRepository;
import com.smartqueue.notification.service.impl.WebSocketNotificationSender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationRepository notificationRepository;
    private final WebSocketNotificationSender notificationSender;

    @KafkaListener(topics = "${smartqueue.kafka.topic}",
                    groupId = "${smartqueue.kafka.consumer.group-id}",
                    containerFactory = "containerFactory")
    @Transactional
    public void handleEvent(ConsumerRecord<String, QueueEvent> consumerRecord) {
        QueueEvent event = consumerRecord.value();

        Notification notification = createNotification(event, consumerRecord.key());
        notification = notificationRepository.save(notification);
        notificationSender.sendNotificationToUser(notification);
    }

    private Notification createNotification(QueueEvent event, String userId) {
        String message = messageByType(event.getType());

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(event.getType());
        notification.setMessage(message);
        notification.setTimestamp(event.getTimestamp());

        return notification;
    }

    private String messageByType(EventType type) {
        String message = "";
        switch (type) {
            case JOINED -> message = "The user joined the queue";
            case CALLED -> message = "The user was just called";
            case SERVED -> message = "The user has been served";
        }
        return message;
    }

}
