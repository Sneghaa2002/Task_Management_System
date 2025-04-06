package com.example.task_management_system.service.auth;

import com.example.task_management_system.dto.SignupRequest;
import com.example.task_management_system.dto.UserDto;

public interface AuthService {

    // Handles user registration and returns user details after successful signup
    UserDto signupUser(SignupRequest signupRequest);

    // Checks if a user already exists with the given email
    boolean hasUserWithEmail(String email);
}
