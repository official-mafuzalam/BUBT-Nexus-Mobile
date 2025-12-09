package com.octosync.bubtnexus.models;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String password_confirmation;
    private String user_type;
    private String phone;

    // Student fields
    private String student_id;
    private Long program_id;
    private String semester;
    private Integer intake;
    private Integer section;
    private Double cgpa;

    // Faculty fields
    private String faculty_code;
    private String department;
    private String designation;

    // Constructor for Student
    public RegisterRequest(String name, String email, String password, String password_confirmation,
                           String phone, String student_id, Long program_id, String semester,
                           Integer intake, Integer section, Double cgpa) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.user_type = "student";
        this.phone = phone;
        this.student_id = student_id;
        this.program_id = program_id;
        this.semester = semester;
        this.intake = intake;
        this.section = section;
        this.cgpa = cgpa;
    }

    // Constructor for Faculty
    public RegisterRequest(String name, String email, String password, String password_confirmation,
                           String phone, String faculty_code, String department, String designation) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.password_confirmation = password_confirmation;
        this.user_type = "faculty";
        this.phone = phone;
        this.faculty_code = faculty_code;
        this.department = department;
        this.designation = designation;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public Long getProgram_id() {
        return program_id;
    }

    public void setProgram_id(Long program_id) {
        this.program_id = program_id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getIntake() {
        return intake;
    }

    public void setIntake(Integer intake) {
        this.intake = intake;
    }

    public Integer getSection() {
        return section;
    }

    public void setSection(Integer section) {
        this.section = section;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public String getFaculty_code() {
        return faculty_code;
    }

    public void setFaculty_code(String faculty_code) {
        this.faculty_code = faculty_code;
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
}