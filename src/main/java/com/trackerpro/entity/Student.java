package com.trackerpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "student_id")
    private UUID studentId;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;
    
    @Size(max = 30, message = "Last name cannot exceed 30 characters")
    @Column(name = "last_name", length = 30)
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;
    
    @NotBlank(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth; // Format: dd/mm/yyyy
    
    @Min(value = 20, message = "Age must be at least 20")
    @Max(value = 25, message = "Age must not exceed 25")
    @Column(name = "age", nullable = false)
    private Integer age;
    
    @Size(max = 50, message = "Location cannot exceed 50 characters")
    @Column(name = "location", length = 50)
    private String location;
    
    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 10, message = "Mobile number must be 10 digits")
    @Column(name = "mobile_no", nullable = false, length = 10)
    private String mobileNo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StudentStatus status = StudentStatus.REGISTERED;
    
    @CreationTimestamp
    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Student() {}
    
    public Student(String firstName, String lastName, String email, String password, 
                  Gender gender, String dateOfBirth, Integer age, String location, String mobileNo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.location = location;
        this.mobileNo = mobileNo;
    }
    
    // Getters and Setters
    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    
    public StudentStatus getStatus() { return status; }
    public void setStatus(StudentStatus status) { this.status = status; }
    
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", status=" + status +
                '}';
    }
}