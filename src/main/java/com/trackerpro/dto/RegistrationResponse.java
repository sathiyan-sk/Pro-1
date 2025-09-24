package com.trackerpro.dto;

public class RegistrationResponse {
    
    private boolean success;
    private String message;
    private String studentId;
    
    // Constructors
    public RegistrationResponse() {}
    
    public RegistrationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public RegistrationResponse(boolean success, String message, String studentId) {
        this.success = success;
        this.message = message;
        this.studentId = studentId;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    // Static factory methods
    public static RegistrationResponse success(String message, String studentId) {
        return new RegistrationResponse(true, message, studentId);
    }
    
    public static RegistrationResponse failure(String message) {
        return new RegistrationResponse(false, message);
    }
}