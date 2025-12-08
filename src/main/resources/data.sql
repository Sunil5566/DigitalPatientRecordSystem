-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Insert default users
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role) VALUES ('doctor', 'doctor123', 'doctor')
ON CONFLICT (username) DO NOTHING;

INSERT INTO users (username, password, role) VALUES ('nurse', 'nurse123', 'nurse')
ON CONFLICT (username) DO NOTHING;
