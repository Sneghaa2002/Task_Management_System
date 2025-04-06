package com.example.task_management_system.controller.auth;

import com.example.task_management_system.dto.*;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.auth.AuthService;
import com.example.task_management_system.service.jwt.UserService;
import com.example.task_management_system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        logger.info("Registration attempt for email: {}", signupRequest.getEmail());

        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            logger.warn("Registration failed - email already exists: {}", signupRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        UserDto createdUserDto = authService.signupUser(signupRequest);
        logger.info("User registered successfully with ID: {}", createdUserDto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );

        final UserDetails userDetails = userService.userDetailService()
                .loadUserByUsername(authenticationRequest.getEmail());

        Optional<User> optionalUser = userRepository.findByEmail(authenticationRequest.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        User user = optionalUser.get();

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwtToken);
        authenticationResponse.setUserId(user.getId());
        authenticationResponse.setUserRole(user.getUserRole());

        logger.info("Login successful for user ID: {}", user.getId());
        return ResponseEntity.ok(authenticationResponse);
    }
}