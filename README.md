# TrackerPro - Student Project Tracking System

A comprehensive student project tracking system built with **Spring Boot 3.5.5**, **Java 21**, and **H2 database** with **role-based authentication** and modern web interface.

## 🚀 Features

### ✅ **Authentication System**
- **Admin Login**: Full system administration capabilities
- **Faculty/HR Login**: User management and academic oversight  
- **Student Registration & Login**: Self-service registration and access
- **Secure Authentication**: BCrypt password encryption with Spring Security

### ✅ **Admin Dashboard**  
- **Real-time Statistics**: Student counts, faculty metrics, course data
- **Student Management**: View all registrations, search, and export data
- **User Management**: Create and manage Faculty/HR accounts
- **Course Management**: Create, publish, and manage academic courses
- **Complaint System**: Handle student complaints and feedback
- **System Settings**: Configure application parameters

### ✅ **Student Features**
- **Easy Registration**: Age-validated registration (20-25 years)
- **Secure Login**: Individual student accounts
- **Profile Management**: Personal information updates

### ✅ **Technical Architecture**
- **Backend**: Spring Boot 3.5.5 with Spring Security, JPA, and Validation
- **Database**: H2 (development) with MySQL production support
- **Frontend**: Modern responsive HTML/CSS/JavaScript interface
- **API Design**: RESTful endpoints with comprehensive error handling

---

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Backend** | Spring Boot | 3.5.5 |
| **Language** | Java | 21 |
| **Database** | H2 Database | In-Memory |
| **Security** | Spring Security | Latest |
| **Frontend** | HTML/CSS/JavaScript | ES6+ |
| **Build Tool** | Maven | 3.6+ |
| **Testing** | JUnit 5 | Latest |

---

## 📋 Prerequisites

Before running the application, ensure you have:

- ☑️ **Java 21** or higher installed
- ☑️ **Maven 3.6+** (optional if using wrapper)
- ☑️ **Git** for version control
- ☑️ **Web Browser** (Chrome, Firefox, Safari, Edge)

---

## 🚀 Quick Start Guide

### **Step 1: Clone the Repository**
```bash
git clone <repository-url>
cd tracker-pro-spts
```

### **Step 2: Build the Application**
```bash
# Using Maven wrapper (recommended)
./mvnw clean install

# Or using system Maven
mvn clean install
```

### **Step 3: Run the Application**
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using system Maven  
mvn spring-boot:run

# Or run the JAR file directly
java -jar target/tracker-pro-spts-0.0.1-SNAPSHOT.jar
```

### **Step 4: Access the Application**
Once the application starts successfully, you can access:

- 🌐 **Main Application**: http://localhost:8080
- 📊 **H2 Database Console**: http://localhost:8080/h2-console
- 👨‍💼 **Admin Dashboard**: http://localhost:8080/adminPage.html
- 🔐 **Login Page**: http://localhost:8080/loginPage.html
- 📝 **Registration Page**: http://localhost:8080/registerPage.html

---

## 👤 Default User Accounts

The application comes with pre-configured accounts for immediate testing:

### **🔑 Admin Account**
- **Email**: `admin@tracker.com`
- **Password**: `admin123`
- **Access**: Full system administration

### **👨‍🏫 Faculty Account**
- **Email**: `sarah.faculty@tracker.com`
- **Password**: `faculty123`
- **Access**: Academic management functions

### **👔 HR Account**  
- **Email**: `mike.hr@tracker.com`
- **Password**: `hr123456`
- **Access**: Human resources functions

### **🎓 Sample Students**
- **Student 1**: `john.smith@example.com` / `student123`
- **Student 2**: `emily.davis@example.com` / `student123`

---

## 📊 H2 Database Access

For development and debugging purposes:

1. **Navigate to**: http://localhost:8080/h2-console
2. **Connection Settings**:
   - **JDBC URL**: `jdbc:h2:mem:trackerprodb`
   - **Username**: `sa`
   - **Password**: `password`
3. **Click Connect** to access the database console

### **Database Tables**
- `admins` - Admin user accounts
- `users` - Faculty and HR accounts  
- `students` - Student registrations
- `courses` - Academic course catalog
- `complaints` - Student complaint system

---

## 🔌 API Endpoints

### **Authentication APIs**
```http
POST /api/login                    # Universal login endpoint
POST /api/auth/register             # Student registration
POST /api/logout                    # User logout
```

### **Admin APIs**
```http
GET  /api/admin/dashboard/stats     # Dashboard statistics
GET  /api/admin/registrations       # All student registrations
GET  /api/admin/registrations/search?query=  # Search registrations
GET  /api/admin/registrations/recent # Recent registrations
GET  /api/admin/users              # All users (Faculty/HR)
POST /api/admin/users              # Create new user
GET  /api/admin/admins             # All admin accounts
POST /api/admin/admins             # Create new admin
```

### **API Response Format**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* Response data */ }
}
```

---

## 🎯 Complete Usage Workflow

### **For Administrators:**

