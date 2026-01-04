-- IMMEDIATE FIX: Add missing 'active' column to users table
-- Run this in PostgreSQL to fix the login error

-- Check and add active column
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'users' 
        AND column_name = 'active'
    ) THEN
        ALTER TABLE users ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;
        RAISE NOTICE 'Added active column';
    ELSE
        RAISE NOTICE 'active column already exists';
    END IF;
END $$;

-- Check and add created_at column
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'users' 
        AND column_name = 'created_at'
    ) THEN
        ALTER TABLE users ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
        RAISE NOTICE 'Added created_at column';
    ELSE
        RAISE NOTICE 'created_at column already exists';
    END IF;
END $$;

-- Ensure all required columns exist with proper defaults
UPDATE users SET active = true WHERE active IS NULL;
UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

-- Verify the table structure
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'users' 
ORDER BY ordinal_position;

