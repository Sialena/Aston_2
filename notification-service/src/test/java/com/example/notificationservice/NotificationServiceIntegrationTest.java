package com.example.notificationservice;

import com.example.notificationservice.dto.UserEvent;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class NotificationServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static GreenMail greenMail;

    @BeforeEach
    void setUp() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();

        System.setProperty("spring.mail.port", "3025");
        System.setProperty("spring.mail.host", "localhost");
        System.setProperty("spring.mail.username", "");
        System.setProperty("spring.mail.password", "");
    }

    @AfterEach
    void tearDown() {
        greenMail.stop();
    }

    @Test
    void shouldSendEmailViaRestApi() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/send-email?email=test@example.com&operation=CREATE",
                null,
                String.class
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Письмо отправлено");


        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(1);
        String content = greenMail.getReceivedMessages()[0].getContent().toString();
        assertThat(content).contains("аккаунт на сайте ваш сайт был успешно создан");
        assertThat(greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString()).isEqualTo("test@example.com");
    }
}