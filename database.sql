-- Create Database (if not exists)
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

-- Table: students
DROP TABLE IF EXISTS students;
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    student_code VARCHAR(50) NOT NULL UNIQUE,
    major VARCHAR(100),
    password VARCHAR(255) NOT NULL
);

-- Table: courses
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    credits INT NOT NULL,
    status VARCHAR(20) NOT NULL -- Enum: OPEN, CLOSED
);

-- Sample Data: students
INSERT INTO students (full_name, student_code, major, password) VALUES 
('Nguyen Van A', 'SV001', 'IT', '123456'),
('Tran Thi B', 'SV002', 'Business', '123456'),
('Le Van C', 'SV003', 'Graphic Design', '123456');

-- Sample Data: courses
INSERT INTO courses (name, credits, status) VALUES 
('Lap Trinh Java', 3, 'OPEN'),
('Co So Du Lieu', 4, 'CLOSED'),
('Thiet Ke Web', 3, 'OPEN'),
('Kinh Te Vi Mo', 2, 'OPEN');
