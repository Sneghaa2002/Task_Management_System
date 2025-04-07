package com.example.task_management_system.controller.notification;

import com.example.task_management_system.entity.Notification;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.service.notification.NotificationService;
import com.example.task_management_system.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;
    private List<Notification> testNotifications;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUserRole(UserRole.EMPLOYEE);

        Notification notification1 = Notification.builder()
                .id(1L)
                .user(testUser)
                .message("Task 'Complete Project' status changed to COMPLETED")
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        Notification notification2 = Notification.builder()
                .id(2L)
                .user(testUser)
                .message("New task assigned: 'Review Documentation'")
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        testNotifications = Arrays.asList(notification1, notification2);
    }

    @Test
    void getUserNotifications_Success() {
        // Arrange
        when(jwtUtil.getLoggedInUser()).thenReturn(testUser);
        when(notificationService.getUserNotifications(testUser.getId())).thenReturn(testNotifications);

        ResponseEntity<List<Notification>> response = notificationController.getUserNotifications();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testNotifications, response.getBody());
        verify(jwtUtil, times(1)).getLoggedInUser();
        verify(notificationService, times(1)).getUserNotifications(testUser.getId());
    }

    @Test
    void getUserNotifications_EmptyList() {
        // Arrange
        when(jwtUtil.getLoggedInUser()).thenReturn(testUser);
        when(notificationService.getUserNotifications(testUser.getId())).thenReturn(List.of());

        ResponseEntity<List<Notification>> response = notificationController.getUserNotifications();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(jwtUtil, times(1)).getLoggedInUser();
        verify(notificationService, times(1)).getUserNotifications(testUser.getId());
    }

    @Test
    void getUserNotifications_UserNotLoggedIn() {
        // Arrange
        when(jwtUtil.getLoggedInUser()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> notificationController.getUserNotifications());
        verify(jwtUtil, times(1)).getLoggedInUser();
        verify(notificationService, never()).getUserNotifications(any());
    }
}
