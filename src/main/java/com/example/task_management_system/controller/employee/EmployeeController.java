package com.example.task_management_system.controller.employee;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    @GetMapping("/tasks/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByUserId(@PathVariable Long userId) {
        logger.info("Fetching tasks for user ID: {}", userId);
        return ResponseEntity.ok(employeeService.getTasksByUserId(userId));
    }

    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Updating task ID {} to status: {}", id, status);
        return ResponseEntity.ok(employeeService.updateTask(id, status));
    }
}