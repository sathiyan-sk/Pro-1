#!/usr/bin/env python3
"""
TrackerPro MySQL Migration Verification Test Suite
Tests the Spring Boot application after migration from H2 to MySQL database
"""

import requests
import sys
import json
from datetime import datetime
import time

class MySQLMigrationTester:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.tests_run = 0
        self.tests_passed = 0
        self.admin_token = None
        self.student_token = None
        self.faculty_token = None
        self.hr_token = None
        
        # Test data
        self.test_student_email = f"test_student_{datetime.now().strftime('%H%M%S')}@test.com"
        self.test_faculty_email = f"test_faculty_{datetime.now().strftime('%H%M%S')}@test.com"
        self.test_hr_email = f"test_hr_{datetime.now().strftime('%H%M%S')}@test.com"

    def log_test(self, name, success, details=""):
        """Log test results"""
        self.tests_run += 1
        if success:
            self.tests_passed += 1
            print(f"✅ {name} - PASSED {details}")
        else:
            print(f"❌ {name} - FAILED {details}")
        return success

    def make_request(self, method, endpoint, data=None, expected_status=200, headers=None):
        """Make HTTP request and validate response"""
        url = f"{self.base_url}{endpoint}"
        
        if headers is None:
            headers = {'Content-Type': 'application/json'}
        
        try:
            if method == 'GET':
                response = self.session.get(url, headers=headers)
            elif method == 'POST':
                response = self.session.post(url, json=data, headers=headers)
            elif method == 'PUT':
                response = self.session.put(url, json=data, headers=headers)
            elif method == 'DELETE':
                response = self.session.delete(url, headers=headers)
            
            success = response.status_code == expected_status
            response_data = {}
            
            try:
                response_data = response.json()
            except:
                response_data = {"text": response.text}
            
            return success, response_data, response.status_code
            
        except Exception as e:
            print(f"Request error: {str(e)}")
            return False, {"error": str(e)}, 0

    def test_mysql_connectivity(self):
        """Test basic MySQL database connectivity"""
        print("\n🔍 Testing MySQL Database Connectivity...")
        
        # Test basic health endpoint
        success, data, status = self.make_request('GET', '/api/auth/status')
        return self.log_test("MySQL Database Connection", success, 
                           f"Status: {status}")

    def test_admin_authentication(self):
        """Test admin login with MySQL backend"""
        print("\n🔍 Testing Admin Authentication (MySQL)...")
        
        login_data = {
            "email": "admin@tracker.com",
            "password": "admin123"
        }
        
        success, data, status = self.make_request('POST', '/api/login', login_data)
        
        if success and data.get('success'):
            self.admin_token = data.get('token')
            user_type = data.get('userType')
            return self.log_test("Admin Login (MySQL)", True, 
                               f"UserType: {user_type}, Token received")
        else:
            return self.log_test("Admin Login (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_faculty_authentication(self):
        """Test faculty login with MySQL backend"""
        print("\n🔍 Testing Faculty Authentication (MySQL)...")
        
        login_data = {
            "email": "sarah.faculty@tracker.com",
            "password": "faculty123"
        }
        
        success, data, status = self.make_request('POST', '/api/login', login_data)
        
        if success and data.get('success'):
            self.faculty_token = data.get('token')
            user_type = data.get('userType')
            return self.log_test("Faculty Login (MySQL)", True, 
                               f"UserType: {user_type}, Token received")
        else:
            return self.log_test("Faculty Login (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_hr_authentication(self):
        """Test HR login with MySQL backend"""
        print("\n🔍 Testing HR Authentication (MySQL)...")
        
        login_data = {
            "email": "mike.hr@tracker.com",
            "password": "hr123456"
        }
        
        success, data, status = self.make_request('POST', '/api/login', login_data)
        
        if success and data.get('success'):
            self.hr_token = data.get('token')
            user_type = data.get('userType')
            return self.log_test("HR Login (MySQL)", True, 
                               f"UserType: {user_type}, Token received")
        else:
            return self.log_test("HR Login (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_student_registration_mysql(self):
        """Test student registration with age validation (MySQL persistence)"""
        print("\n🔍 Testing Student Registration with MySQL Persistence...")
        
        # Test valid age (within 20-25 range) with all required fields
        registration_data = {
            "firstName": "Test",
            "lastName": "Student",
            "email": self.test_student_email,
            "password": "TestPass123!",
            "age": 22,
            "gender": "Male",
            "dob": "01/01/2002",
            "mobileNo": "1234567890",
            "location": "Test City"
        }
        
        success, data, status = self.make_request('POST', '/api/auth/register', registration_data)
        
        if success and data.get('success'):
            student_id = data.get('studentId')
            return self.log_test("Student Registration (MySQL)", True, 
                               f"StudentID: {student_id}, Age validation passed")
        else:
            return self.log_test("Student Registration (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_student_age_validation(self):
        """Test student age validation (19 years - should fail)"""
        print("\n🔍 Testing Student Age Validation (MySQL)...")
        
        invalid_age_data = {
            "firstName": "Invalid",
            "lastName": "Age",
            "email": f"invalid_age_{datetime.now().strftime('%H%M%S')}@test.com",
            "password": "TestPass123!",
            "age": 19,  # Below minimum age of 20
            "gender": "Female",
            "dob": "01/01/2005",
            "mobileNo": "9876543210",
            "location": "Test City"
        }
        
        success, data, status = self.make_request('POST', '/api/auth/register', invalid_age_data)
        
        # Should fail due to age validation
        if not success or not data.get('success'):
            return self.log_test("Age Validation (19 years)", True, 
                               "Correctly rejected age below 20")
        else:
            return self.log_test("Age Validation (19 years)", False, 
                               "Should have rejected age below 20")

    def test_student_login_mysql(self):
        """Test student login after registration (MySQL persistence)"""
        print("\n🔍 Testing Student Login (MySQL Persistence)...")
        
        login_data = {
            "email": self.test_student_email,
            "password": "TestPass123!"
        }
        
        success, data, status = self.make_request('POST', '/api/login', login_data)
        
        if success and data.get('success'):
            self.student_token = data.get('token')
            user_type = data.get('userType')
            return self.log_test("Student Login (MySQL)", True, 
                               f"UserType: {user_type}, Data persisted correctly")
        else:
            return self.log_test("Student Login (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_dashboard_stats_mysql(self):
        """Test dashboard statistics API with MySQL data"""
        print("\n🔍 Testing Dashboard Statistics API (MySQL)...")
        
        if not self.admin_token:
            return self.log_test("Dashboard Stats (MySQL)", False, "No admin token available")
        
        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {self.admin_token}'
        }
        
        success, data, status = self.make_request('GET', '/api/admin/dashboard/stats', headers=headers)
        
        if success and data.get('success'):
            stats = data.get('data', {})
            required_fields = ['totalStudents', 'totalFaculty', 'totalHR', 'totalCourses']
            
            missing_fields = [field for field in required_fields if field not in stats]
            
            if not missing_fields:
                return self.log_test("Dashboard Stats (MySQL)", True, 
                                   f"All required fields present: {list(stats.keys())}")
            else:
                return self.log_test("Dashboard Stats (MySQL)", False, 
                                   f"Missing fields: {missing_fields}")
        else:
            return self.log_test("Dashboard Stats (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_student_management_mysql(self):
        """Test student management APIs with MySQL"""
        print("\n🔍 Testing Student Management APIs (MySQL)...")
        
        if not self.admin_token:
            return self.log_test("Student Management (MySQL)", False, "No admin token available")
        
        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {self.admin_token}'
        }
        
        # Test get all registrations
        success, data, status = self.make_request('GET', '/api/admin/registrations', headers=headers)
        
        if success and data.get('success'):
            students = data.get('data', [])
            return self.log_test("Student Management (MySQL)", True, 
                               f"Retrieved {len(students)} student records")
        else:
            return self.log_test("Student Management (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_user_creation_mysql(self):
        """Test user creation with MySQL persistence"""
        print("\n🔍 Testing User Creation (MySQL Persistence)...")
        
        if not self.admin_token:
            return self.log_test("User Creation (MySQL)", False, "No admin token available")
        
        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {self.admin_token}'
        }
        
        # Create Faculty user
        faculty_data = {
            "firstName": "Test",
            "lastName": "Faculty",
            "email": self.test_faculty_email,
            "password": "TestFaculty123!",
            "userType": "FACULTY",
            "phoneNumber": "1234567890"
        }
        
        success, data, status = self.make_request('POST', '/api/admin/users', faculty_data, headers=headers)
        
        if success and data.get('success'):
            return self.log_test("User Creation (MySQL)", True, 
                               f"Faculty user created successfully")
        else:
            return self.log_test("User Creation (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_data_persistence_verification(self):
        """Verify data persistence by retrieving created records"""
        print("\n🔍 Testing Data Persistence Verification (MySQL)...")
        
        if not self.admin_token:
            return self.log_test("Data Persistence (MySQL)", False, "No admin token available")
        
        headers = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {self.admin_token}'
        }
        
        # Search for the student we created
        success, data, status = self.make_request('GET', f'/api/admin/registrations/search?query={self.test_student_email}', headers=headers)
        
        if success and data.get('success'):
            students = data.get('data', [])
            found_student = any(student.get('email') == self.test_student_email for student in students)
            
            if found_student:
                return self.log_test("Data Persistence (MySQL)", True, 
                                   "Student data persisted and searchable")
            else:
                return self.log_test("Data Persistence (MySQL)", False, 
                                   "Student data not found in search")
        else:
            return self.log_test("Data Persistence (MySQL)", False, 
                               f"Status: {status}, Response: {data}")

    def test_database_schema_constraints(self):
        """Test database schema constraints and validation"""
        print("\n🔍 Testing Database Schema Constraints (MySQL)...")
        
        # Test duplicate email constraint
        duplicate_data = {
            "firstName": "Duplicate",
            "lastName": "Email",
            "email": self.test_student_email,  # Same email as before
            "password": "TestPass123!",
            "age": 23,
            "phoneNumber": "1234567890",
            "address": "Test Address"
        }
        
        success, data, status = self.make_request('POST', '/api/auth/register', duplicate_data)
        
        # Should fail due to duplicate email constraint
        if not success or not data.get('success'):
            return self.log_test("Database Constraints (MySQL)", True, 
                               "Duplicate email constraint working")
        else:
            return self.log_test("Database Constraints (MySQL)", False, 
                               "Duplicate email should be rejected")

    def run_all_tests(self):
        """Run all MySQL migration tests"""
        print("🚀 Starting TrackerPro MySQL Migration Test Suite")
        print("=" * 60)
        
        # Test sequence
        tests = [
            self.test_mysql_connectivity,
            self.test_admin_authentication,
            self.test_faculty_authentication,
            self.test_hr_authentication,
            self.test_student_registration_mysql,
            self.test_student_age_validation,
            self.test_student_login_mysql,
            self.test_dashboard_stats_mysql,
            self.test_student_management_mysql,
            self.test_user_creation_mysql,
            self.test_data_persistence_verification,
            self.test_database_schema_constraints
        ]
        
        for test in tests:
            try:
                test()
                time.sleep(0.5)  # Brief pause between tests
            except Exception as e:
                self.log_test(test.__name__, False, f"Exception: {str(e)}")
        
        # Print summary
        print("\n" + "=" * 60)
        print(f"📊 MySQL Migration Test Results:")
        print(f"   Tests Run: {self.tests_run}")
        print(f"   Tests Passed: {self.tests_passed}")
        print(f"   Success Rate: {(self.tests_passed/self.tests_run*100):.1f}%")
        
        if self.tests_passed == self.tests_run:
            print("🎉 All MySQL migration tests PASSED!")
            return 0
        else:
            print(f"⚠️  {self.tests_run - self.tests_passed} tests FAILED")
            return 1

def main():
    """Main test execution"""
    tester = MySQLMigrationTester()
    return tester.run_all_tests()

if __name__ == "__main__":
    sys.exit(main())