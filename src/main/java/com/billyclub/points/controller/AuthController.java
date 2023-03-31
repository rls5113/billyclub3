package com.billyclub.points.controller;

import com.billyclub.points.dto.UserDto;
import com.billyclub.points.exceptions.ResourceNotFoundException;
import com.billyclub.points.model.User;
import com.billyclub.points.service.EmailService;
import com.billyclub.points.service.UserService;
import com.billyclub.points.util.ServletUtility;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

@Controller
public class AuthController {

    private UserService userService;
    private EmailService emailService;

    @Autowired
    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }
    private JavaMailSender mailSender;

    @GetMapping("index")
    public String home(){
        return "index";
    }
    @GetMapping("/layout")
    public String layout(){
        return "layout";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existingEmail = userService.findByEmail(user.getEmail());
        if (existingEmail != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }
        if (result.hasErrors()) {

            model.addAttribute("user", user);
            return "register";
        }
        model.addAttribute("user", userService.addUser(user));
        return "redirect:/login?success";
    }
    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }
    @PostMapping("/forgot-password")
    public String postForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(45);
        try {
            User user = userService.updateResetPasswordToken(token,email);
            //generate pw reset link
            String link = ServletUtility.getSiteURL(request)+"/reset-password?token="+ token;
            //send email
            sendForgotPasswordEmail(user.getName(), email, link, request.getLocale());
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error",e.getMessage());
            return "forgot-password";
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error","Error while sending email" + e.getMessage());
            return "redirect:/forgot-password?error";
        }

        return "redirect:/forgot-password?success";
    }

    private void sendForgotPasswordEmail(String name, String email, String link, Locale locale) throws MessagingException, UnsupportedEncodingException {
        emailService.sendForgotPasswordEmail(name, email, link, locale );
    }
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@Param("token") String token, Model model) {
        if(!StringUtils.isEmpty(token)){
            try {
                User userToReset = userService.findByResetPasswordToken(token);
                model.addAttribute("token", token);

            } catch (ResourceNotFoundException e) {
                model.addAttribute("error", "Invalid reset password token");
                return "reset-password?error";
            }
        }
        model.addAttribute("loggedInUsername", SecurityContextHolder.getContext().getAuthentication().getName());

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String postResetPassword(HttpServletRequest request, Model model) {
        String password = request.getParameter("password");
        String token =  request.getParameter("token");
        String loggedInUsername =  request.getParameter("logged-in-username");

        if(!StringUtils.isEmpty(token)){
            try {
                User userToReset = userService.findByResetPasswordToken(token);
                userService.updatePassword(userToReset, password);
                model.addAttribute("name", userToReset.getName());
            }catch (ResourceNotFoundException e) {
                model.addAttribute("error", "Invalid reset password token");
                return "reset-password?error";
            }
        }else {
            try {
                User userToReset = userService.findByUsername(loggedInUsername);
                userService.updatePassword(userToReset, password);
            }catch (ResourceNotFoundException e) {
                model.addAttribute("error", "Invalid account. "+ e.getMessage());
                return "reset-password?error";
            }
        }

        return "redirect:/login?changepass";
    }
  }
