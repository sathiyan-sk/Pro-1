package com.trackerpro.service;

import com.trackerpro.entity.Course;
import com.trackerpro.entity.CourseStatus;
import com.trackerpro.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CourseService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * Get all courses
     */
    public List<Course> getAllCourses() {
        logger.info("Fetching all courses");
        return courseRepository.findAll();
    }
    
    /**
     * Get course by ID
     */
    public Optional<Course> getCourseById(UUID courseId) {
        logger.info("Fetching course by ID: {}", courseId);
        return courseRepository.findById(courseId);
    }
    
    /**
     * Create new course
     */
    public Course createCourse(Course course) {
        logger.info("Creating new course: {}", course.getCourseCode());
        
        // Check if course code already exists
        if (courseRepository.existsByCourseCodeIgnoreCase(course.getCourseCode())) {
            throw new RuntimeException("Course code already exists: " + course.getCourseCode());
        }
        
        // Set default values
        if (course.getStatus() == null) {
            course.setStatus(CourseStatus.DRAFT);
        }
        if (course.getBatchesCount() == null) {
            course.setBatchesCount(0);
        }
        
        Course savedCourse = courseRepository.save(course);
        logger.info("Course created successfully with ID: {}", savedCourse.getCourseId());
        return savedCourse;
    }
    
    /**
     * Update existing course
     */
    public Course updateCourse(UUID courseId, Course updatedCourse) {
        logger.info("Updating course with ID: {}", courseId);
        
        Course existingCourse = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        // Check if new course code conflicts with existing ones (excluding current course)
        if (!existingCourse.getCourseCode().equalsIgnoreCase(updatedCourse.getCourseCode())) {
            if (courseRepository.existsByCourseCodeIgnoreCase(updatedCourse.getCourseCode())) {
                throw new RuntimeException("Course code already exists: " + updatedCourse.getCourseCode());
            }
        }
        
        // Update fields
        existingCourse.setCourseCode(updatedCourse.getCourseCode());
        existingCourse.setCourseTitle(updatedCourse.getCourseTitle());
        existingCourse.setDurationMonths(updatedCourse.getDurationMonths());
        existingCourse.setCategory(updatedCourse.getCategory());
        existingCourse.setPrerequisites(updatedCourse.getPrerequisites());
        existingCourse.setDescription(updatedCourse.getDescription());
        
        if (updatedCourse.getStatus() != null) {
            existingCourse.setStatus(updatedCourse.getStatus());
        }
        
        Course savedCourse = courseRepository.save(existingCourse);
        logger.info("Course updated successfully: {}", savedCourse.getCourseId());
        return savedCourse;
    }
    
    /**
     * Delete course
     */
    public void deleteCourse(UUID courseId) {
        logger.info("Deleting course with ID: {}", courseId);
        
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found with ID: " + courseId);
        }
        
        courseRepository.deleteById(courseId);
        logger.info("Course deleted successfully: {}", courseId);
    }
    
    /**
     * Search courses
     */
    public List<Course> searchCourses(String searchTerm) {
        logger.info("Searching courses with term: {}", searchTerm);
        return courseRepository.searchCourses(searchTerm);
    }
    
    /**
     * Get courses by status
     */
    public List<Course> getCoursesByStatus(CourseStatus status) {
        logger.info("Fetching courses by status: {}", status);
        return courseRepository.findByStatus(status);
    }
    
    /**
     * Get published courses
     */
    public List<Course> getPublishedCourses() {
        logger.info("Fetching published courses");
        return courseRepository.findPublishedCourses();
    }
    
    /**
     * Publish course
     */
    public Course publishCourse(UUID courseId) {
        logger.info("Publishing course with ID: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        course.setStatus(CourseStatus.PUBLISHED);
        Course savedCourse = courseRepository.save(course);
        
        logger.info("Course published successfully: {}", savedCourse.getCourseId());
        return savedCourse;
    }
    
    /**
     * Unpublish course
     */
    public Course unpublishCourse(UUID courseId) {
        logger.info("Unpublishing course with ID: {}", courseId);
        
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        
        course.setStatus(CourseStatus.DRAFT);
        Course savedCourse = courseRepository.save(course);
        
        logger.info("Course unpublished successfully: {}", savedCourse.getCourseId());
        return savedCourse;
    }
    
    /**
     * Get course statistics
     */
    public CourseStatistics getCourseStatistics() {
        logger.info("Calculating course statistics");
        
        long totalCourses = courseRepository.count();
        long publishedCourses = courseRepository.countByStatus(CourseStatus.PUBLISHED);
        long draftCourses = courseRepository.countByStatus(CourseStatus.DRAFT);
        List<String> categories = courseRepository.findAllDistinctCategories();
        
        return new CourseStatistics(totalCourses, publishedCourses, draftCourses, categories.size());
    }
    
    /**
     * Course statistics inner class
     */
    public static class CourseStatistics {
        private final long totalCourses;
        private final long publishedCourses;
        private final long draftCourses;
        private final long totalCategories;
        
        public CourseStatistics(long totalCourses, long publishedCourses, long draftCourses, long totalCategories) {
            this.totalCourses = totalCourses;
            this.publishedCourses = publishedCourses;
            this.draftCourses = draftCourses;
            this.totalCategories = totalCategories;
        }
        
        public long getTotalCourses() { return totalCourses; }
        public long getPublishedCourses() { return publishedCourses; }
        public long getDraftCourses() { return draftCourses; }
        public long getTotalCategories() { return totalCategories; }
    }
}