package com.trackerpro.repository;

import com.trackerpro.entity.Admin;
import com.trackerpro.entity.AdminStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    /**
     * Find admin by email (case-insensitive)
     */
    Optional<Admin> findByEmailIgnoreCase(String email);

    /**
     * Find admin by username (case-insensitive)
     */
    Optional<Admin> findByUsernameIgnoreCase(String username);

    /**
     * Check if email exists (case-insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Check if username exists (case-insensitive)
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Find all admins by status
     */
    List<Admin> findByStatus(AdminStatus status);

    /**
     * Find all active admins
     */
    @Query("SELECT a FROM Admin a WHERE a.status = 'ACTIVE'")
    List<Admin> findActiveAdmins();

    /**
     * Count admins by status
     */
    long countByStatus(AdminStatus status);

    /**
     * Search admins by name or email
     */
    @Query("SELECT a FROM Admin a WHERE " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Admin> searchAdmins(@Param("searchTerm") String searchTerm);
}