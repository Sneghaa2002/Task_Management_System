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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User employee;
    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    void setUp() {
        employee = new User();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john@example.com");
        employee.setUserRole(UserRole.EMPLOYEE);

        task = new Task();
        task.setId(1L);
        task.setTitle("Complete project");
        task.setDescription("Finish all modules");
        task.setPriority("High");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setUser(employee);

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Complete project");
        taskDto.setDescription("Finish all modules");
        taskDto.setPriority("High");
        taskDto.setTaskStatus(TaskStatus.PENDING);
        taskDto.setEmployeeId(1L);
    }

    @Test
    void getUsers_ShouldReturnEmployeeList() {
        // Arrange
        User admin = new User();
        admin.setId(2L);
        admin.setUserRole(UserRole.ADMIN);

        when(userRepository.findAll()).thenReturn(Arrays.asList(employee, admin));

        // Act
        List<UserDto> result = adminService.getUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createTask_ShouldCreateTaskSuccessfully() {
        // Arrange
        when(userRepository.findById(taskDto.getEmployeeId())).thenReturn(Optional.of(employee));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = adminService.createTask(taskDto);

        // Assert
        assertNotNull(result);
        assertEquals("Complete project", result.getTitle());
        verify(userRepository, times(1)).findById(taskDto.getEmployeeId());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(notificationService, times(1)).createInAppNotification(eq(employee), anyString());
    }

    @Test
    void createTask_ShouldThrowExceptionWhenEmployeeNotFound() {
        // Arrange
        when(userRepository.findById(taskDto.getEmployeeId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> adminService.createTask(taskDto));
        verify(userRepository, times(1)).findById(taskDto.getEmployeeId());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void getAllTasks_ShouldReturnAllTasksSortedByDeadline() {
        // Arrange
        Task oldTask = new Task();
        oldTask.setDeadline(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        Task newTask = new Task();
        newTask.setDeadline(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow

        when(taskRepository.findAll()).thenReturn(Arrays.asList(oldTask, newTask));

        // Act
        List<TaskDto> result = adminService.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0).getDeadline().after(result.get(1).getDeadline()));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void deleteTask_ShouldDeleteExistingTask() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);

        // Act
        adminService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_ShouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> adminService.deleteTask(1L));
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, never()).deleteById(any());
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        TaskDto result = adminService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Complete project", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_ShouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> adminService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @Transactional
    void updateTask_ShouldUpdateTaskSuccessfully() {
        // Arrange
        TaskDto updatedDto = new TaskDto();
        updatedDto.setTitle("Updated title");
        updatedDto.setDescription("Updated description");
        updatedDto.setPriority("Low");
        updatedDto.setTaskStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = adminService.updateTask(1L, updatedDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated title", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
        verify(notificationService, times(1)).createInAppNotification(eq(employee), anyString());
    }

    @Test
    void searchTasksByTitle_ShouldReturnMatchingTasks() {
        // Arrange
        when(taskRepository.findByTitleContainingIgnoreCase("project"))
                .thenReturn(Collections.singletonList(task));
        List<TaskDto> result = adminService.searchTasksByTitle("project");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Complete project", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByTitleContainingIgnoreCase("project");
    }

    @Test
    void searchTasksByTitle_ShouldReturnEmptyListForEmptySearch() {
        List<TaskDto> result = adminService.searchTasksByTitle("");

        // Assert
        assertTrue(result.isEmpty());
        verify(taskRepository, never()).findByTitleContainingIgnoreCase(any());
    }

    @Test
    void filterTasksByStatus_ShouldReturnFilteredTasks() {
        // Arrange
        when(taskRepository.findByTaskStatus(TaskStatus.PENDING))
                .thenReturn(Collections.singletonList(task));

        // Act
        List<TaskDto> result = adminService.filterTasksByStatus(TaskStatus.PENDING);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TaskStatus.PENDING, result.get(0).getTaskStatus());
        verify(taskRepository, times(1)).findByTaskStatus(TaskStatus.PENDING);
    }

    @Test
    void filterTasksByPriority_ShouldReturnFilteredTasks() {
        // Arrange
        when(taskRepository.findByPriority("High"))
                .thenReturn(Collections.singletonList(task));

        // Act
        List<TaskDto> result = adminService.filterTasksByPriority("High");

        // Assert
        assertEquals(1, result.size());
        assertEquals("High", result.get(0).getPriority());
        verify(taskRepository, times(1)).findByPriority("High");
    }
}
