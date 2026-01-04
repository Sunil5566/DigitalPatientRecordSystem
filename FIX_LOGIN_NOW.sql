-- URGENT FIX: Add missing 'active' column to fix login errors
-- Run this script in PostgreSQL immediately

-- Add active column if missing
ALTER TABLE users ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true;

-- Add created_at column if missing  
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Update any null values
UPDATE users SET active = true WHERE active IS NULL;
UPDATE users SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;

-- Verify columns exist
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns 
WHERE table_name = 'users' 
ORDER BY ordinal_position;

