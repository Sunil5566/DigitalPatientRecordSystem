-- Migration Fix Script for Existing Database
-- Run this script if you have existing data in the users table

-- Step 1: Update existing rows to have default values for new required columns
UPDATE users 
SET 
    active = COALESCE(active, true),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    email = COALESCE(email, username || '@hospital.com'),
    first_name = COALESCE(first_name, 'User')
WHERE active IS NULL 
   OR created_at IS NULL 
   OR email IS NULL 
   OR first_name IS NULL;

-- Step 2: If you want to drop the old table and recreate (WARNING: This deletes all data)
-- Uncomment the lines below ONLY if you want to start fresh:

-- DROP TABLE IF EXISTS users CASCADE;
-- Then run the DATABASE_SCHEMA.sql script

-- Step 3: Alternative - Drop and recreate just the users table (keeps other tables)
-- Uncomment if you want to reset only users table:

/*
DROP TABLE IF EXISTS users CASCADE;

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

-- Re-insert default admin
INSERT INTO users (first_name, last_name, email, username, password, role, active, created_at)
VALUES ('Admin', 'System', 'admin@hospital.com', 'admin', 
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        'ADMIN', true, CURRENT_TIMESTAMP);
*/

