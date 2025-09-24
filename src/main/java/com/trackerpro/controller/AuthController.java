package com.trackerpro.controller;

import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.LoginResponse;
import com.trackerpro.dto.StudentRegistrationRequest;
import com.trackerpro.dto.RegistrationResponse;
import com.trackerpro.service.AuthenticationService;
import com.trackerpro.service.StudentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private StudentService studentService;
    
    /**
     * Universal login endpoint for all users (Admin, Faculty, HR, Students)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());
        
        LoginResponse response = authenticationService.authenticateLogin(loginRequest);
        
        if (response.isSuccess()) {
            logger.info("Login successful for email: {}, userType: {}", 
                       loginRequest.getEmail(), response.getUserType());
        } else {
            logger.warn("Login failed for email: {}, reason: {}", 
                       loginRequest.getEmail(), response.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Student registration endpoint
     */
    @PostMapping("/auth/register")
    public ResponseEntity<RegistrationResponse> registerStudent(
            @Valid @RequestBody StudentRegistrationRequest registrationRequest) {
        
        logger.info("Student registration attempt for email: {}", registrationRequest.getEmail());
        
        RegistrationResponse response = studentService.registerStudent(registrationRequest);
        
        if (response.isSuccess()) {
            logger.info("Student registration successful for email: {}, studentId: {}", 
                       registrationRequest.getEmail(), response.getStudentId());
        } else {
            logger.warn("Student registration failed for email: {}, reason: {}", 
                       registrationRequest.getEmail(), response.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Logout endpoint (for session cleanup if needed)
     */
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout() {
        logger.info("User logout");
        return ResponseEntity.ok(new LoginResponse(true, "Logout successful"));
    }
    
    /**
     * Check authentication status
     */
    @GetMapping("/auth/status")
    public ResponseEntity<LoginResponse> checkAuthStatus() {
        // This would typically check JWT token or session
        // For now, return a simple response
        return ResponseEntity.ok(new LoginResponse(true, "Authentication check"));
    }
}