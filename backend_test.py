#!/usr/bin/env python3
"""
TrackerPro Backend API Testing Suite
Tests all backend APIs for the Student Project Tracking System
"""

import requests
import json
import sys
from datetime import datetime, timedelta
import time

class TrackerProAPITester:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.tests_run = 0
        self.tests_passed = 0
        self.test_results = []
        
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
    
    def test_server_health(self):
        """Test if server is running and accessible"""
        print("\n🔍 Testing Server Health...")
        
        try:
            response = self.make_request('GET', '/api/auth/status')
            if response and response.status_code in [200, 404]:  # 404 is ok, means server is running
                self.log_test("Server Health Check", True, f"Server is running (Status: {response.status_code})")
                return True
            else:
                self.log_test("Server Health Check", False, f"Server not responding properly")
                return False
        except Exception as e:
            self.log_test("Server Health Check", False, f"Server connection failed: {str(e)}")
            return False
    
    def test_login_functionality(self):
        """Test universal login system"""
        print("\n🔍 Testing Login Functionality...")
        
        # Test credentials from requirements
        test_credentials = [
            {"email": "admin@tracker.com", "password": "admin123", "expected_type": "ADMIN"},
            {"email": "john.smith@example.com", "password": "student123", "expected_type": "STUDENT"},
            {"email": "emily.davis@example.com", "password": "student123", "expected_type": "STUDENT"},
            {"email": "sarah.faculty@tracker.com", "password": "faculty123", "expected_type": "FACULTY"},
            {"email": "mike.hr@tracker.com", "password": "hr123", "expected_type": "HR"}
        ]
        
        for cred in test_credentials:
            response = self.make_request('POST', '/api/login', cred)
            
            if response and response.status_code == 200:
                try:
                    data = response.json()
                    if data.get('success', False):
                        user_type = data.get('userType', '')
                        self.log_test(f"Login - {cred['email']}", True, 
                                    f"Login successful, UserType: {user_type}")
                    else:
                        self.log_test(f"Login - {cred['email']}", False, 
                                    f"Login failed: {data.get('message', 'Unknown error')}")
                except json.JSONDecodeError:
                    self.log_test(f"Login - {cred['email']}", False, "Invalid JSON response")
            else:
                status = response.status_code if response else "No response"
                self.log_test(f"Login - {cred['email']}", False, f"HTTP Error: {status}")
        
        # Test invalid credentials
        invalid_cred = {"email": "invalid@test.com", "password": "wrongpass"}
        response = self.make_request('POST', '/api/login', invalid_cred)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if not data.get('success', True):  # Should fail
                    self.log_test("Login - Invalid Credentials", True, "Correctly rejected invalid login")
                else:
                    self.log_test("Login - Invalid Credentials", False, "Should have rejected invalid login")
            except json.JSONDecodeError:
                self.log_test("Login - Invalid Credentials", False, "Invalid JSON response")
        else:
            self.log_test("Login - Invalid Credentials", False, "Unexpected response for invalid login")
    
    def test_student_registration(self):
        """Test student registration with age validation"""
        print("\n🔍 Testing Student Registration...")
        
        # Test valid registration (age 20-25)
        valid_student = {
            "firstName": "Test",
            "lastName": "Student",
            "email": f"test.student.{int(time.time())}@example.com",
            "password": "testpass123",
            "gender": "MALE",
            "dob": "2001-01-15",  # Age ~23
            "age": 23,
            "location": "Test City",
            "mobileNo": "1234567890"
        }
        
        response = self.make_request('POST', '/api/auth/register', valid_student)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    self.log_test("Student Registration - Valid Age", True, 
                                f"Registration successful: {data.get('message', '')}")
                else:
                    self.log_test("Student Registration - Valid Age", False, 
                                f"Registration failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Student Registration - Valid Age", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Student Registration - Valid Age", False, f"HTTP Error: {status}")
        
        # Test invalid age (under 20)
        invalid_age_student = valid_student.copy()
        invalid_age_student["email"] = f"test.young.{int(time.time())}@example.com"
        invalid_age_student["age"] = 18
        invalid_age_student["dob"] = "2005-01-15"
        
        response = self.make_request('POST', '/api/auth/register', invalid_age_student)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if not data.get('success', True):  # Should fail for age < 20
                    self.log_test("Student Registration - Invalid Age (Under 20)", True, 
                                "Correctly rejected student under 20")
                else:
                    self.log_test("Student Registration - Invalid Age (Under 20)", False, 
                                "Should have rejected student under 20")
            except json.JSONDecodeError:
                self.log_test("Student Registration - Invalid Age (Under 20)", False, "Invalid JSON response")
        
        # Test duplicate email
        duplicate_student = valid_student.copy()
        response = self.make_request('POST', '/api/auth/register', duplicate_student)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if not data.get('success', True):  # Should fail for duplicate email
                    self.log_test("Student Registration - Duplicate Email", True, 
                                "Correctly rejected duplicate email")
                else:
                    self.log_test("Student Registration - Duplicate Email", False, 
                                "Should have rejected duplicate email")
            except json.JSONDecodeError:
                self.log_test("Student Registration - Duplicate Email", False, "Invalid JSON response")
    
    def test_admin_dashboard_apis(self):
        """Test admin dashboard APIs"""
        print("\n🔍 Testing Admin Dashboard APIs...")
        
        # Test dashboard statistics
        response = self.make_request('GET', '/api/admin/dashboard/stats')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False) and 'data' in data:
                    stats = data['data']
                    required_fields = ['totalStudents', 'totalFaculty', 'totalHR', 'newStudentsThisWeek']
                    missing_fields = [field for field in required_fields if field not in stats]
                    
                    if not missing_fields:
                        self.log_test("Admin Dashboard Stats", True, 
                                    f"Stats loaded: {stats}")
                    else:
                        self.log_test("Admin Dashboard Stats", False, 
                                    f"Missing fields: {missing_fields}")
                else:
                    self.log_test("Admin Dashboard Stats", False, 
                                f"Invalid response format: {data}")
            except json.JSONDecodeError:
                self.log_test("Admin Dashboard Stats", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Admin Dashboard Stats", False, f"HTTP Error: {status}")
    
    def test_student_management_apis(self):
        """Test student management APIs"""
        print("\n🔍 Testing Student Management APIs...")
        
        # Test get all registrations
        response = self.make_request('GET', '/api/admin/registrations')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    students = data.get('data', [])
                    self.log_test("Get All Registrations", True, 
                                f"Retrieved {len(students)} student registrations")
                else:
                    self.log_test("Get All Registrations", False, 
                                f"API failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Get All Registrations", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Get All Registrations", False, f"HTTP Error: {status}")
        
        # Test recent registrations
        response = self.make_request('GET', '/api/admin/registrations/recent')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    recent_students = data.get('data', [])
                    self.log_test("Get Recent Registrations", True, 
                                f"Retrieved {len(recent_students)} recent registrations")
                else:
                    self.log_test("Get Recent Registrations", False, 
                                f"API failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Get Recent Registrations", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Get Recent Registrations", False, f"HTTP Error: {status}")
        
        # Test search functionality
        response = self.make_request('GET', '/api/admin/registrations/search?query=john')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    search_results = data.get('data', [])
                    self.log_test("Search Registrations", True, 
                                f"Search returned {len(search_results)} results")
                else:
                    self.log_test("Search Registrations", False, 
                                f"Search failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Search Registrations", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Search Registrations", False, f"HTTP Error: {status}")
    
    def test_user_management_apis(self):
        """Test user management APIs (Faculty/HR)"""
        print("\n🔍 Testing User Management APIs...")
        
        # Test get all users
        response = self.make_request('GET', '/api/admin/users')
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    users = data.get('data', [])
                    self.log_test("Get All Users", True, 
                                f"Retrieved {len(users)} users")
                else:
                    self.log_test("Get All Users", False, 
                                f"API failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Get All Users", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Get All Users", False, f"HTTP Error: {status}")
        
        # Test create new user (Faculty)
        new_user = {
            "firstName": "Test",
            "lastName": "Faculty",
            "email": f"test.faculty.{int(time.time())}@tracker.com",
            "password": "testpass123",
            "role": "FACULTY",
            "gender": "FEMALE",
            "city": "Test City",
            "mobileNo": "9876543210"
        }
        
        response = self.make_request('POST', '/api/admin/users', new_user)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    self.log_test("Create New User (Faculty)", True, 
                                f"User created successfully")
                else:
                    self.log_test("Create New User (Faculty)", False, 
                                f"User creation failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Create New User (Faculty)", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Create New User (Faculty)", False, f"HTTP Error: {status}")
    
    def test_data_persistence(self):
        """Test data persistence by creating and retrieving data"""
        print("\n🔍 Testing Data Persistence...")
        
        # Create a test student
        test_student = {
            "firstName": "Persistence",
            "lastName": "Test",
            "email": f"persistence.test.{int(time.time())}@example.com",
            "password": "testpass123",
            "gender": "MALE",
            "dob": "2000-06-15",
            "age": 24,
            "location": "Persistence City",
            "mobileNo": "5555555555"
        }
        
        # Register the student
        response = self.make_request('POST', '/api/auth/register', test_student)
        
        if response and response.status_code == 200:
            try:
                data = response.json()
                if data.get('success', False):
                    # Now try to login with the same credentials
                    login_data = {
                        "email": test_student["email"],
                        "password": test_student["password"]
                    }
                    
                    login_response = self.make_request('POST', '/api/login', login_data)
                    
                    if login_response and login_response.status_code == 200:
                        login_result = login_response.json()
                        if login_result.get('success', False):
                            self.log_test("Data Persistence", True, 
                                        "Student data persisted and login successful")
                        else:
                            self.log_test("Data Persistence", False, 
                                        "Student registered but login failed")
                    else:
                        self.log_test("Data Persistence", False, 
                                    "Student registered but login request failed")
                else:
                    self.log_test("Data Persistence", False, 
                                f"Student registration failed: {data.get('message', '')}")
            except json.JSONDecodeError:
                self.log_test("Data Persistence", False, "Invalid JSON response")
        else:
            status = response.status_code if response else "No response"
            self.log_test("Data Persistence", False, f"Registration HTTP Error: {status}")
    
    def run_all_tests(self):
        """Run all backend tests"""
        print("🚀 Starting TrackerPro Backend API Tests")
        print("=" * 60)
        
        # Test server health first
        if not self.test_server_health():
            print("\n❌ Server is not accessible. Stopping tests.")
            return False
        
        # Run all test suites
        self.test_login_functionality()
        self.test_student_registration()
        self.test_admin_dashboard_apis()
        self.test_student_management_apis()
        self.test_user_management_apis()
        self.test_data_persistence()
        
        # Print summary
        print("\n" + "=" * 60)
        print("📊 TEST SUMMARY")
        print("=" * 60)
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
        
        return self.tests_passed == self.tests_run

def main():
    """Main function to run tests"""
    tester = TrackerProAPITester()
    success = tester.run_all_tests()
    
    # Return appropriate exit code
    return 0 if success else 1

if __name__ == "__main__":
    sys.exit(main())