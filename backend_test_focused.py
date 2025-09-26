#!/usr/bin/env python3
"""
TrackerPro Admin System - Focused Testing for Reported Issues
Tests specific issues: session management, user creation, course editing, API authentication
"""

import requests
import json
import sys
from datetime import datetime
import time
import uuid

class TrackerProFocusedTester:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.tests_run = 0
        self.tests_passed = 0
        self.test_results = []
        self.admin_token = None
        
    def log_test(self, test_name, success, message="", response_data=None):
        """Log test results"""
        self.tests_run += 1
        if success:
            self.tests_passed += 1
            print(f"✅ {test_name}: PASSED - {message}")
        else:
            print(f"❌ {test_name}: FAILED - {message}")
        
        self.test_results.append({
            "test": test_name,
            "success": success,
            "message": message,
            "response_data": response_data
        })
        
    def make_request(self, method, endpoint, data=None, expected_status=200):
        """Make HTTP request and return response"""
        url = f"{self.base_url}{endpoint}"
        headers = {'Content-Type': 'application/json'}
        
        try:
            if method.upper() == 'GET':
                response = self.session.get(url, headers=headers)
            elif method.upper() == 'POST':
                response = self.session.post(url, json=data, headers=headers)
            elif method.upper() == 'PUT':
                response = self.session.put(url, json=data, headers=headers)
            elif method.upper() == 'DELETE':
                response = self.session.delete(url, headers=headers)
            else:
                raise ValueError(f"Unsupported HTTP method: {method}")
                
            return response
            
        except requests.exceptions.RequestException as e:
            print(f"❌ Request failed for {endpoint}: {str(e)}")
            return None

    def test_admin_login_and_session(self):
        """Test admin login and session management"""
        print("\n🔍 Testing Admin Login and Session Management...")
        
        # Test admin login
        admin_creds = {"email": "admin@tracker.com", "password": "admin123"}
        response = self.make_request('POST', '/api/login', admin_creds)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    self.admin_token = data.get('token')  # Store token if provided
                    self.log_test("Admin Login", True, f"Admin login successful, UserType: {data.get('userType')}")
                    return True
                else:
                    self.log_test("Admin Login", False, f"Login failed: {data.get('message', 'Unknown error')}")
                    return False
            except json.JSONDecodeError:
                self.log_test("Admin Login", False, "Invalid JSON response")
                return False
        else:
            status = response.status_code if response else "No response"
            self.log_test("Admin Login", False, f"HTTP Error: {status}")
            return False

    def test_admin_api_authentication(self):
        """Test admin API endpoints authentication"""
        print("\n🔍 Testing Admin API Authentication...")
        
        # Test dashboard stats (should work without explicit auth in current setup)
        response = self.make_request('GET', '/api/admin/dashboard/stats')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    self.log_test("Admin API - Dashboard Stats", True, "Dashboard stats accessible")
                else:
                    self.log_test("Admin API - Dashboard Stats", False, f"API failed: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Admin API - Dashboard Stats", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Admin API - Dashboard Stats", False, f"HTTP Error: {status}")
        
        # Test user management API
        response = self.make_request('GET', '/api/admin/users')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    self.log_test("Admin API - User Management", True, f"Retrieved {len(data.get('data', []))} users")
                else:
                    self.log_test("Admin API - User Management", False, f"API failed: {data.get('message')}")
            except json.JSONDecodeError:
                self.log_test("Admin API - User Management", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Admin API - User Management", False, f"HTTP Error: {status}")

    def test_user_creation_functionality(self):
        """Test user creation in user management section"""
        print("\n🔍 Testing User Creation Functionality...")
        
        # Test creating a new faculty user
        timestamp = int(time.time())
        new_faculty = {
            "firstName": "Test",
            "lastName": "Faculty",
            "email": f"test.faculty.{timestamp}@tracker.com",
            "password": "testpass123",
            "role": "FACULTY",
            "gender": "MALE",
            "city": "Test City",
            "mobileNo": "9876543210",
            "dateOfBirth": "1990-01-15"
        }
        
        response = self.make_request('POST', '/api/admin/users', new_faculty)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    created_user = data.get('data', {})
                    self.log_test("User Creation - Faculty", True, 
                                f"Faculty user created successfully: {created_user.get('email', 'N/A')}")
                    
                    # Test creating HR user
                    new_hr = new_faculty.copy()
                    new_hr["email"] = f"test.hr.{timestamp}@tracker.com"
                    new_hr["role"] = "HR"
                    new_hr["lastName"] = "HR"
                    
                    hr_response = self.make_request('POST', '/api/admin/users', new_hr)
                    
                    if hr_response and hr_response.status_code == 200:
                        hr_data = hr_response.json()
                        if hr_data.get('success', False):
                            self.log_test("User Creation - HR", True, 
                                        f"HR user created successfully: {hr_data.get('data', {}).get('email', 'N/A')}")
                        else:
                            self.log_test("User Creation - HR", False, 
                                        f"HR creation failed: {hr_data.get('message', '')}")
                    else:
                        self.log_test("User Creation - HR", False, "HR creation request failed")
                        
                else:
                    self.log_test("User Creation - Faculty", False, 
                                f"Faculty creation failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("User Creation - Faculty", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("User Creation - Faculty", False, f"HTTP Error: {status}")
        
        # Test duplicate email validation
        duplicate_response = self.make_request('POST', '/api/admin/users', new_faculty)
        if duplicate_response and duplicate_response.status_code == 200:
            try:
                duplicate_data = duplicate_response.json()
                if not duplicate_data.get('success', True):  # Should fail
                    self.log_test("User Creation - Duplicate Email Validation", True, 
                                "Correctly rejected duplicate email")
                else:
                    self.log_test("User Creation - Duplicate Email Validation", False, 
                                "Should have rejected duplicate email")
            except json.JSONDecodeError:
                self.log_test("User Creation - Duplicate Email Validation", False, "Invalid JSON response")

    def test_course_management_apis(self):
        """Test course management APIs - creation, retrieval, and update"""
        print("\n🔍 Testing Course Management APIs...")
        
        # Test get all courses
        response = self.make_request('GET', '/api/admin/courses')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    courses = data.get('data', [])
                    self.log_test("Course Management - Get All Courses", True, 
                                f"Retrieved {len(courses)} courses")
                    
                    # If courses exist, test getting a specific course
                    if courses:
                        course_id = courses[0].get('courseId')
                        if course_id:
                            course_response = self.make_request('GET', f'/api/admin/courses/{course_id}')
                            
                            if course_response and course_response.status_code == 200:
                                course_data = course_response.json()
                                if course_data.get('success', False):
                                    course_details = course_data.get('data', {})
                                    self.log_test("Course Management - Get Course by ID", True, 
                                                f"Retrieved course: {course_details.get('courseTitle', 'N/A')}")
                                    
                                    # Test course update
                                    updated_course = course_details.copy()
                                    updated_course['description'] = f"Updated description - {int(time.time())}"
                                    
                                    update_response = self.make_request('PUT', f'/api/admin/courses/{course_id}', updated_course)
                                    
                                    if update_response and update_response.status_code == 200:
                                        update_data = update_response.json()
                                        if update_data.get('success', False):
                                            self.log_test("Course Management - Update Course", True, 
                                                        "Course updated successfully")
                                        else:
                                            self.log_test("Course Management - Update Course", False, 
                                                        f"Update failed: {update_data.get('message', '')}")
                                    else:
                                        self.log_test("Course Management - Update Course", False, 
                                                    "Update request failed")
                                else:
                                    self.log_test("Course Management - Get Course by ID", False, 
                                                f"Failed to get course: {course_data.get('message', '')}")
                            else:
                                self.log_test("Course Management - Get Course by ID", False, 
                                            "Course retrieval request failed")
                    
                else:
                    self.log_test("Course Management - Get All Courses", False, 
                                f"API failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Course Management - Get All Courses", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Course Management - Get All Courses", False, f"HTTP Error: {status}")
        
        # Test creating a new course
        timestamp = int(time.time())
        new_course = {
            "courseCode": f"TEST{timestamp}",
            "courseTitle": f"Test Course {timestamp}",
            "durationMonths": 6,
            "category": "Web Development",
            "prerequisites": "Basic programming knowledge",
            "description": f"Test course created at {datetime.now()}"
        }
        
        create_response = self.make_request('POST', '/api/admin/courses', new_course)
        
        if create_response and create_response.status_code == 200:
            try:
                create_data = create_response.json()
                if create_data.get('success', False):
                    created_course = create_data.get('data', {})
                    self.log_test("Course Management - Create Course", True, 
                                f"Course created: {created_course.get('courseTitle', 'N/A')}")
                else:
                    self.log_test("Course Management - Create Course", False, 
                                f"Creation failed: {create_data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Course Management - Create Course", False, "Invalid JSON response")
        else:
            status = create_response.status_code if response else "No response"
            self.log_test("Course Management - Create Course", False, f"HTTP Error: {status}")

    def test_logout_functionality(self):
        """Test logout functionality"""
        print("\n🔍 Testing Logout Functionality...")
        
        # Test logout endpoint
        response = self.make_request('POST', '/api/logout')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    self.log_test("Logout API", True, "Logout endpoint working")
                else:
                    self.log_test("Logout API", False, f"Logout failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Logout API", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Logout API", False, f"HTTP Error: {status}")

    def run_focused_tests(self):
        """Run focused tests for reported issues"""
        print("🚀 Starting TrackerPro Admin System - Focused Issue Testing")
        print("=" * 70)
        print("Testing specific issues:")
        print("1. Session management - logout functionality")
        print("2. User creation in user management")
        print("3. Course editing functionality")
        print("4. Admin API authentication")
        print("=" * 70)
        
        # Test admin login and session
        if not self.test_admin_login_and_session():
            print("\n❌ Admin login failed. Some tests may not work properly.")
        
        # Run focused test suites
        self.test_admin_api_authentication()
        self.test_user_creation_functionality()
        self.test_course_management_apis()
        self.test_logout_functionality()
        
        # Print summary
        print("\n" + "=" * 70)
        print("📊 FOCUSED TEST SUMMARY")
        print("=" * 70)
        print(f"Total Tests: {self.tests_run}")
        print(f"Passed: {self.tests_passed}")
        print(f"Failed: {self.tests_run - self.tests_passed}")
        print(f"Success Rate: {(self.tests_passed/self.tests_run)*100:.1f}%")
        
        # Print failed tests
        failed_tests = [result for result in self.test_results if not result['success']]
        if failed_tests:
            print("\n❌ FAILED TESTS:")
            for test in failed_tests:
                print(f"  - {test['test']}: {test['message']}")
        else:
            print("\n✅ All focused tests passed!")
        
        return self.tests_passed == self.tests_run

def main():
    """Main function to run focused tests"""
    tester = TrackerProFocusedTester()
    success = tester.run_focused_tests()
    
    # Return appropriate exit code
    return 0 if success else 1

if __name__ == "__main__":
    sys.exit(main())