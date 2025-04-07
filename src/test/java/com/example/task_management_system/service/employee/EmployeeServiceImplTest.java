package com.example.task_management_system.service.employee;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.repository.TaskRepository;
import com.example.task_management_system.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority("High");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setUser(user);
    }

    @Test
    void getTasksByUserId_ShouldReturnTasks() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(task));

        List<TaskDto> result = employeeService.getTasksByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByUserId(1L);
    }

    @Test
    void updateTask_ShouldUpdateStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        TaskDto result = employeeService.updateTask(1L, "COMPLETED");

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, task.getTaskStatus());
        assertNotNull(task.getCompletedAt());
        verify(notificationService, times(1)).createInAppNotification(eq(user), anyString());
    }

    @Test
    void updateTask_ShouldThrowWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.updateTask(1L, "COMPLETED"));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_ShouldThrowWhenInvalidStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateTask(1L, "INVALID_STATUS"));
        verify(taskRepository, never()).save(any());
    }
}
