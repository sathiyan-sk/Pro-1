package com.trackerpro.repository;

import com.trackerpro.entity.Complaint;
import com.trackerpro.entity.ComplaintStatus;
import com.trackerpro.entity.ComplaintPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    
    // Find complaints by status
    List<Complaint> findByStatus(ComplaintStatus status);
    
    // Find complaints by priority
    List<Complaint> findByPriority(ComplaintPriority priority);
    
    // Find complaints by category
    @Query("SELECT c FROM Complaint c WHERE LOWER(c.category) = LOWER(:category)")
    List<Complaint> findByCategoryIgnoreCase(@Param("category") String category);
    
    // Find complaints by student email
    @Query("SELECT c FROM Complaint c WHERE LOWER(c.studentEmail) = LOWER(:email)")
    List<Complaint> findByStudentEmailIgnoreCase(@Param("email") String email);
    
    // Find recent complaints (last N days)
    @Query("SELECT c FROM Complaint c WHERE c.createdAt >= :sinceDate ORDER BY c.createdAt DESC")
    List<Complaint> findRecentComplaints(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Find open complaints ordered by priority
    @Query("SELECT c FROM Complaint c WHERE c.status = 'OPEN' ORDER BY " +
           "CASE c.priority " +
           "WHEN 'URGENT' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 " +
           "WHEN 'LOW' THEN 4 " +
           "END, c.createdAt ASC")
    List<Complaint> findOpenComplaintsByPriority();
    
    // Search complaints by description or student name
    @Query("SELECT c FROM Complaint c WHERE " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.studentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Complaint> searchComplaints(@Param("searchTerm") String searchTerm);
    
    // Count complaints by status
    long countByStatus(ComplaintStatus status);
    
    // Count complaints by priority
    long countByPriority(ComplaintPriority priority);
    
    // Find all distinct categories
    @Query("SELECT DISTINCT c.category FROM Complaint c ORDER BY c.category")
    List<String> findAllDistinctCategories();
}