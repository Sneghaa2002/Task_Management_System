package com.example.task_management_system.service.employee;

import com.example.task_management_system.dto.TaskDto;
import java.util.List;

public interface EmployeeService {

    // Get all tasks assigned to a specific employee using their user ID
    List<TaskDto> getTasksByUserId(Long userId);

    // Update the status of a task (like marking it as in-progress or completed)
    TaskDto updateTask(Long id, String status);
}
