package com.trackerpro.dto;

import jakarta.validation.constraints.*;

public class StudentRegistrationRequest {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    private String firstName;
    
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    private String lastName;
    
    @NotBlank(message = "Gender is required")
    private String gender;
    
    @NotBlank(message = "Date of birth is required")
    private String dob; // Format: dd/mm/yyyy
    
    @Size(max = 50, message = "Location cannot exceed 50 characters")
    private String location;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    private String mobileNo;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
    private String password;
    
    @Min(value = 20, message = "Age must be at least 20")
    @Max(value = 25, message = "Age must not exceed 25")
    private Integer age;
    
    // Constructors
    public StudentRegistrationRequest() {}
    
    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    @Override
    public String toString() {
        return "StudentRegistrationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                '}';
    }
}