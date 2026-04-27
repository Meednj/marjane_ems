-- EMS Database Migration Script
-- Refactoring from inheritance-based to role-based user model
-- This script consolidates administrators, employes, and technicians into a single users table

-- Step 1: Backup existing data (OPTIONAL - run before migration)
-- CREATE TABLE users_backup AS SELECT * FROM users;
-- CREATE TABLE administrators_backup AS SELECT * FROM administrators;
-- CREATE TABLE employes_backup AS SELECT * FROM employes;
-- CREATE TABLE technicians_backup AS SELECT * FROM technicians;

-- Step 2: Create new unified users table
CREATE TABLE users_new (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    eid VARCHAR(20) UNIQUE NOT NULL COMMENT 'Employee ID with role prefix: E001, T042, A005',
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL COMMENT 'ADMIN, TECHNICIAN, EMPLOYEE',
    status VARCHAR(50) DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE, SUSPENDED',
    department VARCHAR(100) COMMENT 'Only for EMPLOYEE role',
    activity_status VARCHAR(50) COMMENT 'Only for EMPLOYEE role',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_eid (eid),
    INDEX idx_role (role),
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 3: Migrate data from inherited tables to new unified table
-- Migrate from old users table (if there were direct users records)
INSERT INTO users_new (id, eid, username, email, password, first_name, last_name, phone, role, status, created_at, updated_at)
SELECT 
    u.id,
    u.eid,
    u.email AS username,  -- Using email as username if username doesn't exist
    u.email,
    u.password,
    u.first_name,
    u.last_name,
    u.phone,
    'EMPLOYEE' AS role,
    u.status,
    u.created_at,
    u.updated_at
FROM users u
WHERE NOT EXISTS (SELECT 1 FROM administrators WHERE id = u.id)
  AND NOT EXISTS (SELECT 1 FROM employes WHERE id = u.id)
  AND NOT EXISTS (SELECT 1 FROM technicians WHERE id = u.id)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Migrate Employees
INSERT INTO users_new (id, eid, username, email, password, first_name, last_name, phone, role, department, activity_status, status, created_at, updated_at)
SELECT 
    e.id,
    e.eid,
    u.email AS username,
    u.email,
    u.password,
    u.first_name,
    u.last_name,
    u.phone,
    'EMPLOYEE' AS role,
    e.department,
    e.activity_status,
    u.status,
    u.created_at,
    u.updated_at
FROM employes e
JOIN users u ON e.id = u.id
ON DUPLICATE KEY UPDATE eid = VALUES(eid);

-- Migrate Technicians
INSERT INTO users_new (id, eid, username, email, password, first_name, last_name, phone, role, status, created_at, updated_at)
SELECT 
    t.id,
    t.eid,
    u.email AS username,
    u.email,
    u.password,
    u.first_name,
    u.last_name,
    u.phone,
    'TECHNICIAN' AS role,
    u.status,
    u.created_at,
    u.updated_at
FROM technicians t
JOIN users u ON t.id = u.id
ON DUPLICATE KEY UPDATE eid = VALUES(eid);

-- Migrate Administrators
INSERT INTO users_new (id, eid, username, email, password, first_name, last_name, phone, role, status, created_at, updated_at)
SELECT 
    a.id,
    a.eid,
    u.email AS username,
    u.email,
    u.password,
    u.first_name,
    u.last_name,
    u.phone,
    'ADMIN' AS role,
    u.status,
    u.created_at,
    u.updated_at
FROM administrators a
JOIN users u ON a.id = u.id
ON DUPLICATE KEY UPDATE eid = VALUES(eid);

-- Step 4: Update foreign keys to point to new users table
-- Update tickets table
ALTER TABLE tickets 
  DROP FOREIGN KEY tickets_ibfk_1,  -- Adjust constraint name as needed
  DROP FOREIGN KEY tickets_ibfk_2;

ALTER TABLE tickets
  MODIFY COLUMN creator_id BIGINT NOT NULL,
  MODIFY COLUMN technician_id BIGINT,
  ADD CONSTRAINT tickets_fk_creator FOREIGN KEY (creator_id) REFERENCES users_new(id) ON DELETE RESTRICT,
  ADD CONSTRAINT tickets_fk_technician FOREIGN KEY (technician_id) REFERENCES users_new(id) ON DELETE SET NULL;

-- Update leaves table
ALTER TABLE leaves
  DROP FOREIGN KEY leaves_ibfk_1,
  DROP FOREIGN KEY leaves_ibfk_2;

ALTER TABLE leaves
  MODIFY COLUMN user_id BIGINT NOT NULL,
  MODIFY COLUMN approver_id BIGINT,
  ADD CONSTRAINT leaves_fk_user FOREIGN KEY (user_id) REFERENCES users_new(id) ON DELETE CASCADE,
  ADD CONSTRAINT leaves_fk_approver FOREIGN KEY (approver_id) REFERENCES users_new(id) ON DELETE SET NULL;

-- Update presences table
ALTER TABLE presences
  DROP FOREIGN KEY presences_ibfk_1;

ALTER TABLE presences
  MODIFY COLUMN user_id BIGINT NOT NULL,
  ADD CONSTRAINT presences_fk_user FOREIGN KEY (user_id) REFERENCES users_new(id) ON DELETE CASCADE;

-- Update availability table
ALTER TABLE availability
  DROP FOREIGN KEY availability_ibfk_1;

ALTER TABLE availability
  MODIFY COLUMN user_id BIGINT NOT NULL,
  ADD CONSTRAINT availability_fk_user FOREIGN KEY (user_id) REFERENCES users_new(id) ON DELETE CASCADE;

-- Update comments table (if it has author_id referencing User)
ALTER TABLE comments
  DROP FOREIGN KEY comments_ibfk_2;

ALTER TABLE comments
  MODIFY COLUMN author_id BIGINT NOT NULL,
  ADD CONSTRAINT comments_fk_author FOREIGN KEY (author_id) REFERENCES users_new(id) ON DELETE CASCADE;

-- Update ticket_history table (if it has executor_id referencing User)
ALTER TABLE ticket_history
  DROP FOREIGN KEY ticket_history_ibfk_2;

ALTER TABLE ticket_history
  MODIFY COLUMN executor_id BIGINT NOT NULL,
  ADD CONSTRAINT ticket_history_fk_executor FOREIGN KEY (executor_id) REFERENCES users_new(id) ON DELETE CASCADE;

-- Update user_shift table
ALTER TABLE user_shift
  DROP FOREIGN KEY user_shift_ibfk_1;

ALTER TABLE user_shift
  MODIFY COLUMN user_id BIGINT NOT NULL,
  ADD CONSTRAINT user_shift_fk_user FOREIGN KEY (user_id) REFERENCES users_new(id) ON DELETE CASCADE;

-- Step 5: Drop old tables (after verification)
-- ALTER TABLE leaves DROP FOREIGN KEY leaves_ibfk_2;
-- ALTER TABLE tickets DROP FOREIGN KEY tickets_ibfk_2;
-- DROP TABLE technicians;
-- DROP TABLE employes;
-- DROP TABLE administrators;
-- DROP TABLE users;

-- Step 6: Rename new table to users
-- RENAME TABLE users TO users_old;
-- RENAME TABLE users_new TO users;

-- Step 7: Create EID sequences table for future EID generation
CREATE TABLE IF NOT EXISTS eid_sequences (
    role VARCHAR(20) PRIMARY KEY,
    next_value BIGINT DEFAULT 1,
    UNIQUE KEY unique_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Initialize EID sequences with current counts
INSERT INTO eid_sequences (role, next_value)
SELECT role, COUNT(*) + 1 FROM users_new GROUP BY role
ON DUPLICATE KEY UPDATE next_value = GREATEST(next_value, VALUES(next_value));

-- Step 8: Drop legacy repositories
-- These are no longer needed since we use UserRepository with Role filtering
-- AdministratorRepository, EmployeRepository, TechnicianRepository
