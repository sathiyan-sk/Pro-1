package com.trackerpro.entity;

public enum ApplicationStatus {
    APPLIED("Applied"),
    UNDER_REVIEW("Under Review"),
    INTERVIEW("Interview"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    COMPLETED("Completed");
    
    private final String displayName;
    
    ApplicationStatus(String displayName) {
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