1. **Login**: Navigate to http://localhost:8080/loginPage.html
2. **Use Admin Credentials**: `admin@tracker.com` / `admin123`
3. **Dashboard Access**: View real-time system statistics
4. **Manage Students**: View and search all student registrations
5. **Manage Users**: Create Faculty/HR accounts
6. **System Administration**: Configure system settings

### **For Students:**

1. **Register**: Go to http://localhost:8080/registerPage.html
2. **Fill Details**: Complete registration form (age 20-25 required)
3. **Login**: Use your registered credentials
4. **Access Services**: Use student dashboard features

### **For Faculty/HR:**

1. **Login**: Use provided credentials or admin-created accounts
2. **Access Role-Based Features**: Academic or HR management functions

---

## 🗂️ Project Structure

```
tracker-pro-spts/
├── src/
│   ├── main/
│   │   ├── java/com/trackerpro/
│   │   │   ├── config/          # Security & data initialization
│   │   │   ├── controller/      # REST API controllers  
│   │   │   ├── dto/            # Data transfer objects
│   │   │   ├── entity/         # JPA entities & database models
│   │   │   ├── exception/      # Custom exception handling
│   │   │   ├── repository/     # Database repositories
│   │   │   └── service/        # Business logic services
│   │   └── resources/
│   │       ├── static/         # Frontend files (HTML/CSS/JS)
│   │       └── application.yml # Application configuration
│   └── test/                   # Test classes and resources
├── target/                     # Build output directory
├── pom.xml                     # Maven configuration
└── README.md                   # This documentation file
```

---

## 🔧 Configuration

### **Application Properties** (`application.yml`)
```yaml
server:
  port: 8080                    # Application port

spring:
  datasource:
    url: jdbc:h2:mem:trackerprodb    # H2 database URL
    username: sa                      # Database username
    password: password                # Database password
  
  jpa:
    hibernate:
      ddl-auto: create-drop          # Database schema management
    show-sql: true                   # SQL query logging
```

### **Environment Profiles**
- **Development**: H2 in-memory database (default)
- **Production**: MySQL database support available

---

## 🧪 Testing the Application

### **Manual Testing Checklist**

✅ **Authentication Testing**
- [ ] Admin login with correct credentials
- [ ] Student registration with valid data (age 20-25)
- [ ] Login with registered student account
- [ ] Faculty/HR login functionality

✅ **Admin Dashboard Testing**  
- [ ] Dashboard statistics loading
- [ ] Student registrations display
- [ ] User management functions
- [ ] Search and filter capabilities

✅ **Data Persistence Testing**
- [ ] New student registrations appear in admin dashboard
- [ ] User creation through admin panel
- [ ] Data consistency across sessions

### **API Testing with cURL**

**Test Admin Dashboard Stats:**
```bash
curl -X GET http://localhost:8080/api/admin/dashboard/stats \
  -H "Content-Type: application/json"
```

**Test Student Registration:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Student", 
    "email": "test@example.com",
    "password": "password123",
    "age": 22,
    "gender": "Male",
    "dob": "01/01/2002",
    "location": "Test City",
    "mobileNo": "9999999999"
  }'
```

**Test Login:**
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@tracker.com",
    "password": "admin123"
  }'
```

---

## 🚀 Production Deployment

### **Database Migration to MySQL**

1. **Update application.yml:**
```yaml
spring:
  profiles:
    active: mysql
  datasource:
    url: jdbc:mysql://localhost:3306/trackerprodb
    username: your_mysql_username
    password: your_mysql_password
```

2. **Create MySQL Database:**
```sql
CREATE DATABASE trackerprodb;
```

3. **Update Maven dependencies** if needed for specific MySQL connector version

---

## 🤝 Contributing

1. **Fork the repository**
2. **Create feature branch**: `git checkout -b feature/new-feature`
3. **Commit changes**: `git commit -am 'Add new feature'`
4. **Push to branch**: `git push origin feature/new-feature`
5. **Submit pull request**

---

## 📋 Troubleshooting

### **Common Issues & Solutions**

**❌ Port 8080 already in use**
```bash
# Kill process on port 8080
sudo kill -9 $(sudo lsof -t -i:8080)
# Or change port in application.yml
```

**❌ Database connection issues**
```bash
# Check H2 console connection settings
# Verify JDBC URL: jdbc:h2:mem:trackerprodb
```

**❌ Application won't start**
```bash
# Check Java version
java --version
# Ensure Java 21+ is installed
```

**❌ Frontend not loading data**
```bash
# Check browser console for API errors
# Verify backend server is running
# Check network requests in developer tools
```

---

## 📞 Support & Contact

For support, bug reports, or feature requests:
- **Email**: support@trackerpro.com
- **Issue Tracker**: Create GitHub issues for bugs
- **Documentation**: Refer to inline code comments

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🎉 Acknowledgments

- **Spring Boot Community** for excellent framework
- **H2 Database** for development convenience  
- **Bootstrap & Modern CSS** for responsive design inspiration
- **Maven** for dependency management

---

**🎯 Ready to use TrackerPro? Start with the Quick Start Guide above!**