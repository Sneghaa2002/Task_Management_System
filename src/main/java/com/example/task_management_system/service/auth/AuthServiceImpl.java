package com.example.task_management_system.service.auth;

import com.example.task_management_system.dto.SignupRequest;
import com.example.task_management_system.dto.UserDto;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.notification.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final NotificationService notificationService;

    @PostConstruct
    public void createAdminAccount() {
        Optional<User> existingAdmin = userRepository.findByUserRole(UserRole.ADMIN);
        if (existingAdmin.isEmpty()) {
            logger.info("Creating initial admin account");
            User admin = new User();
            admin.setName("Sangeetha");
            admin.setEmail("sneghaa.aanandan@gmail.com");
            admin.setPassword(passwordEncoder.encode("SANG123"));
            admin.setUserRole(UserRole.ADMIN);
            userRepository.save(admin);
            logger.info("Admin account created successfully");
        }
    }

    @Override
    public UserDto signupUser(SignupRequest signupRequest) {
        logger.info("Registering new user: {}", signupRequest.getEmail());
        if (hasUserWithEmail(signupRequest.getEmail())) {
            logger.warn("Email already registered: {}", signupRequest.getEmail());
            throw new IllegalArgumentException("Email already in use");
        }

        User newUser = new User();
        newUser.setName(signupRequest.getName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setUserRole(UserRole.EMPLOYEE);

        User createdUser = userRepository.save(newUser);
        logger.info("User registered successfully with ID: {}", createdUser.getId());

        sendRegistrationEmail(createdUser);
        return createdUser.getUserDto();
    }

    private void sendRegistrationEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Task Management System");
            message.setText(String.format(
                    "Dear %s,\n\nuou have been successfully registered as an employee in our Task Management System.\n\n" +
                            "Your login email: %s\n\nRegards,\nTask Management Team",
                    user.getName(),
                    user.getEmail()
            ));
            mailSender.send(message);
        } catch (MailException e) {
            logger.error("Failed to send registration email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}