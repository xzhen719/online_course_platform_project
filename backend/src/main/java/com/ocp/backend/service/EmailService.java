package com.ocp.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        // Adjust this URL to match your Frontend Route
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        String subject = "<<iKnowVation>>重設密碼信";
        String htmlBody = "<h3>重新設定密碼</h3>"
                + "<p>親愛的會員，您好:</p>"
                + "<p>請點擊下方連結重新設定密碼:</p>"
                + "<a href=\"" + resetLink + "\">" + resetLink + "</a>"
                + "<p>請注意!此連結將於15分鐘後失效。</p>";

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
            // Don't throw exception to avoid breaking the user flow
        }
    }
}
