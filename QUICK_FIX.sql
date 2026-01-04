-- QUICK FIX: Drop and recreate users table with new schema
-- This will DELETE all existing user data except the default admin
-- Run this script in your PostgreSQL database

-- Drop existing users table
DROP TABLE IF EXISTS users CASCADE;

-- Recreate with new clean schema
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

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role);

-- Insert default admin user (password: admin123)
INSERT INTO users (first_name, last_name, email, username, password, role, active, created_at)
VALUES ('Admin', 'System', 'admin@hospital.com', 'admin', 
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'ADMIN', true, CURRENT_TIMESTAMP);

