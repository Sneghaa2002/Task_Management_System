package com.example.task_management_system.controller.employee;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.service.employee.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setTaskStatus(TaskStatus.PENDING);
        taskDto.setEmployeeId(1L);
    }

    @Test
    void getTasksByUserId_Success() {
        // Arrange
        Long userId = 1L;
        List<TaskDto> tasks = Arrays.asList(taskDto);
        when(employeeService.getTasksByUserId(userId)).thenReturn(tasks);

        // Act
        ResponseEntity<List<TaskDto>> response = employeeController.getTasksByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(tasks, response.getBody());
        verify(employeeService, times(1)).getTasksByUserId(userId);
    }

    @Test
    void updateTaskStatus_Success() {
        // Arrange
        Long taskId = 1L;
        String newStatus = "COMPLETED";
        when(employeeService.updateTask(taskId, newStatus)).thenReturn(taskDto);

        // Act
        ResponseEntity<TaskDto> response = employeeController.updateTaskStatus(taskId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(taskDto, response.getBody());
        verify(employeeService, times(1)).updateTask(taskId, newStatus);
    }

    /*@Test
    void updateTaskStatus_InvalidStatus() {
        // Arrange
        Long taskId = 1L;
        String invalidStatus = "INVALID_STATUS";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            employeeController.updateTaskStatus(taskId, invalidStatus);
        });

        verify(employeeService, never()).updateTask(any(), any());
    }*/

    @Test
    void updateTaskStatus_PendingToCompleted() {
        // Arrange
        Long taskId = 1L;
        String newStatus = "COMPLETED";
        TaskDto completedTask = new TaskDto();
        completedTask.setId(taskId);
        completedTask.setTaskStatus(TaskStatus.COMPLETED);

        when(employeeService.updateTask(taskId, newStatus)).thenReturn(completedTask);

        // Act
        ResponseEntity<TaskDto> response = employeeController.updateTaskStatus(taskId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(TaskStatus.COMPLETED, response.getBody().getTaskStatus());
        verify(employeeService, times(1)).updateTask(taskId, newStatus);
    }

    @Test
    void updateTaskStatus_CompletedToPending() {
        // Arrange
        Long taskId = 1L;
        String newStatus = "PENDING";
        TaskDto pendingTask = new TaskDto();
        pendingTask.setId(taskId);
        pendingTask.setTaskStatus(TaskStatus.PENDING);

        when(employeeService.updateTask(taskId, newStatus)).thenReturn(pendingTask);

        // Act
        ResponseEntity<TaskDto> response = employeeController.updateTaskStatus(taskId, newStatus);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(TaskStatus.PENDING, response.getBody().getTaskStatus());
        verify(employeeService, times(1)).updateTask(taskId, newStatus);
    }
}