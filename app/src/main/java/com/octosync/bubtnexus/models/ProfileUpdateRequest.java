package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class ProfileUpdateRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("student_id")
    private String studentId;

    @SerializedName("faculty_code")
    private String facultyCode;

    @SerializedName("program")
    private String program;

    @SerializedName("semester")
    private String semester;

    @SerializedName("intake")
    private String intake;

    @SerializedName("section")
    private String section;

    @SerializedName("cgpa")
    private String cgpa;

    @SerializedName("department")
    private String department;

    @SerializedName("designation")
    private String designation;

    @SerializedName("phone")
    private String phone;

    // Constructors
    public ProfileUpdateRequest() {
    }

    public ProfileUpdateRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSection() {
        return section;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFacultyCode() {
        return facultyCode;
    }

    public void setFacultyCode(String facultyCode) {
        this.facultyCode = facultyCode;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public String getCgpa() {
        return cgpa;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSection(String section) {
        this.section = section;
    }
}