package com.example.notificationservice.controller;

import com.example.notificationservice.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestParam String email,
                            @RequestParam String operation) {
        emailService.sendUserNotification(email, operation);
        return "Письмо отправлено на " + email + " с операцией " + operation;
    }
}