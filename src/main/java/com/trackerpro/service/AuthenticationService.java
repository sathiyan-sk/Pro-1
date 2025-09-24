package com.trackerpro.service;

import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentService studentService;
    
    /**
     * Universal login method that handles both admin/users and students
     */
    public LoginResponse authenticateLogin(LoginRequest loginRequest) {
        // First try user authentication (Admin/Faculty/HR)
        LoginResponse userResponse = userService.authenticateUser(loginRequest);
        if (userResponse.isSuccess()) {
            return userResponse;
        }
        
        // If user authentication fails, try student authentication
        LoginResponse studentResponse = studentService.authenticateStudent(loginRequest);
        if (studentResponse.isSuccess()) {
            return studentResponse;
        }
        
        // If both fail, return generic failure message
        return LoginResponse.failure("Invalid email or password");
    }
}