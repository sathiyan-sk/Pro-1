#!/usr/bin/env python3
"""
TrackerPro Admin System - Comprehensive Backend API Testing (Iteration 3)
Final verification of admin fixes focusing on:
1. Session management and authentication
2. User creation with validation
3. Course editing and form population
4. All admin API endpoints
"""

import requests
import json
import sys
import time
from datetime import datetime
import uuid

class TrackerProAdminTester:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.admin_token = None
        self.session = requests.Session()
        self.tests_run = 0
        self.tests_passed = 0
        self.test_results = []
        
        # Test data
        self.timestamp = str(int(time.time()))
        self.test_admin_email = "admin@tracker.com"
        self.test_admin_password = "admin123"
        
    def log_test(self, name, success, details="", response_data=None):
        """Log test results"""
        self.tests_run += 1
        if success:
            self.tests_passed += 1
            print(f"✅ {name}")
        else:
            print(f"❌ {name} - {details}")
            
        self.test_results.append({
            "test": name,
            "success": success,
            "details": details,
            "response_data": response_data
        })
        
    def make_request(self, method, endpoint, data=None, headers=None):
        """Make HTTP request with error handling"""
        url = f"{self.base_url}{endpoint}"
        default_headers = {'Content-Type': 'application/json'}
        
        if headers:
            default_headers.update(headers)
            
        try:
            if method == 'GET':
                response = self.session.get(url, headers=default_headers, timeout=10)
            elif method == 'POST':
                response = self.session.post(url, json=data, headers=default_headers, timeout=10)
            elif method == 'PUT':
                response = self.session.put(url, json=data, headers=default_headers, timeout=10)
            elif method == 'DELETE':
                response = self.session.delete(url, headers=default_headers, timeout=10)
            else:
                raise ValueError(f"Unsupported method: {method}")
                
            return response
            
        except requests.exceptions.RequestException as e:
            print(f"Request failed: {e}")
            return None
    
    def test_admin_login(self):
        """Test admin authentication"""
        print("\n🔐 Testing Admin Authentication...")
        
        login_data = {
            "email": self.test_admin_email,
            "password": self.test_admin_password
        }
        
        response = self.make_request('POST', '/api/login', login_data)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success') and data.get('userType') == 'ADMIN':
                    self.admin_token = data.get('token', 'admin_authenticated')
                    self.log_test("Admin Login", True, f"Logged in as {data.get('userType')}")
                    return True
                else:
                    self.log_test("Admin Login", False, f"Login failed: {data.get('message', 'Unknown error')}")
            except json.JSONDecodeError:
                self.log_test("Admin Login", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Admin Login", False, f"HTTP {status}")
            
        return False
    
    def test_dashboard_stats(self):
        """Test dashboard statistics API"""
        print("\n📊 Testing Dashboard Statistics...")
        
        response = self.make_request('GET', '/api/admin/dashboard/stats')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success'):
                    stats = data.get('data', {})
                    required_fields = ['totalStudents', 'totalFaculty', 'totalHR', 'totalCourses', 'publishedCourses']
                    
                    missing_fields = [field for field in required_fields if field not in stats]
                    if not missing_fields:
                        self.log_test("Dashboard Statistics", True, f"All stats present: {stats}")
                        return True
                    else:
                        self.log_test("Dashboard Statistics", False, f"Missing fields: {missing_fields}")
                else:
                    self.log_test("Dashboard Statistics", False, f"API error: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Dashboard Statistics", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Dashboard Statistics", False, f"HTTP {status}")
            
        return False
    
    def test_user_management(self):
        """Test user management APIs"""
        print("\n👥 Testing User Management...")
        
        # Test get all users
        response = self.make_request('GET', '/api/admin/users')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success'):
                    users = data.get('data', [])
                    self.log_test("Get All Users", True, f"Retrieved {len(users)} users")
                else:
                    self.log_test("Get All Users", False, f"API error: {data.get('message')}")
                    return False
            except json.JSONDecodeError:
                self.log_test("Get All Users", False, "Invalid JSON response")
                return False
        else:
            status = response.status_code if response else "No response"
            self.log_test("Get All Users", False, f"HTTP {status}")
            return False
            
        return True
    
    def test_user_creation(self):
        """Test user creation with validation"""
        print("\n➕ Testing User Creation...")
        
        # Test Faculty user creation
        faculty_user = {
            "firstName": "Test",
            "lastName": "Faculty",
            "email": f"test.faculty.{self.timestamp}@tracker.com",
            "password": "TestPass123!",
            "role": "FACULTY",
            "gender": "MALE",
            "city": "Test City",
            "mobileNo": "1234567890",
            "dateOfBirth": "1990-01-01"
        }
        
        response = self.make_request('POST', '/api/admin/users', faculty_user)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success'):
                    created_user = data.get('data', {})
                    self.log_test("Create Faculty User", True, f"Created user: {created_user.get('email')}")
                    
                    # Test HR user creation
                    hr_user = {
                        "firstName": "Test",
                        "lastName": "HR",
                        "email": f"test.hr.{self.timestamp}@tracker.com",
                        "password": "TestPass123!",
                        "role": "HR",
                        "gender": "FEMALE",
                        "city": "Test City",
                        "mobileNo": "1234567891",
                        "dateOfBirth": "1985-01-01"
                    }
                    
                    hr_response = self.make_request('POST', '/api/admin/users', hr_user)
                    
                    if hr_response and hr_response.status_code == 200:
                        hr_data = hr_response.json()
                        if hr_data.get('success'):
                            self.log_test("Create HR User", True, f"Created user: {hr_data.get('data', {}).get('email')}")
                            
                            # Test duplicate email validation
                            duplicate_response = self.make_request('POST', '/api/admin/users', faculty_user)
                            if duplicate_response and duplicate_response.status_code == 200:
                                duplicate_data = duplicate_response.json()
                                if not duplicate_data.get('success'):
                                    self.log_test("Duplicate Email Validation", True, "Correctly rejected duplicate email")
                                    return True
                                else:
                                    self.log_test("Duplicate Email Validation", False, "Should have rejected duplicate email")
                            else:
                                self.log_test("Duplicate Email Validation", False, "Failed to test duplicate email")
                        else:
                            self.log_test("Create HR User", False, f"API error: {hr_data.get('message')}")
                    else:
                        status = hr_response.status_code if hr_response else "No response"
                        self.log_test("Create HR User", False, f"HTTP {status}")
                else:
                    self.log_test("Create Faculty User", False, f"API error: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Create Faculty User", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Create Faculty User", False, f"HTTP {status}")
            
        return False
    
    def test_course_management(self):
        """Test course management APIs including editing"""
        print("\n📚 Testing Course Management...")
        
        # Test get all courses
        response = self.make_request('GET', '/api/admin/courses')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success'):
                    courses = data.get('data', [])
                    self.log_test("Get All Courses", True, f"Retrieved {len(courses)} courses")
                    
                    # Test course creation
                    new_course = {
                        "courseCode": f"TEST{self.timestamp}",
                        "courseTitle": f"Test Course {self.timestamp}",
                        "durationMonths": 6,
                        "category": "Web Development",
                        "prerequisites": "Basic programming knowledge",
                        "description": "This is a test course for validation"
                    }
                    
                    create_response = self.make_request('POST', '/api/admin/courses', new_course)
                    
                    if create_response and create_response.status_code == 200:
                        create_data = create_response.json()
                        if create_data.get('success'):
                            created_course = create_data.get('data', {})
                            course_id = created_course.get('courseId')
                            self.log_test("Create Course", True, f"Created course: {created_course.get('courseCode')}")
                            
                            # Test course retrieval by ID (for form population)
                            if course_id:
                                get_response = self.make_request('GET', f'/api/admin/courses/{course_id}')
                                
                                if get_response and get_response.status_code == 200:
                                    get_data = get_response.json()
                                    if get_data.get('success'):
                                        retrieved_course = get_data.get('data', {})
                                        # Verify all fields are present for form population
                                        required_fields = ['courseCode', 'courseTitle', 'durationMonths', 'category', 'prerequisites', 'description']
                                        missing_fields = [field for field in required_fields if field not in retrieved_course]
                                        
                                        if not missing_fields:
                                            self.log_test("Course Retrieval for Form Population", True, "All course fields available for form population")
                                            
                                            # Test course update (editing)
                                            updated_course = retrieved_course.copy()
                                            updated_course['description'] = f"Updated description {self.timestamp}"
                                            updated_course['prerequisites'] = "Updated prerequisites"
                                            
                                            update_response = self.make_request('PUT', f'/api/admin/courses/{course_id}', updated_course)
                                            
                                            if update_response and update_response.status_code == 200:
                                                update_data = update_response.json()
                                                if update_data.get('success'):
                                                    self.log_test("Course Update/Editing", True, "Course updated successfully")
                                                    return True
                                                else:
                                                    self.log_test("Course Update/Editing", False, f"Update failed: {update_data.get('message')}")
                                            else:
                                                status = update_response.status_code if update_response else "No response"
                                                self.log_test("Course Update/Editing", False, f"HTTP {status}")
                                        else:
                                            self.log_test("Course Retrieval for Form Population", False, f"Missing fields: {missing_fields}")
                                    else:
                                        self.log_test("Course Retrieval for Form Population", False, f"API error: {get_data.get('message')}")
                                else:
                                    status = get_response.status_code if get_response else "No response"
                                    self.log_test("Course Retrieval for Form Population", False, f"HTTP {status}")
                            else:
                                self.log_test("Course Retrieval for Form Population", False, "No course ID returned")
                        else:
                            self.log_test("Create Course", False, f"API error: {create_data.get('message')}")
                    else:
                        status = create_response.status_code if create_response else "No response"
                        self.log_test("Create Course", False, f"HTTP {status}")
                else:
                    self.log_test("Get All Courses", False, f"API error: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Get All Courses", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Get All Courses", False, f"HTTP {status}")
            
        return False
    
    def test_registrations_management(self):
        """Test student registrations management"""
        print("\n📝 Testing Registrations Management...")
        
        # Test get all registrations
        response = self.make_request('GET', '/api/admin/registrations')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success'):
                    registrations = data.get('data', [])
                    self.log_test("Get All Registrations", True, f"Retrieved {len(registrations)} registrations")
                    
                    # Test recent registrations
                    recent_response = self.make_request('GET', '/api/admin/registrations/recent')
                    
                    if recent_response and recent_response.status_code == 200:
                        recent_data = recent_response.json()
                        if recent_data.get('success'):
                            recent_registrations = recent_data.get('data', [])
                            self.log_test("Get Recent Registrations", True, f"Retrieved {len(recent_registrations)} recent registrations")
                            
                            # Test search functionality
                            search_response = self.make_request('GET', '/api/admin/registrations/search?query=test')
                            
                            if search_response and search_response.status_code == 200:
                                search_data = search_response.json()
                                if search_data.get('success'):
                                    search_results = search_data.get('data', [])
                                    self.log_test("Search Registrations", True, f"Search returned {len(search_results)} results")
                                    return True
                                else:
                                    self.log_test("Search Registrations", False, f"API error: {search_data.get('message')}")
                            else:
                                status = search_response.status_code if search_response else "No response"
                                self.log_test("Search Registrations", False, f"HTTP {status}")
                        else:
                            self.log_test("Get Recent Registrations", False, f"API error: {recent_data.get('message')}")
                    else:
                        status = recent_response.status_code if recent_response else "No response"
                        self.log_test("Get Recent Registrations", False, f"HTTP {status}")
                else:
                    self.log_test("Get All Registrations", False, f"API error: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Get All Registrations", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Get All Registrations", False, f"HTTP {status}")
            
        return False
    
    def test_logout_functionality(self):
        """Test logout API endpoint"""
        print("\n🚪 Testing Logout Functionality...")
        
        response = self.make_request('POST', '/api/logout')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success'):
                    self.log_test("Logout API", True, "Logout endpoint working")
                    return True
                else:
                    self.log_test("Logout API", False, f"API error: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Logout API", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Logout API", False, f"HTTP {status}")
            
        return False
    
    def run_all_tests(self):
        """Run comprehensive test suite"""
        print("🚀 Starting TrackerPro Admin System - Comprehensive Backend Testing (Iteration 3)")
        print("=" * 80)
        
        # Test authentication first
        if not self.test_admin_login():
            print("❌ Admin authentication failed - cannot proceed with other tests")
            return False
            
        # Run all admin functionality tests
        self.test_dashboard_stats()
        self.test_user_management()
        self.test_user_creation()
        self.test_course_management()
        self.test_registrations_management()
        self.test_logout_functionality()
        
        # Print summary
        print("\n" + "=" * 80)
        print("📊 TEST SUMMARY")
        print("=" * 80)
        print(f"Tests Run: {self.tests_run}")
        print(f"Tests Passed: {self.tests_passed}")
        print(f"Success Rate: {(self.tests_passed/self.tests_run*100):.1f}%")
        
        if self.tests_passed == self.tests_run:
            print("🎉 ALL TESTS PASSED - Admin system is working perfectly!")
            return True
        else:
            print(f"⚠️  {self.tests_run - self.tests_passed} tests failed")
            return False

def main():
    tester = TrackerProAdminTester()
    success = tester.run_all_tests()
    return 0 if success else 1

if __name__ == "__main__":
    sys.exit(main())