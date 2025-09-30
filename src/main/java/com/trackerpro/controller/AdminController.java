package com.trackerpro.controller;

import com.trackerpro.dto.ApiResponse;
import com.trackerpro.entity.Admin;
import com.trackerpro.entity.Course;
import com.trackerpro.entity.Student;
import com.trackerpro.entity.User;
import com.trackerpro.service.AdminService;
import com.trackerpro.service.CourseService;
import com.trackerpro.service.StudentService;
import com.trackerpro.service.StudentApplicationService;
import com.trackerpro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        logger.info("Fetching dashboard statistics");
        
        try {
            StudentService.StudentStatistics studentStats = studentService.getStudentStatistics();
            UserService.UserStatistics userStats = userService.getUserStatistics();
            AdminService.AdminStatistics adminStats = adminService.getAdminStatistics();
            CourseService.CourseStatistics courseStats = courseService.getCourseStatistics();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", studentStats.getTotalStudents());
            stats.put("newStudentsThisWeek", studentStats.getNewStudentsThisWeek());
            stats.put("totalFaculty", userStats.getFacultyCount());
            stats.put("totalHR", userStats.getHrCount());
            stats.put("totalUsers", userStats.getActiveUsers());
            stats.put("totalAdmins", adminStats.getActiveAdmins());
            stats.put("totalCourses", courseStats.getTotalCourses());
            stats.put("publishedCourses", courseStats.getPublishedCourses());
            
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
    
    /**
     * Get all admins
     */
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<Admin>>> getAllAdmins() {
        logger.info("Fetching all admins");
        
        try {
            List<Admin> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(ApiResponse.success("Admins fetched successfully", admins));
            
        } catch (Exception e) {
            logger.error("Error fetching admins", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch admins"));
        }
    }
    
    /**
     * Create new admin
     */
    @PostMapping("/admins")
    public ResponseEntity<ApiResponse<Admin>> createAdmin(@RequestBody Admin admin) {
        logger.info("Creating new admin with email: {}", admin.getEmail());
        
        try {
            Admin createdAdmin = adminService.createAdmin(admin);
            return ResponseEntity.ok(ApiResponse.success("Admin created successfully", createdAdmin));
            
        } catch (Exception e) {
            logger.error("Error creating admin", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to create admin: " + e.getMessage()));
        }
    }
    
    // =================== COURSE MANAGEMENT ENDPOINTS ===================
    
    /**
     * Get all courses
     */
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        logger.info("Fetching all courses");
        
        try {
            List<Course> courses = courseService.getAllCourses();
            return ResponseEntity.ok(ApiResponse.success("Courses fetched successfully", courses));
            
        } catch (Exception e) {
            logger.error("Error fetching courses", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch courses"));
        }
    }
    
    /**
     * Get course by ID
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable UUID courseId) {
        logger.info("Fetching course by ID: {}", courseId);
        
        try {
            Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
            return ResponseEntity.ok(ApiResponse.success("Course fetched successfully", course));
            
        } catch (Exception e) {
            logger.error("Error fetching course", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to fetch course: " + e.getMessage()));
        }
    }
    
    /**
     * Create new course
     */
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        logger.info("Creating new course with code: {}", course.getCourseCode());
        
        try {
            Course createdCourse = courseService.createCourse(course);
            return ResponseEntity.ok(ApiResponse.success("Course created successfully", createdCourse));
            
        } catch (Exception e) {
            logger.error("Error creating course", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to create course: " + e.getMessage()));
        }
    }
    
    /**
     * Update existing course
     */
    @PutMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable UUID courseId, @RequestBody Course course) {
        logger.info("Updating course with ID: {}", courseId);
        
        try {
            Course updatedCourse = courseService.updateCourse(courseId, course);
            return ResponseEntity.ok(ApiResponse.success("Course updated successfully", updatedCourse));
            
        } catch (Exception e) {
            logger.error("Error updating course", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to update course: " + e.getMessage()));
        }
    }
    
    /**
     * Delete course
     */
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable UUID courseId) {
        logger.info("Deleting course with ID: {}", courseId);
        
        try {
            courseService.deleteCourse(courseId);
            return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
            
        } catch (Exception e) {
            logger.error("Error deleting course", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to delete course: " + e.getMessage()));
        }
    }
    
    /**
     * Search courses
     */
    @GetMapping("/courses/search")
    public ResponseEntity<ApiResponse<List<Course>>> searchCourses(@RequestParam String query) {
        logger.info("Searching courses with query: {}", query);
        
        try {
            List<Course> courses = courseService.searchCourses(query);
            return ResponseEntity.ok(ApiResponse.success("Search completed", courses));
            
        } catch (Exception e) {
            logger.error("Error searching courses", e);
            return ResponseEntity.ok(ApiResponse.failure("Search failed"));
        }
    }
    
    /**
     * Publish course
     */
    @PutMapping("/courses/{courseId}/publish")
    public ResponseEntity<ApiResponse<Course>> publishCourse(@PathVariable UUID courseId) {
        logger.info("Publishing course with ID: {}", courseId);
        
        try {
            Course publishedCourse = courseService.publishCourse(courseId);
            return ResponseEntity.ok(ApiResponse.success("Course published successfully", publishedCourse));
            
        } catch (Exception e) {
            logger.error("Error publishing course", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to publish course: " + e.getMessage()));
        }
    }
    
    /**
     * Unpublish course
     */
    @PutMapping("/courses/{courseId}/unpublish")
    public ResponseEntity<ApiResponse<Course>> unpublishCourse(@PathVariable UUID courseId) {
        logger.info("Unpublishing course with ID: {}", courseId);
        
        try {
            Course unpublishedCourse = courseService.unpublishCourse(courseId);
            return ResponseEntity.ok(ApiResponse.success("Course unpublished successfully", unpublishedCourse));
            
        } catch (Exception e) {
            logger.error("Error unpublishing course", e);
            return ResponseEntity.ok(ApiResponse.failure("Failed to unpublish course: " + e.getMessage()));
        }
    }
}