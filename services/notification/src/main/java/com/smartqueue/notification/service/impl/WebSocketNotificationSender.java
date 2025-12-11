package com.smartqueue.notification.service.impl;

import com.smartqueue.notification.entity.Notification;
import com.smartqueue.notification.service.NotificationSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationSender implements NotificationSenderService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotificationToUser(Notification notification) {
        String destination = "/queue/notifications";

        messagingTemplate.convertAndSendToUser(
                notification.getUserId(),
                destination,
                notification);

        log.info("Notification sent to user '{}' at destination '/user/{}/queue/notifications'",
                notification.getUserId(), notification.getUserId());
    }

}
