package com.trackerpro.entity;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");
    
    private final String displayName;
    
    Gender(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static Gender fromString(String text) {
        for (Gender gender : Gender.values()) {
            if (gender.displayName.equalsIgnoreCase(text)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("No enum constant for: " + text);
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}