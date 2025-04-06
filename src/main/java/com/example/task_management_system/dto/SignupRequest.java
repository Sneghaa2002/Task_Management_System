package com.example.task_management_system.dto;


import lombok.Data;

@Data
public class SignupRequest {

    private String name;
    private String email;
    private String password;
}
