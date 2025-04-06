package com.example.task_management_system.service.notification;

import com.example.task_management_system.entity.Notification;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.repository.NotificationRepository;
import com.example.task_management_system.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User testUser;
    private Task testTask;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Complete Project");
        testTask.setDescription("Finish all modules");
        testTask.setPriority("High");
        testTask.setUser(testUser);
        testTask.setDeadline(java.sql.Date.valueOf(LocalDate.now().plusDays(1)));

        testNotification = Notification.builder()
                .id(1L)
                .user(testUser)
                .message("Test notification")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createInAppNotification_ShouldCreateNotification() {
        // Arrange
        String message = "Test notification message";
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        notificationService.createInAppNotification(testUser, message);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void sendDeadlineReminderEmails_WithUpcomingTasks_ShouldSendEmails() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(taskRepository.findByDeadline(tomorrow)).thenReturn(Collections.singletonList(testTask));

        // Act
        notificationService.sendDeadlineReminderEmails();

        // Assert
        verify(taskRepository, times(1)).findByDeadline(tomorrow);

        // Verify email was sent by capturing the SimpleMailMessage
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertTrue(sentMessage.getSubject().contains("Deadline Reminder"));
        assertTrue(sentMessage.getText().contains("Reminder: Task 'Complete Project'"));
    }

    @Test
    void sendDeadlineReminderEmails_NoUpcomingTasks_ShouldNotSendEmails() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(taskRepository.findByDeadline(tomorrow)).thenReturn(Collections.emptyList());

        // Act
        notificationService.sendDeadlineReminderEmails();

        // Assert
        verify(taskRepository, times(1)).findByDeadline(tomorrow);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendDeadlineReminderEmails_WhenTaskHasNoUser_ShouldNotSendEmail() {
        // Arrange
        testTask.setUser(null);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        when(taskRepository.findByDeadline(tomorrow)).thenReturn(Collections.singletonList(testTask));

        // Act
        notificationService.sendDeadlineReminderEmails();

        // Assert
        verify(taskRepository, times(1)).findByDeadline(tomorrow);
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void getUserNotifications_ShouldReturnNotifications() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId()))
                .thenReturn(Collections.singletonList(testNotification));

        // Act
        List<Notification> notifications = notificationService.getUserNotifications(testUser.getId());

        // Assert
        assertEquals(1, notifications.size());
        assertEquals("Test notification", notifications.get(0).getMessage());
        verify(notificationRepository, times(1))
                .findByUserIdOrderByCreatedAtDesc(testUser.getId());
    }

    @Test
    void getUserNotifications_NoNotifications_ShouldReturnEmptyList() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId()))
                .thenReturn(Collections.emptyList());

        // Act
        List<Notification> notifications = notificationService.getUserNotifications(testUser.getId());

        // Assert
        assertTrue(notifications.isEmpty());
        verify(notificationRepository, times(1))
                .findByUserIdOrderByCreatedAtDesc(testUser.getId());
    }
}