package com.trackerpro.service;

import com.trackerpro.entity.User;
import com.trackerpro.entity.UserRole;
import com.trackerpro.entity.UserStatus;
import com.trackerpro.entity.Gender;
import com.trackerpro.repository.UserRepository;
import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.LoginResponse;
import com.trackerpro.exception.UserNotFoundException;
import com.trackerpro.exception.DuplicateEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Authenticate user login (Faculty/HR only)
     */
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Check for regular users (Faculty/HR only)
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(loginRequest.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Check if user is active
                if (user.getStatus() != UserStatus.ACTIVE) {
                    return LoginResponse.failure("Account is not active. Please contact administrator.");
                }
                
                // Verify password
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                        user.getUserId().toString(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getRole().name()
                    );
                    return LoginResponse.success(user.getRole().name(), userInfo);
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
     * Create a new user (Admin/Faculty/HR)
     */
    public User createUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + user.getEmail());
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default status
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get users by role
     */
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * Get active users by role
     */
    public List<User> getActiveUsersByRole(UserRole role) {
        return userRepository.findActiveUsersByRole(role);
    }
    
    /**
     * Search users
     */
    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
    
    /**
     * Update user
     */
    public User updateUser(UUID userId, User updatedUser) {
        User existingUser = getUserById(userId);
        
        // Check if email is being changed and if new email exists
        if (!existingUser.getEmail().equalsIgnoreCase(updatedUser.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(updatedUser.getEmail())) {
                throw new DuplicateEmailException("Email already exists: " + updatedUser.getEmail());
            }
        }
        
        // Update fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setMobileNo(updatedUser.getMobileNo());
        existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        existingUser.setStatus(updatedUser.getStatus());
        
        // Only update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(UUID userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
    
    /**
     * Get user statistics
     */
    public UserStatistics getUserStatistics() {
        UserStatistics stats = new UserStatistics();
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByStatus(UserStatus.ACTIVE));
        stats.setFacultyCount(userRepository.countByRole(UserRole.FACULTY));
        stats.setHrCount(userRepository.countByRole(UserRole.HR));
        return stats;
    }
    
    // Inner class for statistics
    public static class UserStatistics {
        private long totalUsers;
        private long activeUsers;
        private long facultyCount;
        private long hrCount;
        
        // Getters and Setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getActiveUsers() { return activeUsers; }
        public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
        
        public long getFacultyCount() { return facultyCount; }
        public void setFacultyCount(long facultyCount) { this.facultyCount = facultyCount; }
        
        public long getHrCount() { return hrCount; }
        public void setHrCount(long hrCount) { this.hrCount = hrCount; }
    }
}