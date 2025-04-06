package com.example.task_management_system.controller.analytics;

import com.example.task_management_system.entity.User;
import com.example.task_management_system.service.analyticservice.AnalyticsService;
import com.example.task_management_system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    private final AnalyticsService analyticsService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        User currentUser = jwtUtil.getLoggedInUser();
        if (currentUser == null) {
            logger.warn("Unauthorized access attempt to analytics endpoint");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Fetching analytics for user ID: {}", currentUser.getId());

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("completionPercentage", analyticsService.getCompletionPercentage(currentUser.getId()));
        analytics.put("weeklyTrends", analyticsService.getWeeklyCompletionTrends(currentUser.getId()));
        analyticsService.getAverageCompletionTime(currentUser.getId()).ifPresent(
                avg -> analytics.put("averageCompletionTime", avg)
        );

        logger.debug("Analytics data prepared successfully for user ID: {}", currentUser.getId());
        return ResponseEntity.ok(analytics);
    }
}