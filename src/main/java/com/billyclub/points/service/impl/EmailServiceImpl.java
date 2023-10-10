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

import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
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
            log.info("SEND EMAIL (Forgot Password) is turned OFF!");
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
        message.setBcc("stuartrl@comcast.net");
        final String content = this.templateEngine.process("email-forgot-password",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);
    }

    @Override
    public void sendRegistrationEmail(List<User> admins, String recipientEmail, String link, Locale locale, String recipientName) throws MessagingException {
        if(!sendMail)   {
            log.info("SEND EMAIL (Registration) is turned OFF!");
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
    public void sendNewEventEmail(List<User> recipients, Map params, Locale locale) throws MessagingException {
        if(!sendMail)   {
            log.info("SEND EMAIL (new Event) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        String eventDate = (String) params.get("eventDate");
        ctx.setVariable("eventDate", eventDate);
        String startTime = (String) params.get("startTime");
        ctx.setVariable("startTime", startTime);
        String course = (String) params.get("course");
        ctx.setVariable("course", course);
        ctx.setVariable("link", params.get("link"));

        for(User user: recipients){
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject("Billy Club Golf: "+course+" on "+ eventDate +" @ "+startTime+"  new event created");
            message.setFrom(FROM_EMAIL_ADDRESS);
            message.addTo(new InternetAddress(user.getEmail(),Boolean.TRUE));
            final String content = this.templateEngine.process("email-new-event",ctx);
            message.setText(content,true);
            this.mailSender.send(mimeMessage);
        }

    }
    @Override
    public void sendMovedFromWaitlistEmail(List<User> recipients, Map params, Locale locale) throws MessagingException {
        if(!sendMail)   {
            log.info("SEND EMAIL (moved from waitlist) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        String eventDate = (String) params.get("eventDate");
        ctx.setVariable("eventDate", eventDate);
        String startTime = (String) params.get("startTime");
        ctx.setVariable("startTime", startTime);
        ctx.setVariable("link", params.get("link"));

        for(User user: recipients){
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject("Billy Club Golf: "+ eventDate +" "+startTime+" added FROM waiting list");
            message.setFrom(FROM_EMAIL_ADDRESS);
            message.addTo(new InternetAddress(user.getEmail(),Boolean.TRUE));
            message.setBcc("stuartrl@comcast.net");

            ctx.setVariable("name",user.getName());
            final String content = this.templateEngine.process("email-player-from-waitlist",ctx);
            message.setText(content, true);
            this.mailSender.send(mimeMessage);
        }


    }
    @Override
    public void sendMovedToWaitlistEmail(List<User> recipients, Map params, Locale locale) throws MessagingException {
        if(!sendMail)   {
            log.info("SEND EMAIL (moved from waitlist) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", params.get("eventDate"));
        String startTime = (String) params.get("startTime");
        ctx.setVariable("startTime", startTime);
        ctx.setVariable("link", params.get("link"));


        for(User user: recipients){
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject("Billy Club Golf: "+ params +" "+startTime+" added TO waiting list");
            message.setFrom(FROM_EMAIL_ADDRESS);
            message.addTo(new InternetAddress(user.getEmail(),Boolean.TRUE));
            message.setBcc("stuartrl@comcast.net");

            ctx.setVariable("name",user.getName());
            final String content = this.templateEngine.process("email-player-to-waitlist",ctx);
            message.setText(content, true);
            if(!sendMail) {
                this.mailSender.send(mimeMessage);
            }
        }


    }
    @Override
    public void sendEventStatusChangedEmail(List<User> recipients, Map params, Locale locale) throws MessagingException {
        if(!sendMail)   {
            log.info("SEND EMAIL (event status changed) is turned OFF!");
            return;
        }
        final Context ctx = new Context(locale);
        ctx.setVariable("eventDate", params.get("eventDate"));
        ctx.setVariable("link", params.get("link"));

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Billy Club Golf: Event status changed to "+ params.get("eventStatus"));
        message.setFrom(FROM_EMAIL_ADDRESS);
        for(User user: recipients){
            message.addTo(new InternetAddress(user.getEmail(), Boolean.TRUE));
        }
        message.setBcc("stuartrl@comcast.net");

        String content = this.templateEngine.process("email-event-status-changed",ctx);
        message.setText(content,true);
        this.mailSender.send(mimeMessage);

    }
}
