package com.example.task_management_system.service.analyticservice;

import com.example.task_management_system.entity.Task;
import com.example.task_management_system.enums.TaskStatus;
import com.example.task_management_system.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.*;


@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);
    private final TaskRepository taskRepository;

    @Override
    public double getCompletionPercentage(Long userId) {
        logger.debug("Calculating completion percentage for user: {}", userId);
        long totalTasks = taskRepository.countByUserId(userId);
        if (totalTasks == 0) {
            logger.debug("No tasks found for user: {}", userId);
            return 0.0;
        }

        long completedTasks = taskRepository.countByUserIdAndTaskStatus(userId, TaskStatus.COMPLETED);
        double percentage = Math.round((completedTasks * 100.0 / totalTasks) * 10) / 10.0;
        logger.debug("Completion percentage for user {}: {}%", userId, percentage);
        return percentage;
    }

    @Override
    public Map<String, Long> getWeeklyCompletionTrends(Long userId) {
        logger.debug("Generating weekly trends for user: {}", userId);

        // First get the completed tasks
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);

        List<Task> completedTasks = taskRepository.findByUserIdAndTaskStatusAndCompletedAtBetween(
                userId,
                TaskStatus.COMPLETED,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        // Initialize with all days of the week
        Map<String, Long> trends = new LinkedHashMap<>();
        String[] daysOfWeek = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

        // initialize all days with 0 count
        for (String day : daysOfWeek) {
            trends.put(day, 0L);
        }

        // count tasks per day
        for (Task task : completedTasks) {
            if (task.getCompletedAt() != null) {
                String day = task.getCompletedAt().getDayOfWeek().toString();
                trends.merge(day, 1L, Long::sum);
            }
        }

        return trends;
    }

    @Override
    public Optional<Double> getAverageCompletionTime(Long userId) {
        logger.debug("Calculating average completion time for user: {}", userId);
        List<Task> completedTasks = taskRepository.findByUserIdAndTaskStatus(userId, TaskStatus.COMPLETED);

        OptionalDouble average = completedTasks.stream()
                .filter(task -> task.getTimeEstimate() != null)
                .mapToInt(Task::getTimeEstimate)
                .average();

        return average.isPresent()
                ? Optional.of(average.getAsDouble())
                : Optional.empty();
    }
}