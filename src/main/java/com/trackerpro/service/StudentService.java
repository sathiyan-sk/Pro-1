package com.trackerpro.service;

import com.trackerpro.entity.Student;
import com.trackerpro.entity.StudentStatus;
import com.trackerpro.entity.Gender;
import com.trackerpro.repository.StudentRepository;
import com.trackerpro.dto.StudentRegistrationRequest;
import com.trackerpro.dto.RegistrationResponse;
import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.LoginResponse;
import com.trackerpro.exception.DuplicateEmailException;
import com.trackerpro.exception.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new student
     */
    public RegistrationResponse registerStudent(StudentRegistrationRequest request) {
        try {
            // Check if email already exists
            if (studentRepository.existsByEmailIgnoreCase(request.getEmail())) {
                return RegistrationResponse.failure("Email already exists. Please use a different email.");
            }
            
            // Create new student entity
            Student student = new Student();
            student.setFirstName(request.getFirstName());
            student.setLastName(request.getLastName());
            student.setEmail(request.getEmail());
            student.setPassword(passwordEncoder.encode(request.getPassword()));
            student.setGender(Gender.fromString(request.getGender()));
            student.setDateOfBirth(request.getDob());
            student.setAge(request.getAge());
            student.setLocation(request.getLocation());
            student.setMobileNo(request.getMobileNo());
            student.setStatus(StudentStatus.REGISTERED);
            
            // Save student
            Student savedStudent = studentRepository.save(student);
            
            return RegistrationResponse.success(
                "Registration successful! Welcome to TrackerPro.", 
                savedStudent.getStudentId().toString()
            );
            
        } catch (IllegalArgumentException e) {
            return RegistrationResponse.failure("Invalid gender value provided.");
        } catch (Exception e) {
            return RegistrationResponse.failure("Registration failed. Please try again.");
        }
    }
    
    /**
     * Authenticate student login
     */
    public LoginResponse authenticateStudent(LoginRequest loginRequest) {
        try {
            Optional<Student> studentOpt = studentRepository.findByEmailIgnoreCase(loginRequest.getEmail());
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                
                // Check if student is active
                if (student.getStatus() == StudentStatus.SUSPENDED) {
                    return LoginResponse.failure("Account is suspended. Please contact administrator.");
                }
                
                // Verify password
                if (passwordEncoder.matches(loginRequest.getPassword(), student.getPassword())) {
                    LoginResponse.UserInfo studentInfo = new LoginResponse.UserInfo(
                        student.getStudentId().toString(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getEmail(),
                        "STUDENT"
                    );
                    return LoginResponse.success("STUDENT", studentInfo);
                } else {
                    return LoginResponse.failure("Invalid email or password");
                }
            }
            
            return LoginResponse.failure("Invalid email or password");
            
        } catch (Exception e) {
            return LoginResponse.failure("Login failed. Please try again.");
        }
    }
    
    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    /**
     * Get students by status
     */
    public List<Student> getStudentsByStatus(StudentStatus status) {
        return studentRepository.findByStatus(status);
    }
    
    /**
     * Search students
     */
    public List<Student> searchStudents(String searchTerm) {
        return studentRepository.searchStudents(searchTerm);
    }
    
    /**
     * Get student by ID
     */
    public Student getStudentById(UUID studentId) {
        return studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + studentId));
    }
    
    /**
     * Get recent students (last 7 days)
     */
    public List<Student> getRecentStudents() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return studentRepository.findRecentStudents(weekAgo);
    }
    
    /**
     * Update student
     */
    public Student updateStudent(UUID studentId, Student updatedStudent) {
        Student existingStudent = getStudentById(studentId);
        
        // Check if email is being changed and if new email exists
        if (!existingStudent.getEmail().equalsIgnoreCase(updatedStudent.getEmail())) {
            if (studentRepository.existsByEmailIgnoreCase(updatedStudent.getEmail())) {
                throw new DuplicateEmailException("Email already exists: " + updatedStudent.getEmail());
            }
        }
        
        // Update fields
        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setGender(updatedStudent.getGender());
        existingStudent.setDateOfBirth(updatedStudent.getDateOfBirth());
        existingStudent.setAge(updatedStudent.getAge());
        existingStudent.setLocation(updatedStudent.getLocation());
        existingStudent.setMobileNo(updatedStudent.getMobileNo());
        existingStudent.setStatus(updatedStudent.getStatus());
        
        // Only update password if provided
        if (updatedStudent.getPassword() != null && !updatedStudent.getPassword().isEmpty()) {
            existingStudent.setPassword(passwordEncoder.encode(updatedStudent.getPassword()));
        }
        
        return studentRepository.save(existingStudent);
    }
    
    /**
     * Delete student
     */
    public void deleteStudent(UUID studentId) {
        Student student = getStudentById(studentId);
        studentRepository.delete(student);
    }
    
    /**
     * Get student statistics
     */
    public StudentStatistics getStudentStatistics() {
        StudentStatistics stats = new StudentStatistics();
        stats.setTotalStudents(studentRepository.count());
        stats.setRegisteredStudents(studentRepository.countByStatus(StudentStatus.REGISTERED));
        stats.setEnrolledStudents(studentRepository.countByStatus(StudentStatus.ENROLLED));
        stats.setCompletedStudents(studentRepository.countByStatus(StudentStatus.COMPLETED));
        
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        stats.setNewStudentsThisWeek(studentRepository.countStudentsRegisteredThisWeek(weekAgo));
        
        return stats;
    }
    
    // Inner class for statistics
    public static class StudentStatistics {
        private long totalStudents;
        private long registeredStudents;
        private long enrolledStudents;
        private long completedStudents;
        private long newStudentsThisWeek;
        
        // Getters and Setters
        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
        
        public long getRegisteredStudents() { return registeredStudents; }
        public void setRegisteredStudents(long registeredStudents) { this.registeredStudents = registeredStudents; }
        
        public long getEnrolledStudents() { return enrolledStudents; }
        public void setEnrolledStudents(long enrolledStudents) { this.enrolledStudents = enrolledStudents; }
        
        public long getCompletedStudents() { return completedStudents; }
        public void setCompletedStudents(long completedStudents) { this.completedStudents = completedStudents; }
        
        public long getNewStudentsThisWeek() { return newStudentsThisWeek; }
        public void setNewStudentsThisWeek(long newStudentsThisWeek) { this.newStudentsThisWeek = newStudentsThisWeek; }
    }
}