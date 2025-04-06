package com.example.task_management_system.service.admin;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.dto.UserDto;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    @Override
    public List<UserDto> getUsers() {
        logger.info("Fetching list of all employees");
        List<UserDto> employees = userRepository.findAll().stream()
                .filter(user -> user.getUserRole() == UserRole.EMPLOYEE)
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
        logger.debug("Found {} employees", employees.size());
        return employees;
    }

    @Override
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        logger.info("Creating new task: {}", taskDto.getTitle());
        User assignedUser = userRepository.findById(taskDto.getEmployeeId())
                .orElseThrow(() -> {
                    logger.warn("Employee not found with ID: {}", taskDto.getEmployeeId());
                    return new EntityNotFoundException("Employee doesn't exist");
                });

       /* Task newTask = new Task();
        newTask.setTitle(taskDto.getTitle());
        newTask.setDescription(taskDto.getDescription());
        newTask.setPriority(taskDto.getPriority());
        newTask.setDeadline(taskDto.getDeadline());
        newTask.setTaskStatus(TaskStatus.PENDING);
        newTask.setUser(assignedUser);*/

        Task newTask = new Task();
        newTask.setTitle(taskDto.getTitle());
        newTask.setDescription(taskDto.getDescription());
        newTask.setPriority(taskDto.getPriority());
        newTask.setDeadline(taskDto.getDeadline());
        newTask.setTaskStatus(TaskStatus.PENDING);
        newTask.setUser(assignedUser);
        newTask.setCreatedDate(new Date());

        Task savedTask = taskRepository.save(newTask);
        logger.info("Created task ID {} for employee {}", savedTask.getId(), assignedUser.getId());

        notificationService.createInAppNotification(
                assignedUser,
                "New task assigned: " + taskDto.getTitle()
        );

        return Task.fromTask(savedTask);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        logger.info("Fetching all tasks");
        return taskRepository.findAll().stream()
                .sorted(Comparator.comparing(Task::getDeadline).reversed())
                .map(Task::fromTask)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        logger.info("deleting task ID: {}", id);
        if (!taskRepository.existsById(id)) {
            logger.warn("task not found for deletion: {}", id);
            throw new EntityNotFoundException("Task doesn't exist");
        }
        taskRepository.deleteById(id);
        logger.info("task {} deleted successfully", id);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        logger.debug("Looking up task ID: {}", id);
        return taskRepository.findById(id)
                .map(task -> {
                    logger.debug("Found task {}", id);
                    return Task.fromTask(task);
                })
                .orElseThrow(() -> {
                    logger.warn("Task not found: {}", id);
                    return new EntityNotFoundException("Task doesn't exist");
                });
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        logger.info("Updating task ID: {}", id);
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found for update: {}", id);
                    return new EntityNotFoundException("Task doesn't exist");
                });

        String oldTitle = existingTask.getTitle();
        TaskStatus oldStatus = existingTask.getTaskStatus();

        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setDeadline(taskDto.getDeadline());
        existingTask.setPriority(taskDto.getPriority());
        existingTask.setTaskStatus(taskDto.getTaskStatus());

        Task updatedTask = taskRepository.save(existingTask);
        logger.info("Task {} updated successfully", id);


        if (!oldTitle.equals(taskDto.getTitle())) {
            notificationService.createInAppNotification(
                    existingTask.getUser(),
                    String.format("Task renamed from '%s' to '%s'", oldTitle, taskDto.getTitle())
            );
        }

        if (!oldStatus.equals(taskDto.getTaskStatus())) {
            notificationService.createInAppNotification(
                    existingTask.getUser(),
                    String.format("Status changed from %s to %s", oldStatus, taskDto.getTaskStatus())
            );
        }

        return Task.fromTask(updatedTask);
    }

    @Override
    public List<TaskDto> searchTasksByTitle(String title) {
        logger.info("Searching tasks with title: {}", title);
        if (title == null || title.trim().isEmpty()) {
            logger.debug("Empty search term provided");
            return Collections.emptyList();
        }

        return taskRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(Task::fromTask)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> filterTasksByStatus(TaskStatus status) {
        logger.info("Filtering tasks by status: {}", status);
        return taskRepository.findByTaskStatus(status).stream()
                .map(Task::fromTask)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> filterTasksByPriority(String priority) {
        logger.info("Filtering tasks by priority: {}", priority);
        return taskRepository.findByPriority(priority).stream()
                .map(Task::fromTask)
                .collect(Collectors.toList());
    }
}
