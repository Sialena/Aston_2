package com.example.notificationservice;

import com.example.notificationservice.dto.UserEvent;
import com.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"user-events"})
public class KafkaConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @MockBean
    private EmailService emailService;

    @Test
    void shouldConsumeUserEventAndSendEmail() throws Exception {
        UserEvent event = new UserEvent("CREATE", "user@example.com");
        kafkaTemplate.send("user-events", "key", event);

        verify(emailService, timeout(5000).times(1))
                .sendUserNotification("user@example.com", "CREATE");
    }
}