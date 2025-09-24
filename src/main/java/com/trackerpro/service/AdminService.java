package com.trackerpro.service;

import com.trackerpro.entity.Admin;
import com.trackerpro.entity.AdminStatus;
import com.trackerpro.repository.AdminRepository;
import com.trackerpro.dto.LoginRequest;
import com.trackerpro.dto.LoginResponse;
import com.trackerpro.exception.UserNotFoundException;
import com.trackerpro.exception.DuplicateEmailException;
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
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticate admin login
     */
    public LoginResponse authenticateAdmin(LoginRequest loginRequest) {
        try {
            // Check by email first
            Optional<Admin> adminOpt = adminRepository.findByEmailIgnoreCase(loginRequest.getEmail());

            // If not found by email, try by username
            if (adminOpt.isEmpty()) {
                adminOpt = adminRepository.findByUsernameIgnoreCase(loginRequest.getEmail());
            }

            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();

                // Check if admin is active
                if (admin.getStatus() != AdminStatus.ACTIVE) {
                    return LoginResponse.failure("Admin account is not active. Please contact support.");
                }

                // Verify password
                if (passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                    // Update last login
                    admin.setLastLogin(LocalDateTime.now());
                    adminRepository.save(admin);

                    LoginResponse.UserInfo adminInfo = new LoginResponse.UserInfo(
                            admin.getAdminId().toString(),
                            admin.getFirstName(),
                            admin.getLastName(),
                            admin.getEmail(),
                            "ADMIN"
                    );
                    return LoginResponse.success("ADMIN", adminInfo);
                } else {
                    return LoginResponse.failure("Invalid credentials");
                }
            }

            return LoginResponse.failure("Invalid credentials");

        } catch (Exception e) {
            return LoginResponse.failure("Login failed. Please try again.");
        }
    }

    /**
     * Create a new admin
     */
    public Admin createAdmin(Admin admin) {
        // Check if email already exists
        if (adminRepository.existsByEmailIgnoreCase(admin.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + admin.getEmail());
        }

        // Check if username already exists
        if (adminRepository.existsByUsernameIgnoreCase(admin.getUsername())) {
            throw new DuplicateEmailException("Username already exists: " + admin.getUsername());
        }

        // Encode password
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));

        // Set default status
        if (admin.getStatus() == null) {
            admin.setStatus(AdminStatus.ACTIVE);
        }

        return adminRepository.save(admin);
    }

    /**
     * Get all admins
     */
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    /**
     * Get active admins
     */
    public List<Admin> getActiveAdmins() {
        return adminRepository.findActiveAdmins();
    }

    /**
     * Get admin by ID
     */
    public Admin getAdminById(UUID adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("Admin not found with id: " + adminId));
    }

    /**
     * Get admin by email
     */
    public Optional<Admin> getAdminByEmail(String email) {
        return adminRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Update admin
     */
    public Admin updateAdmin(UUID adminId, Admin updatedAdmin) {
        Admin existingAdmin = getAdminById(adminId);

        // Check if email is being changed and if new email exists
        if (!existingAdmin.getEmail().equalsIgnoreCase(updatedAdmin.getEmail())) {
            if (adminRepository.existsByEmailIgnoreCase(updatedAdmin.getEmail())) {
                throw new DuplicateEmailException("Email already exists: " + updatedAdmin.getEmail());
            }
        }

        // Check if username is being changed and if new username exists
        if (!existingAdmin.getUsername().equalsIgnoreCase(updatedAdmin.getUsername())) {
            if (adminRepository.existsByUsernameIgnoreCase(updatedAdmin.getUsername())) {
                throw new DuplicateEmailException("Username already exists: " + updatedAdmin.getUsername());
            }
        }

        // Update fields
        existingAdmin.setUsername(updatedAdmin.getUsername());
        existingAdmin.setEmail(updatedAdmin.getEmail());
        existingAdmin.setFirstName(updatedAdmin.getFirstName());
        existingAdmin.setLastName(updatedAdmin.getLastName());
        existingAdmin.setStatus(updatedAdmin.getStatus());

        // Only update password if provided
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
            existingAdmin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
        }

        return adminRepository.save(existingAdmin);
    }

    /**
     * Delete admin
     */
    public void deleteAdmin(UUID adminId) {
        Admin admin = getAdminById(adminId);
        adminRepository.delete(admin);
    }

    /**
     * Search admins
     */
    public List<Admin> searchAdmins(String searchTerm) {
        return adminRepository.searchAdmins(searchTerm);
    }

    /**
     * Get admin statistics
     */
    public AdminStatistics getAdminStatistics() {
        AdminStatistics stats = new AdminStatistics();
        stats.setTotalAdmins(adminRepository.count());
        stats.setActiveAdmins(adminRepository.countByStatus(AdminStatus.ACTIVE));
        stats.setInactiveAdmins(adminRepository.countByStatus(AdminStatus.INACTIVE));
        stats.setSuspendedAdmins(adminRepository.countByStatus(AdminStatus.SUSPENDED));
        return stats;
    }

    // Inner class for statistics
    public static class AdminStatistics {
        private long totalAdmins;
        private long activeAdmins;
        private long inactiveAdmins;
        private long suspendedAdmins;

        // Getters and Setters
        public long getTotalAdmins() { return totalAdmins; }
        public void setTotalAdmins(long totalAdmins) { this.totalAdmins = totalAdmins; }

        public long getActiveAdmins() { return activeAdmins; }
        public void setActiveAdmins(long activeAdmins) { this.activeAdmins = activeAdmins; }

        public long getInactiveAdmins() { return inactiveAdmins; }
        public void setInactiveAdmins(long inactiveAdmins) { this.inactiveAdmins = inactiveAdmins; }

        public long getSuspendedAdmins() { return suspendedAdmins; }
        public void setSuspendedAdmins(long suspendedAdmins) { this.suspendedAdmins = suspendedAdmins; }
    }
}