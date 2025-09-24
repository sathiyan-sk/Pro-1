package com.trackerpro.entity;

public enum UserRole {
    ADMIN("Administrator"),
    FACULTY("Faculty Member"), 
    HR("HR Staff");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}