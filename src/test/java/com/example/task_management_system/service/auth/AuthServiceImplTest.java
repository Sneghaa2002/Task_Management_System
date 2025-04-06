package com.example.task_management_system.service.auth;

import com.example.task_management_system.dto.SignupRequest;
import com.example.task_management_system.dto.UserDto;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
    }

    @Test
    void createAdminAccount_ShouldCreateAdminIfNotExists() {
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        authService.createAdminAccount();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createAdminAccount_ShouldNotCreateAdminIfExists() {
        User admin = new User();
        admin.setUserRole(UserRole.ADMIN);
        when(userRepository.findByUserRole(UserRole.ADMIN)).thenReturn(Optional.of(admin));

        authService.createAdminAccount();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signupUser_ShouldCreateNewUser() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = authService.signupUser(signupRequest);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(UserRole.EMPLOYEE, result.getUserRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signupUser_ShouldThrowWhenEmailExists() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.signupUser(signupRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void hasUserWithEmail_ShouldReturnTrueWhenEmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertTrue(authService.hasUserWithEmail("test@example.com"));
    }

    @Test
    void hasUserWithEmail_ShouldReturnFalseWhenEmailNotExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertFalse(authService.hasUserWithEmail("test@example.com"));
    }
}