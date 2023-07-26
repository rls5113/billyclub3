package com.billyclub.points.controller;

import com.billyclub.points.dto.LoginDto;
import com.billyclub.points.dto.ResetPasswordDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

@Controller
public class AuthController {

    private UserService userService;
    private EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/index")
    public String home(){
        return "index";
    }
    @GetMapping("/layout")
    public String layout(){
        return "layout";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        LoginDto login = new LoginDto();
        model.addAttribute("login",login);
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               HttpServletRequest request,
                               Model model){
        log.info("Registering new user ...");
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        try {
            User existingEmail = userService.findByEmail(user.getEmail());
            if (existingEmail != null) {
                log.info("Register: Email exists");
                result.rejectValue("email", null, "There is already an account registered with that email");
            }

        }catch (ResourceNotFoundException e) {
            //expected
        }
        try {
            User existingUser = userService.findByUsername(user.getUsername());
            if (existingUser != null) {
                log.info("Register: Username exists");
                result.rejectValue("username", null, "There is already an account registered with that username");
            }

        }catch (ResourceNotFoundException e) {
            //expected
        }
        try {
            User existingUser = userService.findByFullname(user.getName());
            if (existingUser != null) {
                log.info("Register: First and Last Name exists");
                result.rejectValue("firstName", null, "There is already an account registered with this exact first and last name.");
                result.rejectValue("lastName", null, "There is already an account registered with this exact first and last name.");
            }

        }catch (ResourceNotFoundException e) {
            //expected
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        UserDto newUser = userService.addUser(user);
        model.addAttribute("user", newUser);
        log.info("Register: exit");
        try {
            //generate pw reset link
            String link = ServletUtility.getSiteURL(request)+"/login";
            List<User> admins = userService.findAdminUsers();
            //send email
            log.info("Register: send email");
            emailService.sendRegistrationEmail(admins, user.getEmail(), link, request.getLocale(), user.getName());
        } catch (ResourceNotFoundException e) {
            log.error("Register: resource not found");
            model.addAttribute("error",e.getMessage());
            return "register";
        } catch (Exception e) {
            log.error("Register: Email error!!");
            model.addAttribute("error","Error while sending email" + e.getMessage());
            return "register";
        }

        return "redirect:/login?success";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }
    @PostMapping("/forgot-password")
    public String postForgotPassword(HttpServletRequest request, Model model) {
        log.info("Forgot Password: enter");
        String email = request.getParameter("email");
        String token = RandomString.make(45);
        try {
            User user = userService.updateResetPasswordToken(token,email);
            //generate pw reset link
            String link = ServletUtility.getSiteURL(request)+"/reset-password?token="+ token;
            link = link.replace("forgot-password/","");
            //send email
            sendForgotPasswordEmail(user.getName(), email, link, request.getLocale());
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error",e.getMessage());
            log.error("Forgot Email failed, resource not found. "+ e.getStackTrace());
            return "forgot-password";
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Forgot Email failed. MessagingException " + e.getStackTrace());
            model.addAttribute("error","Error while sending email" + e.getMessage());
            return "redirect:/forgot-password?error";
        }
        log.info("Forgot Password: email sent, part 1 completed");
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
        ResetPasswordDto pwpair = new ResetPasswordDto();
        model.addAttribute("pwpair", pwpair);

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String postResetPassword(@Valid @ModelAttribute("pwpair") ResetPasswordDto dto,
                                    BindingResult result, Model model, HttpServletRequest request
                                    ) {
        log.info("Forgot Password: part 2 enter");
//        String password = request.getParameter("password");
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();
        if(! password.equals(confirmPassword)){
            result.rejectValue("password", null, "Password does NOT match confirmation.");
            return "reset-password";
        }
        String token =  request.getParameter("token");
        String loggedInUsername =  request.getParameter("logged-in-username");

        if(!StringUtils.isEmpty(token)){
            try {
                User userToReset = userService.findByResetPasswordToken(token);
                userService.updatePassword(userToReset, password);
                model.addAttribute("name", userToReset.getName());
            }catch (ResourceNotFoundException e) {
                result.rejectValue("token",null,"Invalid reset password token.");
//                model.addAttribute("error", "Invalid reset password token");
                return "reset-password";
            }
        }else {
            try {
                User userToReset = userService.findByUsername(loggedInUsername);
                userService.updatePassword(userToReset, password);
            }catch (ResourceNotFoundException e) {
                result.rejectValue("user",null,"User account could not be found.");
//                model.addAttribute("error", "Invalid account. "+ e.getMessage());
                return "reset-password";
            }
        }
        log.info("Forgot Password: part 2 exit");
        return "redirect:/login?changepass";
    }
  }
