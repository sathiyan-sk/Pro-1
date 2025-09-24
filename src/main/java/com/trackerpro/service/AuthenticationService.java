package com.trackerpro.service;

import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StudentService studentService;
    
    /**
     * Universal login method that handles Admin, Users (Faculty/HR), and Students
     */
    public LoginResponse authenticateLogin(LoginRequest loginRequest) {
        // First try admin authentication
        LoginResponse adminResponse = adminService.authenticateAdmin(loginRequest);
        if (adminResponse.isSuccess()) {
            return adminResponse;
        }
        
        // Then try user authentication (Faculty/HR)
        LoginResponse userResponse = userService.authenticateUser(loginRequest);
        if (userResponse.isSuccess()) {
            return userResponse;
        }
        
        // Finally try student authentication
        LoginResponse studentResponse = studentService.authenticateStudent(loginRequest);
        if (studentResponse.isSuccess()) {
            return studentResponse;
        }
        
        // If all fail, return generic failure message
        return LoginResponse.failure("Invalid email or password");
    }
}