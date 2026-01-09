package com.royalhalalmeats.royalmeats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Royal Halal Meats - Password Reset");
            message.setText(
                    "Assalamu Alaikum,\n\n"
                            + "You requested a password reset for your Royal Halal Meats account.\n"
                            + "Please click the link below to set a new password:\n\n"
                            + resetLink + "\n\n"
                            + "This link will expire in 15 minutes.\n\n"
                            + "If you did not request this, please ignore this email.\n\n"
                            + "— Royal Halal Meats Team"
            );
            mailSender.send(message);
            System.out.println("✅ Password reset email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
