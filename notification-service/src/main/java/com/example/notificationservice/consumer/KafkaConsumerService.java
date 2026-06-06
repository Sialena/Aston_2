package com.example.notificationservice.consumer;

import com.example.notificationservice.dto.UserEvent;
import com.example.notificationservice.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final EmailService emailService;

    public KafkaConsumerService(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(UserEvent event) {
        System.out.println("Получено событие из Kafka: " + event.getOperation() + " -> " + event.getEmail());
        emailService.sendUserNotification(event.getEmail(), event.getOperation());
    }
}