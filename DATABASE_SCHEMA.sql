-- Digital Patient Record System Database Schema
-- PostgreSQL Database Setup Script

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS vitals CASCADE;
DROP TABLE IF EXISTS appointment CASCADE;
DROP TABLE IF EXISTS patient_record CASCADE;
DROP TABLE IF EXISTS patient_followup CASCADE;
DROP TABLE IF EXISTS patient CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table (clean schema as per requirements)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'DOCTOR', 'NURSE')),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Patient table
CREATE TABLE patient (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    address TEXT,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE')),
    age INTEGER,
    contact VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Patient Followup table
CREATE TABLE patient_followup (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER REFERENCES patient(id) ON DELETE CASCADE,
    doctor_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Patient Record table
CREATE TABLE patient_record (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER REFERENCES patient(id) ON DELETE CASCADE,
    doctor_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    followup_id INTEGER REFERENCES patient_followup(id) ON DELETE SET NULL,
    diagnosis TEXT,
    prescriptions TEXT,
    lab_results TEXT,
    visit_notes TEXT,
    remarks TEXT,
    file VARCHAR(255),
    visit_type VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Appointment table
CREATE TABLE appointment (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER REFERENCES patient(id) ON DELETE CASCADE,
    doctor_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    appointment_date_time TIMESTAMP,
    status VARCHAR(20),
    notes TEXT,
    reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vitals table
CREATE TABLE vitals (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER REFERENCES patient(id) ON DELETE CASCADE,
    recorded_by_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    blood_pressure_systolic DOUBLE PRECISION,
    blood_pressure_diastolic DOUBLE PRECISION,
    temperature DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    height DOUBLE PRECISION,
    blood_sugar DOUBLE PRECISION,
    heart_rate INTEGER,
    respiratory_rate INTEGER,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_patient_name ON patient(first_name, last_name);
CREATE INDEX idx_patient_record_patient ON patient_record(patient_id);
CREATE INDEX idx_patient_record_doctor ON patient_record(doctor_id);
CREATE INDEX idx_appointment_doctor ON appointment(doctor_id);
CREATE INDEX idx_appointment_patient ON appointment(patient_id);
CREATE INDEX idx_vitals_patient ON vitals(patient_id);

-- Insert default admin user (password: admin123)
-- Password hash for 'admin123' using BCrypt
INSERT INTO users (first_name, last_name, email, username, password, role, active, created_at)
VALUES ('Admin', 'System', 'admin@hospital.com', 'admin', 
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'ADMIN', true, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

