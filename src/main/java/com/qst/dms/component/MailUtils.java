package com.qst.dms.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {
    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(to);
        message.setSubject(title);
        message.setText(text);
        javaMailSender.send(message);
    }
}
