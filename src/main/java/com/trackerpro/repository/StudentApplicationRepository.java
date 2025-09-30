package com.trackerpro.repository;

import com.trackerpro.entity.StudentApplication;
import com.trackerpro.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentApplicationRepository extends JpaRepository<StudentApplication, UUID> {
    
    // Find application by student ID (since students can only have one application)
    @Query("SELECT sa FROM StudentApplication sa WHERE sa.student.studentId = :studentId")
    Optional<StudentApplication> findByStudentId(@Param("studentId") UUID studentId);
    
    // Check if student already has an application
    @Query("SELECT COUNT(sa) > 0 FROM StudentApplication sa WHERE sa.student.studentId = :studentId")
    boolean existsByStudentId(@Param("studentId") UUID studentId);
    
    // Find all applications by course
    @Query("SELECT sa FROM StudentApplication sa WHERE sa.course.courseId = :courseId")
    List<StudentApplication> findByCourseId(@Param("courseId") UUID courseId);
    
    // Find applications by status
    List<StudentApplication> findByStatus(ApplicationStatus status);
    
    // Find applications by student ID and status
    @Query("SELECT sa FROM StudentApplication sa WHERE sa.student.studentId = :studentId AND sa.status = :status")
    Optional<StudentApplication> findByStudentIdAndStatus(@Param("studentId") UUID studentId, @Param("status") ApplicationStatus status);
    
    // Count applications by status
    long countByStatus(ApplicationStatus status);
    
    // Count applications by course
    @Query("SELECT COUNT(sa) FROM StudentApplication sa WHERE sa.course.courseId = :courseId")
    long countByCourseId(@Param("courseId") UUID courseId);
    
    // Get all applications with student and course details (for admin dashboard)
    @Query("SELECT sa FROM StudentApplication sa " +
           "JOIN FETCH sa.student s " +
           "JOIN FETCH sa.course c " +
           "ORDER BY sa.appliedAt DESC")
    List<StudentApplication> findAllWithDetails();
    
    // Search applications by student name or email
    @Query("SELECT sa FROM StudentApplication sa " +
           "JOIN FETCH sa.student s " +
           "JOIN FETCH sa.course c " +
           "WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY sa.appliedAt DESC")
    List<StudentApplication> searchByStudentDetails(@Param("searchTerm") String searchTerm);
    
    // Get recent applications (last 7 days)
    @Query("SELECT sa FROM StudentApplication sa " +
           "WHERE sa.appliedAt >= :fromDate " +
           "ORDER BY sa.appliedAt DESC")
    List<StudentApplication> findRecentApplications(@Param("fromDate") java.time.LocalDateTime fromDate);
}