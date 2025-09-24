package com.trackerpro.entity;

public enum StudentStatus {
    REGISTERED("Registered"),
    ENROLLED("Enrolled"),
    COMPLETED("Completed"),
    DROPPED("Dropped"),
    SUSPENDED("Suspended");
    
    private final String displayName;
    
    StudentStatus(String displayName) {
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