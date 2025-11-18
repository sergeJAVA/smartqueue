package com.smartqueue.notification.service;

import com.smartqueue.notification.entity.Notification;

public interface NotificationSenderService {

    void sendNotificationToUser(Notification notification);

}
