# TrackerPro - Student Project Tracking System

A comprehensive student project tracking system built with Spring Boot 3.5.5, Java 21, and H2 database with role-based authentication.

## Features

- **Role-Based Authentication**: Admin, Faculty, HR, and Student roles
- **Student Registration**: Complete registration system with validation
- **Admin Dashboard**: Comprehensive admin interface for managing users and students
- **User Management**: Faculty and HR user management
- **Course Management**: Create and manage courses
- **Complaint System**: Handle student complaints and issues
- **Responsive UI**: Modern, mobile-friendly interface

## Technology Stack

- **Backend**: Spring Boot 3.5.5, Java 21, Spring Security, Spring Data JPA
- **Database**: H2 (development), MySQL ready (production)
- **Frontend**: HTML5, CSS3, JavaScript (existing UI preserved)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.6+ (optional if using wrapper)

### Running the Application

1. **Clone and navigate to project directory**
   ```bash
   cd tracker-pro-spts
   ```

2. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - Main Application: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - Admin Dashboard: http://localhost:8080/admin

### Default Credentials

**Admin Login:**
- Email: `admin@tracker.com`
- Password: `admin123`

**Sample Faculty:**
- Email: `sarah.faculty@tracker.com`
- Password: `faculty123`

**Sample HR:**
- Email: `mike.hr@tracker.com`
- Password: `hr123`

**Sample Student:**
- Email: `john.smith@example.com`
- Password: `student123`

## API Endpoints

### Authentication
- `POST /api/login` - Universal login for all users
- `POST /api/auth/register` - Student registration
- `POST /api/logout` - User logout

### Admin APIs
- `GET /api/admin/dashboard/stats` - Dashboard statistics
- `GET /api/admin/registrations` - All student registrations
- `GET /api/admin/registrations/search?query=` - Search registrations
- `GET /api/admin/users` - All users (Faculty/HR)
- `POST /api/admin/users` - Create new user

## Database Configuration

### H2 Database (Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:trackerprodb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
```

### MySQL Database (Production)
```yaml
spring:
  profiles:
    active: mysql
  datasource:
    url: jdbc:mysql://localhost:3306/trackerprodb
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
```

## Project Structure

```
src/
├── main/
│   ├── java/com/trackerpro/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # JPA repositories
│   │   └── service/        # Business logic
│   └── resources/
│       ├── static/         # Static web content (HTML, CSS, JS, images)
│       └── application.yml # Configuration
└── test/                   # Test classes
```

## Key Features Implemented

### Entity Relationships
- **User Entity**: Admin, Faculty, HR with proper role management
- **Student Entity**: Separate entity for student registrations
- **Course Entity**: Course management with status tracking
- **Complaint Entity**: Complaint management system

### Security Features
- Spring Security configuration
- Password encryption with BCrypt
- CORS configuration for frontend integration
- Role-based access control

### Frontend Integration
- All existing HTML/CSS/JS preserved without changes
- Static file serving through Spring Boot
- API endpoints match frontend expectations
- Proper error handling and validation

## Testing

Run tests with:
```bash
./mvnw test
```

## Database Migration (H2 to MySQL)

To switch to MySQL:

1. **Update application.yml**
   ```yaml
   spring:
     profiles:
       active: mysql
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE trackerprodb;
   ```

3. **Update MySQL connection details** in `application-mysql.yml`

## Development Notes

- All frontend files preserved in `/src/main/resources/static/`
- API endpoints prefixed with `/api/` to match frontend calls
- H2 console available for development at `/h2-console`
- Comprehensive error handling with global exception handler
- Validation on all input fields matching frontend requirements

## Future Enhancements

- JWT token-based authentication
- Email notifications
- File upload capabilities
- Advanced reporting and analytics
- Mobile app integration
- Integration with external learning management systems

## Support

For any issues or questions, please contact the development team.