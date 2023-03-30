package com.billyclub.points.service;

import jakarta.mail.MessagingException;

import java.util.Locale;

public interface EmailService {
    public void sendForgotPasswordEmail(final String recipientName, final String recipientEmail, String link, final Locale locale) throws MessagingException;
}
