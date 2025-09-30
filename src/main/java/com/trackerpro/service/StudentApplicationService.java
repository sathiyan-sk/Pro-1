package com.trackerpro.service;

import com.trackerpro.entity.StudentApplication;
import com.trackerpro.entity.Student;
import com.trackerpro.entity.Course;
import com.trackerpro.entity.ApplicationStatus;
import com.trackerpro.entity.CourseStatus;
import com.trackerpro.entity.StudentStatus;
import com.trackerpro.repository.StudentApplicationRepository;
import com.trackerpro.repository.StudentRepository;
import com.trackerpro.repository.CourseRepository;
import com.trackerpro.dto.CourseApplicationRequest;
import com.trackerpro.dto.ApplicationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StudentApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentApplicationService.class);
    
    @Autowired
    private StudentApplicationRepository applicationRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * Apply for a course (students can only apply for one course)
     */
    public ApplicationResponse applyCourse(CourseApplicationRequest request) {
        try {
            logger.info("Processing course application for student: {} to course: {}", 
                       request.getStudentId(), request.getCourseId());
            
            // Check if student exists
            Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getStudentId()));
            
            // Check if student is eligible (not suspended)
            if (student.getStatus() == StudentStatus.SUSPENDED) {
                return ApplicationResponse.failure("Your account is suspended. Cannot apply for courses.");
            }
            
            // Check if student already has an application
            if (applicationRepository.existsByStudentId(request.getStudentId())) {
                return ApplicationResponse.failure("You have already applied for a course. Students can only apply for one course.");
            }
            
            // Check if course exists and is published
            Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));
            
            if (course.getStatus() != CourseStatus.PUBLISHED) {
                return ApplicationResponse.failure("This course is not available for application.");
            }
            
            // Create new application
            StudentApplication application = new StudentApplication(student, course);
            application.setApplicationNotes(request.getApplicationNotes());
            application.setStatus(ApplicationStatus.APPLIED); // Auto-approval: start with APPLIED
            
            // Update student status to ENROLLED since it's auto-approved
            student.setStatus(StudentStatus.ENROLLED);
            studentRepository.save(student);
            
            StudentApplication savedApplication = applicationRepository.save(application);
            
            // Create response data
            ApplicationResponse.ApplicationData data = new ApplicationResponse.ApplicationData(
                savedApplication.getApplicationId(),
                savedApplication.getStudent().getStudentId(),
                savedApplication.getCourse().getCourseId(),
                savedApplication.getCourse().getCourseTitle(),
                savedApplication.getCourse().getCourseCode(),
                savedApplication.getStatus(),
                savedApplication.getProgressPercentage(),
                savedApplication.getAppliedAt(),
                savedApplication.getUpdatedAt()
            );
            
            logger.info("Course application successful for student: {} to course: {}", 
                       request.getStudentId(), request.getCourseId());
            
            return ApplicationResponse.success("Application submitted successfully! You are now enrolled in the course.", data);
            
        } catch (RuntimeException e) {
            logger.error("Course application failed: {}", e.getMessage());
            return ApplicationResponse.failure(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during course application: {}", e.getMessage(), e);
            return ApplicationResponse.failure("Application failed. Please try again later.");
        }
    }
    
    /**
     * Get student's application
     */
    public Optional<StudentApplication> getStudentApplication(UUID studentId) {
        logger.info("Fetching application for student: {}", studentId);
        return applicationRepository.findByStudentId(studentId);
    }
    
    /**
     * Get all applications for a course
     */
    public List<StudentApplication> getCourseApplications(UUID courseId) {
        logger.info("Fetching applications for course: {}", courseId);
        return applicationRepository.findByCourseId(courseId);
    }
    
    /**
     * Update application status (for admin use)
     */
    public ApplicationResponse updateApplicationStatus(UUID applicationId, ApplicationStatus newStatus) {
        try {
            logger.info("Updating application status: {} to {}", applicationId, newStatus);
            
            StudentApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));
            
            ApplicationStatus oldStatus = application.getStatus();
            application.setStatus(newStatus);
            
            // Update timestamps based on status
            switch (newStatus) {
                case INTERVIEW -> application.setInterviewDate(LocalDateTime.now());
                case ACCEPTED -> application.setAcceptedAt(LocalDateTime.now());
                case COMPLETED -> {
                    application.setCompletedAt(LocalDateTime.now());
                    // Update student status to COMPLETED
                    application.getStudent().setStatus(StudentStatus.COMPLETED);
                    studentRepository.save(application.getStudent());
                }
                case REJECTED -> {
                    // Update student status back to REGISTERED if rejected
                    application.getStudent().setStatus(StudentStatus.REGISTERED);
                    studentRepository.save(application.getStudent());
                }
            }
            
            StudentApplication savedApplication = applicationRepository.save(application);
            
            logger.info("Application status updated from {} to {} for application: {}", 
                       oldStatus, newStatus, applicationId);
            
            return ApplicationResponse.success("Application status updated successfully.");
            
        } catch (RuntimeException e) {
            logger.error("Failed to update application status: {}", e.getMessage());
            return ApplicationResponse.failure(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating application status: {}", e.getMessage(), e);
            return ApplicationResponse.failure("Status update failed. Please try again later.");
        }
    }
    
    /**
     * Get all applications (for admin dashboard)
     */
    public List<StudentApplication> getAllApplications() {
        logger.info("Fetching all applications");
        return applicationRepository.findAllWithDetails();
    }
    
    /**
     * Search applications by student details
     */
    public List<StudentApplication> searchApplications(String searchTerm) {
        logger.info("Searching applications with term: {}", searchTerm);
        return applicationRepository.searchByStudentDetails(searchTerm);
    }
    
    /**
     * Get recent applications (last 7 days)
     */
    public List<StudentApplication> getRecentApplications() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        logger.info("Fetching recent applications since: {}", weekAgo);
        return applicationRepository.findRecentApplications(weekAgo);
    }
    
    /**
     * Check if student can apply (no existing application)
     */
    public boolean canStudentApply(UUID studentId) {
        return !applicationRepository.existsByStudentId(studentId);
    }
    
    /**
     * Get application statistics
     */
    public ApplicationStatistics getApplicationStatistics() {
        logger.info("Calculating application statistics");
        
        long totalApplications = applicationRepository.count();
        long appliedCount = applicationRepository.countByStatus(ApplicationStatus.APPLIED);
        long underReviewCount = applicationRepository.countByStatus(ApplicationStatus.UNDER_REVIEW);
        long interviewCount = applicationRepository.countByStatus(ApplicationStatus.INTERVIEW);
        long acceptedCount = applicationRepository.countByStatus(ApplicationStatus.ACCEPTED);
        long rejectedCount = applicationRepository.countByStatus(ApplicationStatus.REJECTED);
        long completedCount = applicationRepository.countByStatus(ApplicationStatus.COMPLETED);
        
        return new ApplicationStatistics(totalApplications, appliedCount, underReviewCount, 
                                       interviewCount, acceptedCount, rejectedCount, completedCount);
    }
    
    /**
     * Application statistics inner class
     */
    public static class ApplicationStatistics {
        private final long totalApplications;
        private final long appliedCount;
        private final long underReviewCount;
        private final long interviewCount;
        private final long acceptedCount;
        private final long rejectedCount;
        private final long completedCount;
        
        public ApplicationStatistics(long totalApplications, long appliedCount, long underReviewCount,
                                   long interviewCount, long acceptedCount, long rejectedCount, long completedCount) {
            this.totalApplications = totalApplications;
            this.appliedCount = appliedCount;
            this.underReviewCount = underReviewCount;
            this.interviewCount = interviewCount;
            this.acceptedCount = acceptedCount;
            this.rejectedCount = rejectedCount;
            this.completedCount = completedCount;
        }
        
        // Getters
        public long getTotalApplications() { return totalApplications; }
        public long getAppliedCount() { return appliedCount; }
        public long getUnderReviewCount() { return underReviewCount; }
        public long getInterviewCount() { return interviewCount; }
        public long getAcceptedCount() { return acceptedCount; }
        public long getRejectedCount() { return rejectedCount; }
        public long getCompletedCount() { return completedCount; }
    }
}