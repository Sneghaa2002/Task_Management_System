package com.example.task_management_system.service.analyticservice;

import com.example.task_management_system.entity.Task;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private final Long userId = 1L;
    private Task completedTask1;
    private Task completedTask2;

    @BeforeEach
    void setUp() {
        completedTask1 = new Task();
        completedTask1.setId(1L);
        completedTask1.setTaskStatus(TaskStatus.COMPLETED);
        completedTask1.setCompletedAt(LocalDateTime.now().minusDays(2));
        completedTask1.setTimeEstimate(3);

        completedTask2 = new Task();
        completedTask2.setId(2L);
        completedTask2.setTaskStatus(TaskStatus.COMPLETED);
        completedTask2.setCompletedAt(LocalDateTime.now().minusDays(3));
        completedTask2.setTimeEstimate(5);
    }

    @Test
    void getCompletionPercentage_ShouldReturnCorrectPercentage() {
        when(taskRepository.countByUserId(userId)).thenReturn(5L);
        when(taskRepository.countByUserIdAndTaskStatus(userId, TaskStatus.COMPLETED)).thenReturn(2L);

        double percentage = analyticsService.getCompletionPercentage(userId);

        assertEquals(40.0, percentage);
        verify(taskRepository).countByUserId(userId);
        verify(taskRepository).countByUserIdAndTaskStatus(userId, TaskStatus.COMPLETED);
    }

    @Test
    void getCompletionPercentage_WhenNoTasks_ShouldReturnZero() {
        when(taskRepository.countByUserId(userId)).thenReturn(0L);

        double percentage = analyticsService.getCompletionPercentage(userId);

        assertEquals(0.0, percentage);
        verify(taskRepository).countByUserId(userId);
    }

    @Test
    void getWeeklyCompletionTrends_ShouldReturnTrends() {
        List<Task> completedTasks = Arrays.asList(completedTask1, completedTask2);
        LocalDate now = LocalDate.now();

        when(taskRepository.findByUserIdAndTaskStatusAndCompletedAtBetween(
                eq(userId),
                eq(TaskStatus.COMPLETED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(completedTasks);

        Map<String, Long> trends = analyticsService.getWeeklyCompletionTrends(userId);

        assertEquals(2, trends.size());
        assertTrue(trends.containsKey(completedTask1.getCompletedAt().getDayOfWeek().toString()));
        assertTrue(trends.containsKey(completedTask2.getCompletedAt().getDayOfWeek().toString()));
        verify(taskRepository).findByUserIdAndTaskStatusAndCompletedAtBetween(
                eq(userId),
                eq(TaskStatus.COMPLETED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }

    @Test
    void getAverageCompletionTime_ShouldReturnAverage() {
        List<Task> completedTasks = Arrays.asList(completedTask1, completedTask2);
        when(taskRepository.findByUserIdAndTaskStatus(userId, TaskStatus.COMPLETED))
                .thenReturn(completedTasks);

        Optional<Double> average = analyticsService.getAverageCompletionTime(userId);

        assertTrue(average.isPresent());
        assertEquals(4.0, average.get());
    }

    @Test
    void getAverageCompletionTime_NoEstimates_ShouldReturnEmpty() {
        Task taskWithoutEstimate = new Task();
        taskWithoutEstimate.setTaskStatus(TaskStatus.COMPLETED);

        when(taskRepository.findByUserIdAndTaskStatus(userId, TaskStatus.COMPLETED))
                .thenReturn(List.of(taskWithoutEstimate));

        Optional<Double> average = analyticsService.getAverageCompletionTime(userId);

        assertTrue(average.isEmpty());
    }
}
