package com.trackerpro.repository;

import com.trackerpro.entity.User;
import com.trackerpro.entity.UserRole;
import com.trackerpro.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Find user by email (case-insensitive)
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);
    
    // Check if email exists (case-insensitive)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    // Find users by role
    List<User> findByRole(UserRole role);
    
    // Find users by status
    List<User> findByStatus(UserStatus status);
    
    // Find users by role and status
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    
    // Find active users by role
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
    
    // Search users by name or email
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
    
    // Count users by role
    long countByRole(UserRole role);
    
    // Count active users
    long countByStatus(UserStatus status);
}