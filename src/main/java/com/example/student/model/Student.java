package com.example.student.model;

public class Student {
    private Long id;
    private String fullName;
    private String studentCode;
    private String major;
    private String password;
    private Long courseId;

    public Student() {
    }

    public Student(Long id, String fullName, String studentCode, String major, String password, Long courseId) {
        this.id = id;
        this.fullName = fullName;
        this.studentCode = studentCode;
        this.major = major;
        this.password = password;
        this.courseId = courseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
