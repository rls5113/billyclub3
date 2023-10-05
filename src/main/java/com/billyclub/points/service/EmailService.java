package com.billyclub.points.service;

import com.billyclub.points.model.User;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface EmailService {
    public void sendForgotPasswordEmail(final String recipientName, final String recipientEmail, String link, final Locale locale) throws MessagingException;
    public void sendRegistrationEmail(List<User> admins, final String recipientEmail, String link, final Locale locale, final String recipientName) throws MessagingException;
    public void sendNewEventEmail(List<User> recipients, Map params, final Locale locale) throws MessagingException;
    public void sendMovedFromWaitlistEmail(List<User> recipients, Map params, final Locale locale) throws MessagingException;
    public void sendMovedToWaitlistEmail(List<User> recipients, Map params, final Locale locale) throws MessagingException;
    public void sendEventStatusChangedEmail(List<User> recipients, Map params, final Locale locale) throws MessagingException;
}
