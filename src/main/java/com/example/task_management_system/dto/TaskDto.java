package com.example.task_management_system.dto;

import com.example.task_management_system.enums.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Date createdDate;
    private Date deadline;
    private String priority;
    private TaskStatus taskStatus;
    private Long employeeId;
    private String employeeName;
}