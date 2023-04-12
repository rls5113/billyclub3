package com.billyclub.points.service.impl;

import com.billyclub.points.model.Player;
import com.billyclub.points.model.User;
import com.billyclub.points.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendForgotPasswordEmail(String recipientName, String recipientEmail, String link, Locale locale) throws MessagingException {
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("resetDate", new Date());
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Points System: password reset link");
        message.setFrom("rls1893@yahoo.com");
        message.setTo(recipientEmail);
        final String content = this.templateEngine.process("email-forgot-password",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);
    }

    @Override
    public void sendNewEventEmail(List<User> recipients, String eventDate, String link, Locale locale) throws MessagingException {
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", eventDate);
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Points System: new event created");
        message.setFrom("rls1893@yahoo.com");
//        for(User user: recipients){
//            message.addTo(new InternetAddress(user.getEmail()));
//        }
        message.setTo("stuartrl@comcast.net");
        final String content = this.templateEngine.process("email-new-event",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }
    @Override
    public void sendMovedFromWaitlistEmail(List<User> recipients, String eventDate, String link, Locale locale) throws MessagingException {
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", eventDate);
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Points System: new event created");
        message.setFrom("rls1893@yahoo.com");
//        for(User user: recipients){
//            message.addTo(new InternetAddress(user.getEmail()));
//        }
        message.setTo("stuartrl@comcast.net");
        final String content = this.templateEngine.process("email-player-from-waitlist",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }
    @Override
    public void sendEventStatusChangedEmail(List<User> recipients, String eventStatus, String eventDate, String link, Locale locale) throws MessagingException {
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", eventDate);
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Points System: Event status changed to "+eventStatus);
        message.setFrom("rls1893@yahoo.com");
//        for(User user: recipients){
//            message.addTo(new InternetAddress(user.getEmail()));
//        }
        message.setTo("stuartrl@comcast.net");
        final String content = this.templateEngine.process("email-event-status-changed",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }
}
