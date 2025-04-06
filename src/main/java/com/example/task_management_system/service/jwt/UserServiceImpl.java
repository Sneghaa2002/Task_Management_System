package com.example.task_management_system.service.jwt;

import com.example.task_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailService() {
        return username -> {
            logger.debug("Loading user by username/email: {}", username);
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", username);
                        return new UsernameNotFoundException("User not found");
                    });
        };
    }
}