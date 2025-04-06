package com.example.task_management_system.controller.admin;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.dto.UserDto;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.service.admin.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private TaskDto taskDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setPriority("High");
        taskDto.setTaskStatus(TaskStatus.PENDING);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("admin@example.com");
        userDto.setUserRole(UserRole.ADMIN); // Changed from String to UserRole enum
    }

    // ✅ Test: Get All Users
    @Test
    void getUsers_Success() {
        List<UserDto> users = Collections.singletonList(userDto);
        when(adminService.getUsers()).thenReturn(users);

        ResponseEntity<?> response = adminController.getUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
        verify(adminService, times(1)).getUsers();
    }

    // ✅ Test: Create Task - Success
    @Test
    void createTask_Success() {
        when(adminService.createTask(taskDto)).thenReturn(taskDto);

        ResponseEntity<?> response = adminController.createTask(taskDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
        verify(adminService, times(1)).createTask(taskDto);
    }

    // ✅ Test: Create Task - Failure
    @Test
    void createTask_Failure() {
        when(adminService.createTask(taskDto)).thenReturn(null);

        ResponseEntity<?> response = adminController.createTask(taskDto);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(adminService, times(1)).createTask(taskDto);
    }

    // ✅ Test: Get All Tasks
    @Test
    void getAllTasks_Success() {
        List<TaskDto> tasks = Collections.singletonList(taskDto);
        when(adminService.getAllTasks()).thenReturn(tasks);

        ResponseEntity<?> response = adminController.getAllTasks();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
        verify(adminService, times(1)).getAllTasks();
    }

    // ✅ Test: Delete Task
    @Test
    void deleteTask_Success() {
        doNothing().when(adminService).deleteTask(1L);

        ResponseEntity<Void> response = adminController.deleteTask(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(adminService, times(1)).deleteTask(1L);
    }

    // ✅ Test: Get Task By ID - Success
    @Test
    void getTaskById_Success() {
        when(adminService.getTaskById(1L)).thenReturn(taskDto);

        ResponseEntity<?> response = adminController.getTaskById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
        verify(adminService, times(1)).getTaskById(1L);
    }

    // ✅ Test: Update Task - Success
    @Test
    void updateTask_Success() {
        when(adminService.updateTask(1L, taskDto)).thenReturn(taskDto);

        ResponseEntity<?> response = adminController.updateTask(1L, taskDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskDto, response.getBody());
        verify(adminService, times(1)).updateTask(1L, taskDto);
    }

    // ✅ Test: Update Task - Not Found
    @Test
    void updateTask_NotFound() {
        when(adminService.updateTask(1L, taskDto)).thenReturn(null);

        ResponseEntity<?> response = adminController.updateTask(1L, taskDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(adminService, times(1)).updateTask(1L, taskDto);
    }

    // ✅ Test: Search Tasks By Title
    @Test
    void searchTasksByTitle_Success() {
        List<TaskDto> tasks = Collections.singletonList(taskDto);
        when(adminService.searchTasksByTitle("Test")).thenReturn(tasks);

        ResponseEntity<List<TaskDto>> response = adminController.searchTasksByTitle("Test");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
        verify(adminService, times(1)).searchTasksByTitle("Test");
    }

    // ✅ Test: Filter By Status
    @Test
    void filterByStatus_Success() {
        List<TaskDto> tasks = Collections.singletonList(taskDto);
        when(adminService.filterTasksByStatus(TaskStatus.PENDING)).thenReturn(tasks);

        ResponseEntity<List<TaskDto>> response = adminController.filterByStatus(TaskStatus.PENDING);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
        verify(adminService, times(1)).filterTasksByStatus(TaskStatus.PENDING);
    }

    // ✅ Test: Filter By Priority
    @Test
    void filterByPriority_Success() {
        List<TaskDto> tasks = Collections.singletonList(taskDto);
        when(adminService.filterTasksByPriority("High")).thenReturn(tasks);

        ResponseEntity<List<TaskDto>> response = adminController.filterByPriority("High");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
        verify(adminService, times(1)).filterTasksByPriority("High");
    }
}