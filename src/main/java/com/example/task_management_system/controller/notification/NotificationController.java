package com.example.task_management_system.controller.notification;

import com.example.task_management_system.entity.Notification;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.service.notification.NotificationService;
import com.example.task_management_system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications() {
        User user = jwtUtil.getLoggedInUser();
        logger.info("Fetching notifications for user ID: {}", user.getId());
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }
}