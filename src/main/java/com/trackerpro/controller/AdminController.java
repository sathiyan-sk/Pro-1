package com.trackerpro.controller;

import com.trackerpro.dto.ApiResponse;
import com.trackerpro.entity.Admin;
import com.trackerpro.entity.Student;
import com.trackerpro.entity.User;
import com.trackerpro.service.StudentService;
import com.trackerpro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private UserService userService;

    //admin login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Admin admin) {
        logger.info("Admin login attempt with email: {}", admin.getEmail());

        try {
            boolean isValid = adminService.validateAdmin(admin.getEmail(), admin.getPassword());
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Login successful", "admin"));
            } else {
                return ResponseEntity.status(401).body(ApiResponse.failure("Invalid admin credentials"));
            }
        } catch (Exception e) {
            logger.error("Error during admin login", e);
            return ResponseEntity.status(500).body(ApiResponse.failure("Login failed: " + e.getMessage()));
        }
    }

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        logger.info("Fetching dashboard statistics");
        
        try {
            StudentService.StudentStatistics studentStats = studentService.getStudentStatistics();
            UserService.UserStatistics userStats = userService.getUserStatistics();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", studentStats.getTotalStudents());
            stats.put("newStudentsThisWeek", studentStats.getNewStudentsThisWeek());
            stats.put("totalFaculty", userStats.getFacultyCount());
            stats.put("totalHR", userStats.getHrCount());
            stats.put("totalUsers", userStats.getActiveUsers());
            
            return ResponseEntity.ok(ApiResponse.success("Dashboard statistics fetched", stats));
            
        } catch (Exception e) {
            logger.error("Error fetching dashboard statistics", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch statistics"));
        }
    }
    
    /**
     * Get all student registrations
     */
    @GetMapping("/registrations")
    public ResponseEntity<ApiResponse<List<Student>>> getAllRegistrations() {
        logger.info("Fetching all student registrations");
        
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(ApiResponse.success("Registrations fetched successfully", students));
            
        } catch (Exception e) {
            logger.error("Error fetching registrations", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch registrations"));
        }
    }
    
    /**
     * Search student registrations
     */
    @GetMapping("/registrations/search")
    public ResponseEntity<ApiResponse<List<Student>>> searchRegistrations(@RequestParam String query) {
        logger.info("Searching registrations with query: {}", query);
        
        try {
            List<Student> students = studentService.searchStudents(query);
            return ResponseEntity.ok(ApiResponse.success("Search completed", students));
            
        } catch (Exception e) {
            logger.error("Error searching registrations", e);
            return ResponseEntity.ok(ApiResponse.failure("Search failed"));
        }
    }
    
    /**
     * Get recent student registrations
     */
    @GetMapping("/registrations/recent")
    public ResponseEntity<ApiResponse<List<Student>>> getRecentRegistrations() {
        logger.info("Fetching recent registrations");
        
        try {
            List<Student> students = studentService.getRecentStudents();
            return ResponseEntity.ok(ApiResponse.success("Recent registrations fetched", students));
            
        } catch (Exception e) {
            logger.error("Error fetching recent registrations", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch recent registrations"));
        }
    }
    
    /**
     * Get all users (Faculty/HR)
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        logger.info("Fetching all users");
        
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
            
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch users"));
        }
    }
    
    /**
     * Create new user (Faculty/HR)
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        logger.info("Creating new user with email: {}", user.getEmail());
        
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(ApiResponse.success("User created successfully", createdUser));
            
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to create user: " + e.getMessage()));
        }
    }
}