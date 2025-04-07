package com.example.task_management_system.controller.analytics;

import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.service.analyticservice.AnalyticsService;
import com.example.task_management_system.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AnalyticsController analyticsController;

    private User testUser;
    private Map<String, Object> testAnalytics;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@example.com");
        testUser.setUserRole(UserRole.ADMIN);

        testAnalytics = new HashMap<>();
        testAnalytics.put("completionPercentage", 75.5);
        testAnalytics.put("weeklyTrends", Map.of("MONDAY", 3L, "TUESDAY", 2L));
        testAnalytics.put("averageCompletionTime", 12.5);
    }

    @Test
    void getAnalytics_Success() {
        // Arrange
        when(jwtUtil.getLoggedInUser()).thenReturn(testUser);
        when(analyticsService.getCompletionPercentage(testUser.getId())).thenReturn(75.5);
        when(analyticsService.getWeeklyCompletionTrends(testUser.getId()))
                .thenReturn((Map<String, Long>) testAnalytics.get("weeklyTrends"));
        when(analyticsService.getAverageCompletionTime(testUser.getId()))
                .thenReturn(Optional.of(12.5));

       
        ResponseEntity<Map<String, Object>> response = analyticsController.getAnalytics();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        assertEquals(75.5, responseBody.get("completionPercentage"));
        assertEquals(3L, ((Map<?, ?>) responseBody.get("weeklyTrends")).get("MONDAY"));
        assertEquals(12.5, responseBody.get("averageCompletionTime"));

        verify(jwtUtil, times(1)).getLoggedInUser();
        verify(analyticsService, times(1)).getCompletionPercentage(testUser.getId());
        verify(analyticsService, times(1)).getWeeklyCompletionTrends(testUser.getId());
        verify(analyticsService, times(1)).getAverageCompletionTime(testUser.getId());
    }

    @Test
    void getAnalytics_Unauthorized() {
        // Arrange
        when(jwtUtil.getLoggedInUser()).thenReturn(null);

       
        ResponseEntity<Map<String, Object>> response = analyticsController.getAnalytics();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(jwtUtil, times(1)).getLoggedInUser();
        verifyNoInteractions(analyticsService);
    }

    @Test
    void getAnalytics_NoAverageCompletionTime() {
        // Arrange
        Map<String, Object> analyticsWithoutAvg = new HashMap<>(testAnalytics);
        analyticsWithoutAvg.remove("averageCompletionTime");

        when(jwtUtil.getLoggedInUser()).thenReturn(testUser);
        when(analyticsService.getCompletionPercentage(testUser.getId()))
                .thenReturn(75.5);
        when(analyticsService.getWeeklyCompletionTrends(testUser.getId()))
                .thenReturn((Map<String, Long>) testAnalytics.get("weeklyTrends"));
        when(analyticsService.getAverageCompletionTime(testUser.getId()))
                .thenReturn(Optional.empty());

        
        ResponseEntity<Map<String, Object>> response = analyticsController.getAnalytics();

        // assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        assertEquals(75.5, responseBody.get("completionPercentage"));
        assertEquals(3L, ((Map<?, ?>) responseBody.get("weeklyTrends")).get("MONDAY"));
        assertFalse(responseBody.containsKey("averageCompletionTime"));
    }

    @Test
    void getAnalytics_EmptyWeeklyTrends() {
        // arrange
        Map<String, Object> analyticsWithEmptyTrends = new HashMap<>(testAnalytics);
        analyticsWithEmptyTrends.put("weeklyTrends", Map.of());

        when(jwtUtil.getLoggedInUser()).thenReturn(testUser);
        when(analyticsService.getCompletionPercentage(testUser.getId()))
                .thenReturn(0.0);
        when(analyticsService.getWeeklyCompletionTrends(testUser.getId()))
                .thenReturn(Map.of());
        when(analyticsService.getAverageCompletionTime(testUser.getId()))
                .thenReturn(Optional.empty());

        
        ResponseEntity<Map<String, Object>> response = analyticsController.getAnalytics();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> responseBody = response.getBody();
        assertEquals(0.0, responseBody.get("completionPercentage"));
        assertTrue(((Map<?, ?>) responseBody.get("weeklyTrends")).isEmpty());
        assertFalse(responseBody.containsKey("averageCompletionTime"));
    }
}
