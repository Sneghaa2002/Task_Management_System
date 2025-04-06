package com.example.task_management_system.dto;

import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import lombok.Data;



@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String password;

    private UserRole userRole;

    public static UserDto fromUser(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setUserRole(user.getUserRole());
        return dto;
    }

}
