package com.billyclub.points.service.impl;

import com.billyclub.points.model.User;
import com.billyclub.points.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${from.email.address}")
    private String FROM_EMAIL_ADDRESS;
    @Value("${spring.mail.send}")
    private Boolean sendMail;

    @Autowired
    public EmailServiceImpl(ApplicationContext applicationContext, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.applicationContext = applicationContext;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendForgotPasswordEmail(String recipientName, String recipientEmail, String link, Locale locale) throws MessagingException {
        if(!sendMail)   {
            LOGGER.info("SEND EMAIL (Forgot Password) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("resetDate", new Date());
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Golf: password reset link");
        message.setFrom(FROM_EMAIL_ADDRESS);
        message.setTo(recipientEmail);
        final String content = this.templateEngine.process("email-forgot-password",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);
    }

    @Override
    public void sendRegistrationEmail(List<User> admins, String recipientEmail, String link, Locale locale, String recipientName) throws MessagingException {
        if(!sendMail)   {
            LOGGER.info("SEND EMAIL (Registration) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Golf: "+recipientName+" registration");
        message.setFrom(FROM_EMAIL_ADDRESS);
        message.setTo(recipientEmail);
        for(User user: admins){
            message.addTo(new InternetAddress(user.getEmail(),Boolean.TRUE));
        }

        final String content = this.templateEngine.process("email-registration",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }

    @Override
    public void sendNewEventEmail(List<User> recipients, String eventDate, String startTime, String course, String link, Locale locale) throws MessagingException {
        if(!sendMail)   {
            LOGGER.info("SEND EMAIL (new Event) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", eventDate);
        ctx.setVariable("startTime", startTime);
        ctx.setVariable("course", course);
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Golf: "+course+" on "+eventDate+" @ "+startTime+"  new event created");
        message.setFrom(FROM_EMAIL_ADDRESS);
        for(User user: recipients){
            message.addTo(new InternetAddress(user.getEmail(),Boolean.TRUE));
        }
        final String content = this.templateEngine.process("email-new-event",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }
    @Override
    public void sendMovedFromWaitlistEmail(List<User> recipients, String eventDate, String startTime, Locale locale, String link) throws MessagingException {
        if(!sendMail)   {
            LOGGER.info("SEND EMAIL (moved from waitlist) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", eventDate);
        ctx.setVariable("startTime", startTime);
        ctx.setVariable("link",link);

        for(User user: recipients){
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject("Billy Club Golf: "+eventDate+" "+startTime+" added from waiting list");
            message.setFrom(FROM_EMAIL_ADDRESS);
            message.addTo(new InternetAddress(user.getEmail(),Boolean.TRUE));
            ctx.setVariable("name",user.getName());
            final String content = this.templateEngine.process("email-player-from-waitlist",ctx);
            message.setText(content, true);
            this.mailSender.send(mimeMessage);
        }


    }
    @Override
    public void sendEventStatusChangedEmail(List<User> recipients, String eventStatus, String eventDate, String link, Locale locale) throws MessagingException {
        if(!sendMail)   {
            LOGGER.info("SEND EMAIL (event status changed) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", eventDate);
        ctx.setVariable("link",link);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Golf: Event status changed to "+eventStatus);
        message.setFrom(FROM_EMAIL_ADDRESS);
        for(User user: recipients){
            message.addTo(new InternetAddress(user.getEmail(), Boolean.TRUE));
        }
        String content = this.templateEngine.process("email-event-status-changed",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }
}
