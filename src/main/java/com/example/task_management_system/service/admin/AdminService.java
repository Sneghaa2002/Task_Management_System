package com.example.task_management_system.service.admin;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.dto.UserDto;
import com.example.task_management_system.enums.TaskStatus;

import java.util.List;

public interface AdminService {

    // Get a list of all registered users
    List<UserDto> getUsers();

    // Create a new task and assign it
    TaskDto createTask(TaskDto taskDto);

    // Fetch all tasks in the system
    List<TaskDto> getAllTasks();

    // Remove a task by its ID
    void deleteTask(Long id);

    // Find a specific task using its ID
    TaskDto getTaskById(Long id);

    // Update task details by ID
    TaskDto updateTask(Long id, TaskDto taskDto);

    // Search tasks that match a given title
    List<TaskDto> searchTasksByTitle(String title);

    // Get tasks filtered by their current status (e.g., PENDING, COMPLETED)
    List<TaskDto> filterTasksByStatus(TaskStatus status);

    // Get tasks filtered by priority level (e.g., HIGH, MEDIUM, LOW)
    List<TaskDto> filterTasksByPriority(String priority);
}
