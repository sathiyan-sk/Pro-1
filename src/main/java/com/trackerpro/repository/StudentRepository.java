package com.trackerpro.repository;

import com.trackerpro.entity.Student;
import com.trackerpro.entity.StudentStatus;
import com.trackerpro.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    
    // Find student by email (case-insensitive)
    @Query("SELECT s FROM Student s WHERE LOWER(s.email) = LOWER(:email)")
    Optional<Student> findByEmailIgnoreCase(@Param("email") String email);
    
    // Check if email exists (case-insensitive)
    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE LOWER(s.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    // Find students by status
    List<Student> findByStatus(StudentStatus status);
    
    // Find students by gender
    List<Student> findByGender(Gender gender);
    
    // Search students by name or email
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Student> searchStudents(@Param("searchTerm") String searchTerm);
    
    // Find students registered within date range
    @Query("SELECT s FROM Student s WHERE s.registeredAt BETWEEN :startDate AND :endDate")
    List<Student> findStudentsRegisteredBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    // Find recent students (last N days)
    @Query("SELECT s FROM Student s WHERE s.registeredAt >= :sinceDate ORDER BY s.registeredAt DESC")
    List<Student> findRecentStudents(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Count students by status
    long countByStatus(StudentStatus status);
    
    // Count students by gender
    long countByGender(Gender gender);
    
    // Count students registered today
    @Query("SELECT COUNT(s) FROM Student s WHERE DATE(s.registeredAt) = CURRENT_DATE")
    long countStudentsRegisteredToday();
    
    // Count students registered this week
    @Query("SELECT COUNT(s) FROM Student s WHERE s.registeredAt >= :weekAgo")
    long countStudentsRegisteredThisWeek(@Param("weekAgo") LocalDateTime weekAgo);
    
    // Find students by age range
    @Query("SELECT s FROM Student s WHERE s.age BETWEEN :minAge AND :maxAge")
    List<Student> findByAgeBetween(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    // Find students by location
    @Query("SELECT s FROM Student s WHERE LOWER(s.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Student> findByLocationContainingIgnoreCase(@Param("location") String location);
}