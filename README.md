# Digital Patient Record Management System

A hospital-grade, secure web application for managing patient records. Built with Spring Boot, PostgreSQL, and JWT authentication.

## 🏥 System Overview

This system provides **role-based access control** with three distinct user roles:

### 1. **ADMIN** (Super User)
- Create, activate, and deactivate Doctor and Nurse accounts
- View all patients, records, appointments, and system data
- Full system control and oversight
- **Fixed account** - cannot be deleted

### 2. **DOCTOR**
- View assigned patients
- Create, update, and delete patient medical records
- Add diagnosis, prescriptions, lab results, and visit notes
- View appointment schedules

### 3. **NURSE**
- View assigned patients
- Update patient vitals (BP, temperature, weight, blood sugar, etc.)
- Add new patients
- Update visit status and admission/discharge information
- **Cannot** modify doctor diagnosis or system settings

## 🔐 Security Features

- **JWT Authentication** - Secure token-based authentication
- **Role-based Access Control** - Each role can only access authorized routes
- **No Public Registration** - Only Admin can create user accounts
- **BCrypt Password Hashing** - Secure password storage
- **Account Activation** - Admin controls user activation status

## 🚀 Technology Stack

- **Backend**: Spring Boot 3.5.7
- **Database**: PostgreSQL
- **Frontend**: Thymeleaf Templates
- **Security**: JWT (JSON Web Tokens)
- **Java Version**: 21

## 📋 Prerequisites

1. **Java 21** or higher
2. **PostgreSQL** (version 12 or higher)
3. **Gradle** (included via wrapper)

## 🗄️ Database Setup

### Step 1: Create Database

```sql
CREATE DATABASE DigitalPatientRecordSystem;
```

### Step 2: Run Schema Script

**Option A: Clean Setup (Recommended for first time)**
```bash
psql -U your_username -d DigitalPatientRecordSystem -f DATABASE_SCHEMA.sql
```

**Option B: If you have existing data and getting migration errors**
```bash
# First, run the quick fix to reset users table
psql -U your_username -d DigitalPatientRecordSystem -f QUICK_FIX.sql

# Then run the full schema (it will skip users table if it exists)
psql -U your_username -d DigitalPatientRecordSystem -f DATABASE_SCHEMA.sql
```

Or manually run the SQL commands from the script files.

### Step 3: Configure Database Connection

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/DigitalPatientRecordSystem
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Or set environment variables:
- `DPRS_DB_EMAIL`: PostgreSQL username
- `DPRS_DB_PASSWORD`: PostgreSQL password
- `DPRS_ADMIN_PASSWORD`: Admin password (default: `admin123`)

## 🛠️ Installation & Setup

### Step 1: Clone/Download Project

```bash
cd Project-DigitalPatientRecordSystem
```

### Step 2: Set Environment Variables

**Windows (PowerShell):**
```powershell
$env:DPRS_DB_EMAIL="your_db_username"
$env:DPRS_DB_PASSWORD="your_db_password"
$env:DPRS_ADMIN_PASSWORD="admin123"
```

**Linux/Mac:**
```bash
export DPRS_DB_EMAIL="your_db_username"
export DPRS_DB_PASSWORD="your_db_password"
export DPRS_ADMIN_PASSWORD="admin123"
```

### Step 3: Build and Run

