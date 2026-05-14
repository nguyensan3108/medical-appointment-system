package com.healthcare.api.service.impl;

import com.healthcare.api.event.AppointmentBookedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "senderEmail", "hospital@gmail.com");
    }

    @Test
    void sendAppointmentConfirmation_Success_SendsMail(){
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        emailService.sendAppointmentConfirmation("patient@gmail.com", "Jackie", "2030-12-12", "07:00");

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("hospital@gmail.com", sentMessage.getFrom());
        assertEquals("patient@gmail.com", sentMessage.getTo()[0]);
        assertEquals("Appointment Confirmation", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Jackie"));
        assertTrue(sentMessage.getText().contains("2030-12-12"));
    }

    @Test
    void handleAppointmentBookedEvent_Success_TriggersMailSending(){
        AppointmentBookedEvent event = new AppointmentBookedEvent("patient@gmail.com", "Jackie", "2030-12-12", "07:00");
        emailService.handleAppointmentBookedEvent(event);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}