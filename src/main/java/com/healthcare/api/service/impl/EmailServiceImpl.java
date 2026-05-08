package com.healthcare.api.service.impl;

import com.healthcare.api.event.AppointmentBookedEvent;
import com.healthcare.api.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendAppointmentConfirmation(String toEmail, String patientName, String date, String time) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Appointment Confirmation");

        String context = String.format(
                "Welcome %s! Your appointment has been confirmed!\n" +
                        "Date: %s\n" +
                        "Time: %s",patientName, date, time
        );
        mailMessage.setText(context);
        mailSender.send(mailMessage);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleAppointmentBookedEvent(AppointmentBookedEvent event) {
        sendAppointmentConfirmation(
                event.email(),
                event.patientName(),
                event.date(),
                event.time()
        );
    }
}
