package com.example.student.model;

public class Course {
    private Long id;
    private String name;
    private int credits;
    private CourseStatus status;

    public Course() {
    }

    public Course(Long id, String name, int credits, CourseStatus status) {
        this.id = id;
        this.name = name;
        this.credits = credits;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }
}
