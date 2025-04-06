package com.example.task_management_system.repository;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.entity.Task;
import com.example.task_management_system.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findByDeadline(LocalDate deadline);
    List<Task> findByUserId(Long userId);

    List<Task> findByTaskStatus(TaskStatus taskStatus);
    List<Task> findByPriority(String priority);

    long countByUserId(Long userId);
    long countByUserIdAndTaskStatus(Long userId, TaskStatus taskStatus);
    List<Task> findByUserIdAndTaskStatusAndCompletedAtBetween(Long userId, TaskStatus status, LocalDateTime start, LocalDateTime end);
    List<Task> findByUserIdAndTaskStatus(Long userId, TaskStatus status);
}
