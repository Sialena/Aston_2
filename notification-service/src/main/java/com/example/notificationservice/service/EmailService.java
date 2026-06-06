package com.example.notificationservice.service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendUserNotification(String to, String operation) {
        String subject = "Уведомление от сервиса";
        String text;
        if ("CREATE".equalsIgnoreCase(operation)) {
            text = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
        } else if ("DELETE".equalsIgnoreCase(operation)) {
            text = "Здравствуйте! Ваш аккаунт был удалён.";
        } else {
            throw new IllegalArgumentException("Неизвестная операция: " + operation);
        }
        sendEmail(to, subject, text);
    }
}


