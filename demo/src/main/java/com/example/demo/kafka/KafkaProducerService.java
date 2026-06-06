package com.example.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public KafkaProducerService(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    public void sendMessage(String operation, String email) {
        UserEvent event = new UserEvent(operation, email);
        kafkaTemplate.send(TOPIC, email, event);
        System.out.println("Отправлено событие в Kafka: " + operation + "- " + email);
    }
}