package com.example.task_management_system.repository;

import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    Optional<User> findByUserRole(UserRole userRole);
}
