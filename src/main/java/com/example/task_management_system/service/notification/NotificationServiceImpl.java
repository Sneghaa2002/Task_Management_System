package com.example.task_management_system.service.notification;

import com.example.task_management_system.entity.Notification;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.repository.NotificationRepository;
import com.example.task_management_system.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public void createInAppNotification(User user, String message) {
        log.info("Creating in-app notification for user {}: {}", user.getId(), message);
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void sendDeadlineReminderEmails() {
        log.info("Starting deadline reminder email job");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Task> upcomingTasks = taskRepository.findByDeadline(tomorrow);

        upcomingTasks.forEach(task -> {
            if (task.getUser() != null) {
                try {
                    String message = String.format(
                            "Reminder: Task '%s' is due tomorrow%n%n" +
                                    "Description: %s%n" +
                                    "Priority: %s",
                            task.getTitle(),
                            task.getDescription(),
                            task.getPriority()
                    );

                    sendEmailNotification(
                            task.getUser().getEmail(),
                            "Deadline Reminder: " + task.getTitle(),
                            message
                    );
                } catch (Exception e) {
                    log.error("Error sending reminder for task {}: {}", task.getId(), e.getMessage());
                }
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private void sendEmailNotification(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}