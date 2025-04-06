package com.example.task_management_system.service.jwt;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    // Provides user details (like username, password, roles) to Spring Security for authentication
    UserDetailsService userDetailService();
}
