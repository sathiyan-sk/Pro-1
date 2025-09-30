package com.trackerpro.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CourseApplicationRequest {
    
    @NotNull(message = "Student ID is required")
    private UUID studentId;
    
    @NotNull(message = "Course ID is required")
    private UUID courseId;
    
    private String applicationNotes;
    
    // Constructors
    public CourseApplicationRequest() {}
    
    public CourseApplicationRequest(UUID studentId, UUID courseId, String applicationNotes) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.applicationNotes = applicationNotes;
    }
    
    // Getters and Setters
    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    
    public UUID getCourseId() { return courseId; }
    public void setCourseId(UUID courseId) { this.courseId = courseId; }
    
    public String getApplicationNotes() { return applicationNotes; }
    public void setApplicationNotes(String applicationNotes) { this.applicationNotes = applicationNotes; }
}