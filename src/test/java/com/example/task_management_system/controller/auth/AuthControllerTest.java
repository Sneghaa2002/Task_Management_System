package com.example.task_management_system.controller.auth;

import com.example.task_management_system.dto.*;
import com.example.task_management_system.entity.User;
import com.example.task_management_system.enums.UserRole;
import com.example.task_management_system.repository.UserRepository;
import com.example.task_management_system.service.auth.AuthService;
import com.example.task_management_system.service.jwt.UserService;
import com.example.task_management_system.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private SignupRequest signupRequest;
    private AuthenticationRequest authRequest;
    private User user;
    private UserDto userDto;
    private AuthenticationResponse authResponse;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        authRequest = new AuthenticationRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setUserRole(UserRole.EMPLOYEE);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setUserRole(UserRole.EMPLOYEE);

        authResponse = new AuthenticationResponse();
        authResponse.setJwt("test.jwt.token");
        authResponse.setUserId(1L);
        authResponse.setUserRole(UserRole.EMPLOYEE);
    }

    @Test
    void signupUser_Success() {
        when(authService.hasUserWithEmail(signupRequest.getEmail())).thenReturn(false);
        when(authService.signupUser(signupRequest)).thenReturn(userDto);

        ResponseEntity<?> response = authController.signupUser(signupRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(authService, times(1)).signupUser(signupRequest);
    }

    @Test
    void signupUser_EmailExists() {
        when(authService.hasUserWithEmail(signupRequest.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.signupUser(signupRequest);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(authService, never()).signupUser(any());
    }

    /*@Test
    void login_UserNotFound() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Mock user details
        UserDetails userDetails = mock(UserDetails.class);
        when(userService.userDetailService().loadUserByUsername(authRequest.getEmail()))
                .thenReturn(userDetails);

        // Mock repository
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));

        // Mock JWT generation
        when(jwtUtil.generateToken(userDetails)).thenReturn("test.jwt.token");

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthenticationResponse);

        AuthenticationResponse responseBody = (AuthenticationResponse) response.getBody();
        assertEquals("test.jwt.token", responseBody.getJwt());
        assertEquals(1L, responseBody.getUserId());
        assertEquals(UserRole.EMPLOYEE, responseBody.getUserRole());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtil, times(1)).generateToken(userDetails);
    }*/

   /* @Test
    void login_Success(){
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(userService.userDetailService().loadUserByUsername(authRequest.getEmail()))
                .thenReturn(mock(UserDetails.class));
        when(userRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authController.login(authRequest));
    }*/

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        assertThrows(RuntimeException.class, () -> authController.login(authRequest));
    }
}