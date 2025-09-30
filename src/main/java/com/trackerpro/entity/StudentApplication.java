package com.trackerpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_applications", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id"}))
public class StudentApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id")
    private UUID applicationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "Course is required")
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;
    
    @Column(name = "application_notes", columnDefinition = "TEXT")
    private String applicationNotes;
    
    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 25; // 25% for APPLIED status
    
    @CreationTimestamp
    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "interview_date")
    private LocalDateTime interviewDate;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Constructors
    public StudentApplication() {}
    
    public StudentApplication(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.status = ApplicationStatus.APPLIED;
        this.progressPercentage = 25;
    }
    
    // Getters and Setters
    public UUID getApplicationId() { return applicationId; }
    public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
    
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { 
        this.status = status;
        // Auto-update progress percentage based on status
        updateProgressPercentage();
    }
    
    public String getApplicationNotes() { return applicationNotes; }
    public void setApplicationNotes(String applicationNotes) { this.applicationNotes = applicationNotes; }
    
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getInterviewDate() { return interviewDate; }
    public void setInterviewDate(LocalDateTime interviewDate) { this.interviewDate = interviewDate; }
    
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    // Helper method to update progress percentage based on status
    private void updateProgressPercentage() {
        switch (this.status) {
            case APPLIED -> this.progressPercentage = 25;
            case UNDER_REVIEW -> this.progressPercentage = 50;
            case INTERVIEW -> this.progressPercentage = 75;
            case ACCEPTED -> this.progressPercentage = 90;
            case COMPLETED -> this.progressPercentage = 100;
            case REJECTED -> this.progressPercentage = 0;
        }
    }
    
    @Override
    public String toString() {
        return "StudentApplication{" +
                "applicationId=" + applicationId +
                ", studentId=" + (student != null ? student.getStudentId() : null) +
                ", courseId=" + (course != null ? course.getCourseId() : null) +
                ", status=" + status +
                ", progressPercentage=" + progressPercentage +
                '}';
    }
}