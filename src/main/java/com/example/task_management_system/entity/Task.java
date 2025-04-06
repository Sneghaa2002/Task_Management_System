package com.example.task_management_system.entity;

import com.example.task_management_system.dto.TaskDto;
import com.example.task_management_system.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /*@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date date;*/

    @Temporal(TemporalType.DATE)
    private Date deadline;


    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;


    private String priority;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "time_estimate_minutes")
    private Integer timeEstimate;

    // this method to call when task is completed
    public void markComplete() {
        this.taskStatus = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    public static TaskDto fromTask(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCreatedDate(task.getCreatedDate());
        dto.setDeadline(task.getDeadline());
        dto.setPriority(task.getPriority());
        dto.setTaskStatus(task.getTaskStatus());
        dto.setEmployeeId(task.getUser() != null ? task.getUser().getId() : null);
        dto.setEmployeeName(task.getUser() != null ? task.getUser().getName() : null);
        return dto;
    }
}
