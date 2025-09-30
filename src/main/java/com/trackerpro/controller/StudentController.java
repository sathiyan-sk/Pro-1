package com.trackerpro.controller;

import com.trackerpro.entity.Student;
import com.trackerpro.entity.Course;
import com.trackerpro.entity.StudentApplication;
import com.trackerpro.entity.CourseStatus;
import com.trackerpro.service.StudentService;
import com.trackerpro.service.CourseService;
import com.trackerpro.service.StudentApplicationService;
import com.trackerpro.dto.CourseApplicationRequest;
import com.trackerpro.dto.ApplicationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentApplicationService applicationService;
    
    /**
     * Get student profile by ID
     */
    @GetMapping("/profile/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@PathVariable UUID studentId) {
        try {
            logger.info("Fetching profile for student: {}", studentId);
            
            Student student = studentService.getStudentById(studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createStudentProfileData(student));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching student profile: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch student profile");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get available courses for students to apply
     */
    @GetMapping("/courses/available")
    public ResponseEntity<Map<String, Object>> getAvailableCourses() {
        try {
            logger.info("Fetching available courses for students");
            
            List<Course> publishedCourses = courseService.getCoursesByStatus(CourseStatus.PUBLISHED);
            
            List<Map<String, Object>> coursesData = publishedCourses.stream()
                .map(this::createCourseData)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", coursesData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching available courses: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch available courses");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get student's application
     */
    @GetMapping("/applications/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentApplication(@PathVariable UUID studentId) {
        try {
            logger.info("Fetching application for student: {}", studentId);
            
            Optional<StudentApplication> applicationOpt = applicationService.getStudentApplication(studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            if (applicationOpt.isPresent()) {
                StudentApplication application = applicationOpt.get();
                response.put("data", createApplicationData(application));
                response.put("hasApplication", true);
            } else {
                response.put("data", null);
                response.put("hasApplication", false);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching student application: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch student application");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Apply for a course
     */
    @PostMapping("/apply")
    public ResponseEntity<ApplicationResponse> applyCourse(@Valid @RequestBody CourseApplicationRequest request) {
        logger.info("Course application request from student: {} for course: {}", 
                   request.getStudentId(), request.getCourseId());
        
        ApplicationResponse response = applicationService.applyCourse(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get application progress for a student
     */
    @GetMapping("/progress/{studentId}")
    public ResponseEntity<Map<String, Object>> getApplicationProgress(@PathVariable UUID studentId) {
        try {
            logger.info("Fetching application progress for student: {}", studentId);
            
            Optional<StudentApplication> applicationOpt = applicationService.getStudentApplication(studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            if (applicationOpt.isPresent()) {
                StudentApplication application = applicationOpt.get();
                
                Map<String, Object> progressData = new HashMap<>();
                progressData.put("status", application.getStatus().toString());
                progressData.put("progressPercentage", application.getProgressPercentage());
                progressData.put("currentStep", getCurrentStepFromStatus(application.getStatus()));
                progressData.put("appliedAt", application.getAppliedAt());
                progressData.put("updatedAt", application.getUpdatedAt());
                progressData.put("courseTitle", application.getCourse().getCourseTitle());
                
                // Add step details
                progressData.put("steps", createProgressSteps(application));
                
                response.put("data", progressData);
                
            } else {
                response.put("data", null);
                response.put("message", "No application found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching application progress: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch application progress");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Get course details by ID
     */
    @GetMapping("/courses/{courseId}/details")
    public ResponseEntity<Map<String, Object>> getCourseDetails(@PathVariable UUID courseId) {
        try {
            logger.info("Fetching details for course: {}", courseId);
            
            Optional<Course> courseOpt = courseService.getCourseById(courseId);
            
            Map<String, Object> response = new HashMap<>();
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                response.put("success", true);
                response.put("data", createDetailedCourseData(course));
            } else {
                response.put("success", false);
                response.put("message", "Course not found");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching course details: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch course details");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    /**
     * Check if student can apply for courses
     */
    @GetMapping("/can-apply/{studentId}")
    public ResponseEntity<Map<String, Object>> canStudentApply(@PathVariable UUID studentId) {
        try {
            boolean canApply = applicationService.canStudentApply(studentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("canApply", canApply);
            response.put("message", canApply ? "Student can apply for courses" : "Student already has an application");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking if student can apply: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to check application eligibility");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    // Helper methods to create response data
    private Map<String, Object> createStudentProfileData(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getStudentId());
        data.put("firstName", student.getFirstName());
        data.put("lastName", student.getLastName());
        data.put("fullName", student.getFullName());
        data.put("email", student.getEmail());
        data.put("gender", student.getGender().toString());
        data.put("age", student.getAge());
        data.put("dateOfBirth", student.getDateOfBirth());
        data.put("location", student.getLocation());
        data.put("mobileNo", student.getMobileNo());
        data.put("status", student.getStatus().toString());
        data.put("registeredAt", student.getRegisteredAt());
        return data;
    }
    
    private Map<String, Object> createCourseData(Course course) {
        Map<String, Object> data = new HashMap<>();
        data.put("courseId", course.getCourseId());
        data.put("courseCode", course.getCourseCode());
        data.put("courseTitle", course.getCourseTitle());
        data.put("durationMonths", course.getDurationMonths());
        data.put("category", course.getCategory());
        data.put("prerequisites", course.getPrerequisites());
        data.put("description", course.getDescription());
        data.put("status", course.getStatus().toString());
        data.put("createdAt", course.getCreatedAt());
        return data;
    }
    
    private Map<String, Object> createDetailedCourseData(Course course) {
        Map<String, Object> data = createCourseData(course);
        data.put("batchesCount", course.getBatchesCount());
        data.put("updatedAt", course.getUpdatedAt());
        return data;
    }
    
    private Map<String, Object> createApplicationData(StudentApplication application) {
        Map<String, Object> data = new HashMap<>();
        data.put("applicationId", application.getApplicationId());
        data.put("studentId", application.getStudent().getStudentId());
        data.put("courseId", application.getCourse().getCourseId());
        data.put("courseTitle", application.getCourse().getCourseTitle());
        data.put("courseCode", application.getCourse().getCourseCode());
        data.put("status", application.getStatus().toString());
        data.put("progressPercentage", application.getProgressPercentage());
        data.put("appliedAt", application.getAppliedAt());
        data.put("updatedAt", application.getUpdatedAt());
        data.put("applicationNotes", application.getApplicationNotes());
        return data;
    }
    
    private int getCurrentStepFromStatus(com.trackerpro.entity.ApplicationStatus status) {
        return switch (status) {
            case APPLIED -> 1;
            case UNDER_REVIEW -> 2;
            case INTERVIEW -> 3;
            case ACCEPTED -> 4;
            case COMPLETED -> 5;
            case REJECTED -> -1;
        };
    }
    
    private List<Map<String, Object>> createProgressSteps(StudentApplication application) {
        List<Map<String, Object>> steps = new ArrayList<>();
        
        // Step 1: Registered (always completed for applications)
        steps.add(createStep("Registered", "completed", "✓"));
        
        // Step 2: Applied
        String step2Status = application.getStatus().ordinal() >= 0 ? "completed" : "pending";
        steps.add(createStep("Applied", step2Status, application.getAppliedAt() != null ? "✓" : "○"));
        
        // Step 3: Under Review
        String step3Status = application.getStatus().ordinal() >= 1 ? 
            (application.getStatus() == com.trackerpro.entity.ApplicationStatus.UNDER_REVIEW ? "active" : "completed") : "pending";
        steps.add(createStep("Under Review", step3Status, application.getStatus().ordinal() >= 1 ? "✓" : "○"));
        
        // Step 4: Interview
        String step4Status = application.getStatus().ordinal() >= 2 ? 
            (application.getStatus() == com.trackerpro.entity.ApplicationStatus.INTERVIEW ? "active" : "completed") : "pending";
        steps.add(createStep("Interview", step4Status, application.getInterviewDate() != null ? "✓" : "○"));
        
        // Step 5: Accepted
        String step5Status = application.getStatus().ordinal() >= 3 ? "completed" : "pending";
        steps.add(createStep("Accepted", step5Status, application.getAcceptedAt() != null ? "✓" : "○"));
        
        return steps;
    }
    
    private Map<String, Object> createStep(String title, String status, String icon) {
        Map<String, Object> step = new HashMap<>();
        step.put("title", title);
        step.put("status", status);
        step.put("icon", icon);
        return step;
    }
}