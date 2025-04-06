package com.example.task_management_system.service.notification;

import com.example.task_management_system.entity.Notification;
import com.example.task_management_system.entity.User;

import java.util.List;

public interface NotificationService {

    // Creates an in-app notification for a specific user with the given message
    void createInAppNotification(User user, String message);

    // Sends email reminders to users about upcoming or missed task deadlines
    void sendDeadlineReminderEmails();

    // Retrieves all notifications for a specific user based on their user ID
    List<Notification> getUserNotifications(Long userId);
}
