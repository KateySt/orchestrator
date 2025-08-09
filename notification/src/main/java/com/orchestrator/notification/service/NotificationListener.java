package com.orchestrator.notification.service;

import com.orchestrator.notification.model.EventPayload;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    @KafkaListener(topics = "event-topic", groupId = "notification-group")
    public void listen(EventPayload event) {
        System.out.println("Received event: " + event);
    }

}
