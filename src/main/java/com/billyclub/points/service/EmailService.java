package com.billyclub.points.service;

import com.billyclub.points.model.User;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Locale;

public interface EmailService {
    public void sendForgotPasswordEmail(final String recipientName, final String recipientEmail, String link, final Locale locale) throws MessagingException;
    public void sendNewEventEmail(List<User> recipients, String eventDate, String link, final Locale locale) throws MessagingException;
}
