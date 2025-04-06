package com.example.task_management_system.controller.admin;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        logger.info("Fetching all users");
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PostMapping("/task")
    public ResponseEntity<?> createTask(@RequestBody TaskDto taskDto) {
        logger.debug("creating new task: {}", taskDto.getTitle());
        TaskDto createdTaskDTO = adminService.createTask(taskDto);
        if (createdTaskDTO == null) {
            logger.warn("task creation failed for: {}", taskDto.getTitle());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        logger.info("task created successfully with ID: {}", createdTaskDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskDTO);
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTasks() {
        logger.info("Fetching all tasks");
        return ResponseEntity.ok(adminService.getAllTasks());
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("Deleting task with ID: {}", id);
        adminService.deleteTask(id);
        logger.debug("Task deleted successfully: {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        logger.debug("Fetching task by ID: {}", id);
        return ResponseEntity.ok(adminService.getTaskById(id));
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        logger.info("Updating task ID {} with data: {}", id, taskDto);
        TaskDto updatedTask = adminService.updateTask(id, taskDto);
        if (updatedTask == null) {
            logger.error("Task not found for update: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Task updated successfully: {}", id);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/tasks/search/{title}")
    public ResponseEntity<List<TaskDto>> searchTasksByTitle(@PathVariable String title) {
        logger.info("Searching tasks by title: {}", title);
        return ResponseEntity.ok(adminService.searchTasksByTitle(title));
    }

    @GetMapping("/tasks/filter/status/{status}")
    public ResponseEntity<List<TaskDto>> filterByStatus(@PathVariable TaskStatus status) {
        logger.debug("Filtering tasks by status: {}", status);
        return ResponseEntity.ok(adminService.filterTasksByStatus(status));
    }

    @GetMapping("/tasks/filter/priority/{priority}")
    public ResponseEntity<List<TaskDto>> filterByPriority(@PathVariable String priority) {
        logger.debug("Filtering tasks by priority: {}", priority);
        return ResponseEntity.ok(adminService.filterTasksByPriority(priority));
    }
}