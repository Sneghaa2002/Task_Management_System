package com.example.task_management_system.dto;

import com.example.task_management_system.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {

    private String jwt;
    private Long userId;
    private UserRole userRole;

}
