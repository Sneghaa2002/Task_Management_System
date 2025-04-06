package com.example.task_management_system.service.employee;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Override
    public List<TaskDto> getTasksByUserId(Long userId) {
        logger.info("Fetching tasks for user ID: {}", userId);
        return taskRepository.findByUserId(userId).stream()
                .map(Task::fromTask)
                .toList();
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long id, String status) {
        logger.info("Updating task {} status to: {}", id, status);
        TaskStatus newStatus = TaskStatus.valueOf(status.toUpperCase());
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found: {}", id);
                    return new EntityNotFoundException("Task doesn't exist");
                });

        TaskStatus oldStatus = task.getTaskStatus();

        task.setTaskStatus(newStatus);

        if (newStatus == TaskStatus.COMPLETED && oldStatus != TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (oldStatus == TaskStatus.COMPLETED && newStatus != TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
        }

        Task updatedTask = taskRepository.save(task);

        if (!oldStatus.equals(newStatus)) {
            notificationService.createInAppNotification(
                    task.getUser(),
                    String.format("Task '%s' status changed from %s to %s",
                            task.getTitle(),
                            oldStatus,
                            newStatus)
            );
        }

        return Task.fromTask(updatedTask);
    }
}