**Windows:**
```bash
.\gradlew.bat build
.\gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew build
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 🎯 System Flow

### Landing Page
- **URL**: `/`
- Shows role selection: Admin, Doctor, Nurse
- User selects their role to proceed to login

### Login Pages
- **Admin**: `/admin/login`
- **Doctor**: `/doctor/login`
- **Nurse**: `/nurse/login`

Each role has a dedicated login page. Users can only login from their role's portal.

### Dashboards
- **Admin**: `/admin/dashboard`
- **Doctor**: `/doctor/dashboard`
- **Nurse**: `/nurse/dashboard`

After successful login, users are redirected to their role-specific dashboard.

## 👤 Default Admin Account

On first startup, the system automatically creates an admin account:

- **Email**: `admin@hospital.com`
- **Username**: `admin`
- **Password**: Set via `DPRS_ADMIN_PASSWORD` environment variable (default: `admin123`)
- **Role**: ADMIN
- **Status**: Active

## 📁 Project Structure

```
Project-DigitalPatientRecordSystem/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── admin/          # Admin controllers
│   │   │   ├── auth/           # JWT authentication
│   │   │   ├── config/         # Configuration
│   │   │   ├── doctor/         # Doctor controllers
│   │   │   ├── nurse/          # Nurse controllers
│   │   │   ├── patient/        # Patient entities & repositories
│   │   │   └── user/           # User entity & repository
│   │   └── resources/
│   │       ├── templates/      # Thymeleaf HTML templates
│   │       │   ├── admin/     # Admin pages
│   │       │   ├── doctor/    # Doctor pages
│   │       │   └── nurse/     # Nurse pages
│   │       ├── static/css/    # CSS stylesheets
│   │       └── application.properties
│   └── test/
├── build.gradle
├── DATABASE_SCHEMA.sql        # Database schema script
└── README.md
```

## 🔑 Key Features

### Authentication & Security
- ✅ JWT token-based authentication
- ✅ Role-based route protection
- ✅ Secure password hashing (BCrypt)
- ✅ Account activation system
- ✅ Session management via JWT cookies

### User Management (Admin Only)
- ✅ Create Doctor/Nurse accounts
- ✅ Activate/Deactivate users
- ✅ View all users by role

### Patient Management
- ✅ Patient CRUD operations
- ✅ Patient search functionality
- ✅ Patient details view

### Medical Records (Doctors)
- ✅ Create medical records
- ✅ Update medical records
- ✅ Delete medical records
- ✅ View record history
- ✅ Diagnosis, prescriptions, lab results, visit notes

### Vitals Management (Nurses)
- ✅ Record patient vitals
- ✅ View vitals history
- ✅ Track: BP, temperature, weight, height, blood sugar, heart rate, respiratory rate

## 🗄️ Database Schema

### Core Tables

- **users** - System users (Admin, Doctor, Nurse)
  - Fields: id, firstName, lastName, email, username, password, role, active, createdAt

- **patient** - Patient information
- **patient_record** - Medical records
- **patient_followup** - Follow-up appointments
- **appointment** - Scheduled appointments
- **vitals** - Patient vital signs

See `DATABASE_SCHEMA.sql` for complete schema.

## 🐛 Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure database exists: `CREATE DATABASE DigitalPatientRecordSystem;`
- Run `DATABASE_SCHEMA.sql` to create tables

### JWT Token Issues
- Clear browser cookies
- Check JWT secret in `application.properties`
- Verify token expiration settings

### Login Issues
- Ensure user account exists and is active
- Verify role matches login portal (Admin must use `/admin/login`)
- Check password is correct

### Port Already in Use
Change port in `application.properties`:
```properties
server.port=8081
```

## 🔒 Security Notes

⚠️ **For Production Deployment:**

1. Change default admin password
2. Use HTTPS/SSL
3. Set strong JWT secret key
4. Implement CSRF protection
5. Add rate limiting
6. Use environment variables for sensitive data
7. Enable database connection pooling
8. Add audit logging
9. Implement session timeout
10. Use secure cookie flags (HttpOnly, Secure, SameSite)

## 📝 API Endpoints

### Public Routes
- `GET /` - Landing page (role selection)
- `GET /admin/login` - Admin login page
- `GET /doctor/login` - Doctor login page
- `GET /nurse/login` - Nurse login page

### Authentication
- `POST /admin/login` - Admin login
- `POST /doctor/login` - Doctor login
- `POST /nurse/login` - Nurse login
- `GET /logout` - Logout

### Admin Routes (Requires ADMIN role)
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - Manage users
- `GET /admin/register` - Create user page
- `POST /admin/register` - Create user
- `POST /admin/users/{id}/approve` - Activate user
- `POST /admin/users/{id}/block` - Deactivate user
- `GET /admin/patients` - View all patients
- `GET /admin/records` - View all records
- `GET /admin/appointments` - View all appointments

### Doctor Routes (Requires DOCTOR role)
- `GET /doctor/dashboard` - Doctor dashboard
- `GET /doctor/patients` - View patients
- `GET /doctor/patient/{id}` - Patient details
- `GET /doctor/patient-record/add` - Add record form
- `POST /doctor/patient-record/add` - Create record
- `GET /doctor/patient-record/{id}/edit` - Edit record form
- `POST /doctor/patient-record/{id}/edit` - Update record
- `POST /doctor/patient-record/{id}/delete` - Delete record
- `GET /doctor/appointments` - View appointments

### Nurse Routes (Requires NURSE role)
- `GET /nurse/dashboard` - Nurse dashboard
- `GET /nurse/patients` - View patients
- `GET /nurse/patient/{id}` - Patient details
- `GET /nurse/patient/add` - Add patient form
- `POST /nurse/patient/add` - Create patient
- `GET /nurse/patient/{id}/vitals/add` - Add vitals form
- `POST /nurse/patient/{id}/vitals/add` - Create vitals

## 🎓 Academic Project

This is an academic project built for educational purposes. Use responsibly and ensure compliance with healthcare data regulations (HIPAA, GDPR, etc.) if deploying in a real healthcare environment.

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review code comments
3. Refer to Spring Boot documentation
4. Check PostgreSQL logs for database issues

---

**Built with ❤️ for academic excellence**

**Version**: 2.0 (Re-architected with JWT Authentication)
