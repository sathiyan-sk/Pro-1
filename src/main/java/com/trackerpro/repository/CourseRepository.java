package com.trackerpro.repository;

import com.trackerpro.entity.Course;
import com.trackerpro.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    
    // Find course by code (case-insensitive)
    @Query("SELECT c FROM Course c WHERE LOWER(c.courseCode) = LOWER(:courseCode)")
    Optional<Course> findByCourseCodeIgnoreCase(@Param("courseCode") String courseCode);
    
    // Check if course code exists (case-insensitive)
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE LOWER(c.courseCode) = LOWER(:courseCode)")
    boolean existsByCourseCodeIgnoreCase(@Param("courseCode") String courseCode);
    
    // Find courses by status
    List<Course> findByStatus(CourseStatus status);
    
    // Find courses by category
    @Query("SELECT c FROM Course c WHERE LOWER(c.category) = LOWER(:category)")
    List<Course> findByCategoryIgnoreCase(@Param("category") String category);
    
    // Search courses by title or code
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.courseTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchCourses(@Param("searchTerm") String searchTerm);
    
    // Find published courses
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' ORDER BY c.createdAt DESC")
    List<Course> findPublishedCourses();
    
    // Find courses by duration range
    @Query("SELECT c FROM Course c WHERE c.durationMonths BETWEEN :minDuration AND :maxDuration")
    List<Course> findByDurationBetween(@Param("minDuration") Integer minDuration, 
                                      @Param("maxDuration") Integer maxDuration);
    
    // Count courses by status
    long countByStatus(CourseStatus status);
    
    // Count courses by category
    @Query("SELECT COUNT(c) FROM Course c WHERE LOWER(c.category) = LOWER(:category)")
    long countByCategoryIgnoreCase(@Param("category") String category);
    
    // Find all distinct categories
    @Query("SELECT DISTINCT c.category FROM Course c ORDER BY c.category")
    List<String> findAllDistinctCategories();
}