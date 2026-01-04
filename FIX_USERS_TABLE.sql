-- FIX: Add missing columns to users table
-- Run this script in your PostgreSQL database

-- Add active column if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'active') THEN
        ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;
    END IF;
END $$;

-- Update existing rows to have active = true if null
UPDATE users SET active = true WHERE active IS NULL;

-- Add created_at column if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'created_at') THEN
        ALTER TABLE users ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- Update existing rows to have created_at if null
UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

-- Ensure first_name exists and is not null
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'first_name') THEN
        ALTER TABLE users ADD COLUMN first_name VARCHAR(100);
        UPDATE users SET first_name = 'User' WHERE first_name IS NULL;
        ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
    END IF;
END $$;

-- Ensure email exists and is not null
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'email') THEN
        ALTER TABLE users ADD COLUMN email VARCHAR(255);
        UPDATE users SET email = username || '@hospital.com' WHERE email IS NULL;
        ALTER TABLE users ALTER COLUMN email SET NOT NULL;
        ALTER TABLE users ADD CONSTRAINT users_email_unique UNIQUE (email);
    END IF;
END $$;

-- Ensure username exists
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'username') THEN
        ALTER TABLE users ADD COLUMN username VARCHAR(255);
        UPDATE users SET username = email WHERE username IS NULL;
        ALTER TABLE users ALTER COLUMN username SET NOT NULL;
        ALTER TABLE users ADD CONSTRAINT users_username_unique UNIQUE (username);
    END IF;
END $$;

-- Ensure role column exists
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'role') THEN
        ALTER TABLE users ADD COLUMN role VARCHAR(20);
        UPDATE users SET role = 'ADMIN' WHERE role IS NULL;
        ALTER TABLE users ALTER COLUMN role SET NOT NULL;
    END IF;
END $$;

-- Ensure password column exists
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'password') THEN
        ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';
    END IF;
END $$;

-- Add last_name column if it doesn't exist (nullable is fine)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'last_name') THEN
        ALTER TABLE users ADD COLUMN last_name VARCHAR(100);
    END IF;
END $$;

