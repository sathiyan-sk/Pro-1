package com.trackerpro.dto;

public class LoginResponse {
    
    private boolean success;
    private String message;
    private String userType; // ADMIN, STUDENT
    private String token; // For JWT if needed
    private UserInfo user;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public LoginResponse(boolean success, String message, String userType, UserInfo user) {
        this.success = success;
        this.message = message;
        this.userType = userType;
        this.user = user;
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    
    // Static factory methods
    public static LoginResponse success(String userType, UserInfo user) {
        return new LoginResponse(true, "Login successful", userType, user);
    }
    
    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message);
    }
    
    // Inner class for user info
    public static class UserInfo {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        
        public UserInfo() {}
        
        public UserInfo(String id, String firstName, String lastName, String email, String role) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.role = role;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}