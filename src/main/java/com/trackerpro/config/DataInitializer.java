package com.trackerpro.config;

import com.trackerpro.entity.*;
import com.trackerpro.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing application data...");
        
        initializeAdmins();
        initializeUsers();
        initializeStudents();
        initializeCourses();
        initializeComplaints();
        
        logger.info("Application data initialization completed");
    }
    
    private void initializeAdmins() {
        if (adminRepository.count() == 0) {
            logger.info("Creating default admin...");
            
            // Create default admin user
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setEmail("admin@tracker.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setStatus(AdminStatus.ACTIVE);
            adminRepository.save(admin);
            
            logger.info("Default admin created successfully with email: admin@tracker.com");
        }
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            logger.info("Creating default users...");
            
            // Create Faculty user
            User faculty = new User();
            faculty.setFirstName("Sarah");
            faculty.setLastName("Johnson");
            faculty.setEmail("sarah.faculty@tracker.com");
            faculty.setPassword(passwordEncoder.encode("faculty123"));
            faculty.setRole(UserRole.FACULTY);
            faculty.setGender(Gender.FEMALE);
            faculty.setCity("Mumbai");
            faculty.setMobileNo("9876543210");
            faculty.setDateOfBirth("22/05/1995");
            faculty.setStatus(UserStatus.ACTIVE);
            userRepository.save(faculty);
            
            // Create HR user
            User hr = new User();
            hr.setFirstName("Mike");
            hr.setLastName("Wilson");
            hr.setEmail("mike.hr@tracker.com");
            hr.setPassword(passwordEncoder.encode("hr123456"));
            hr.setRole(UserRole.HR);
            hr.setGender(Gender.MALE);
            hr.setCity("Delhi");
            hr.setMobileNo("7654321098");
            hr.setDateOfBirth("15/08/1990");
            hr.setStatus(UserStatus.ACTIVE);
            userRepository.save(hr);
            
            logger.info("Default users created successfully");
        }
    }
    
    private void initializeStudents() {
        // No sample students - only real registered users can login
        logger.info("Student initialization skipped - using only real registrations");
    }
    
    private void initializeCourses() {
        // No sample courses - only admin-created courses will appear
        logger.info("Course initialization skipped - using only admin-created courses");
    }
    
    private void initializeComplaints() {
        if (complaintRepository.count() == 0) {
            logger.info("Creating sample complaints...");
            
            // Complaint 1
            Complaint complaint1 = new Complaint();
            complaint1.setCategory("Course Quality");
            complaint1.setPriority(ComplaintPriority.HIGH);
            complaint1.setDescription("Issue with course content quality and outdated materials");
            complaint1.setStatus(ComplaintStatus.OPEN);
            complaint1.setStudentName("John Smith");
            complaint1.setStudentEmail("john.smith@example.com");
            complaintRepository.save(complaint1);
            
            // Complaint 2
            Complaint complaint2 = new Complaint();
            complaint2.setCategory("Faculty Behavior");
            complaint2.setPriority(ComplaintPriority.MEDIUM);
            complaint2.setDescription("Complaint about faculty behavior during class sessions");
            complaint2.setStatus(ComplaintStatus.RESOLVED);
            complaint2.setStudentName("Emily Davis");
            complaint2.setStudentEmail("emily.davis@example.com");
            complaint2.setResolutionNotes("Issue resolved after discussion with faculty member");
            complaintRepository.save(complaint2);
            
            logger.info("Sample complaints created successfully");
        }
    }
}