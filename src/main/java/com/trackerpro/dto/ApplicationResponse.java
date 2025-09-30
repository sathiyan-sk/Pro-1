package com.trackerpro.dto;

import com.trackerpro.entity.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationResponse {
    
    private boolean success;
    private String message;
    private ApplicationData data;
    
    // Constructors
    public ApplicationResponse() {}
    
    public ApplicationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ApplicationResponse(boolean success, String message, ApplicationData data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Static factory methods
    public static ApplicationResponse success(String message) {
        return new ApplicationResponse(true, message);
    }
    
    public static ApplicationResponse success(String message, ApplicationData data) {
        return new ApplicationResponse(true, message, data);
    }
    
    public static ApplicationResponse failure(String message) {
        return new ApplicationResponse(false, message);
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public ApplicationData getData() { return data; }
    public void setData(ApplicationData data) { this.data = data; }
    
    // Inner class for application data
    public static class ApplicationData {
        private UUID applicationId;
        private UUID studentId;
        private UUID courseId;
        private String courseTitle;
        private String courseCode;
        private ApplicationStatus status;
        private Integer progressPercentage;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;
        
        // Constructors
        public ApplicationData() {}
        
        public ApplicationData(UUID applicationId, UUID studentId, UUID courseId, 
                             String courseTitle, String courseCode, ApplicationStatus status,
                             Integer progressPercentage, LocalDateTime appliedAt, LocalDateTime updatedAt) {
            this.applicationId = applicationId;
            this.studentId = studentId;
            this.courseId = courseId;
            this.courseTitle = courseTitle;
            this.courseCode = courseCode;
            this.status = status;
            this.progressPercentage = progressPercentage;
            this.appliedAt = appliedAt;
            this.updatedAt = updatedAt;
        }
        
        // Getters and Setters
        public UUID getApplicationId() { return applicationId; }
        public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
        
        public UUID getStudentId() { return studentId; }
        public void setStudentId(UUID studentId) { this.studentId = studentId; }
        
        public UUID getCourseId() { return courseId; }
        public void setCourseId(UUID courseId) { this.courseId = courseId; }
        
        public String getCourseTitle() { return courseTitle; }
        public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
        
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        
        public ApplicationStatus getStatus() { return status; }
        public void setStatus(ApplicationStatus status) { this.status = status; }
        
        public Integer getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
        
        public LocalDateTime getAppliedAt() { return appliedAt; }
        public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}