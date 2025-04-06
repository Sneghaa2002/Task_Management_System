package com.example.task_management_system.service.analyticservice;

import java.util.Map;
import java.util.Optional;

public interface AnalyticsService {

        // Get the percentage of tasks completed by a specific user
        double getCompletionPercentage(Long userId);

        // Get a map showing how many tasks were completed by the user each day in the past week
        Map<String, Long> getWeeklyCompletionTrends(Long userId);

        // Get the average time taken by the user to complete tasks
        Optional<Double> getAverageCompletionTime(Long userId);
}
