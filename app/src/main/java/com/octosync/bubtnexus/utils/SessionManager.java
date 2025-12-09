package com.octosync.bubtnexus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLES = "user_roles";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_IS_STUDENT = "is_student";
    private static final String KEY_IS_FACULTY = "is_faculty";

    // User Details Keys
    private static final String KEY_IS_VERIFIED = "is_verified";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_INTAKE = "intake";
    private static final String KEY_SECTION = "section";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_CGPA = "cgpa";
    private static final String KEY_PROGRAM_ID = "program_id";
    private static final String KEY_PROGRAM_NAME = "program_name";
    private static final String KEY_PROGRAM_CODE = "program_code";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_FACULTY_CODE = "faculty_code";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PROFILE_PICTURE = "profile_picture";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save methods
    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public void saveUserData(String name, String email, String userType, boolean isStudent, boolean isFaculty) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putBoolean(KEY_IS_STUDENT, isStudent);
        editor.putBoolean(KEY_IS_FACULTY, isFaculty);
        editor.apply();
    }

    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public void saveUserDetails(com.octosync.bubtnexus.models.LoginResponse.Details details) {
        if (details != null) {
            editor.putString(KEY_IS_VERIFIED, details.getIsVerified());
            editor.putString(KEY_SEMESTER, details.getSemester());
            editor.putInt(KEY_INTAKE, details.getIntake());
            editor.putInt(KEY_SECTION, details.getSection());
            editor.putString(KEY_STUDENT_ID, details.getStudentId());
            editor.putString(KEY_CGPA, details.getCgpa());
            editor.putString(KEY_DEPARTMENT, details.getDepartment());
            editor.putString(KEY_FACULTY_CODE, details.getFacultyCode());
            editor.putString(KEY_DESIGNATION, details.getDesignation());
            editor.putString(KEY_PHONE, details.getPhone());
            editor.putString(KEY_PROFILE_PICTURE, details.getProfilePicture());

            // Save program information if available
            if (details.getProgram() != null) {
                editor.putInt(KEY_PROGRAM_ID, details.getProgram().getId());
                editor.putString(KEY_PROGRAM_NAME, details.getProgram().getName());
                editor.putString(KEY_PROGRAM_CODE, details.getProgram().getCode());
            }

            editor.apply();
        }
    }

    // Add this method to SessionManager.java
    public void saveUserDetails(String phone, String studentId, String facultyCode,
                                String department, String designation, String semester,
                                int intake, int section, String cgpa, String profilePicture) {
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_FACULTY_CODE, facultyCode);
        editor.putString(KEY_DEPARTMENT, department);
        editor.putString(KEY_DESIGNATION, designation);
        editor.putString(KEY_SEMESTER, semester);
        editor.putInt(KEY_INTAKE, intake);
        editor.putInt(KEY_SECTION, section);
        editor.putString(KEY_CGPA, cgpa);
        editor.putString(KEY_PROFILE_PICTURE, profilePicture);
        editor.apply();
    }

    // Add this method to SessionManager.java
    public void saveProgramInfo(int programId, String programName, String programCode) {
        editor.putInt(KEY_PROGRAM_ID, programId);
        editor.putString(KEY_PROGRAM_NAME, programName);
        editor.putString(KEY_PROGRAM_CODE, programCode);
        editor.apply();
    }

    // Getters
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, "");
    }

    public boolean isStudent() {
        return sharedPreferences.getBoolean(KEY_IS_STUDENT, false);
    }

    public boolean isFaculty() {
        return sharedPreferences.getBoolean(KEY_IS_FACULTY, false);
    }

    // User Details Getters
    public String getIsVerified() {
        return sharedPreferences.getString(KEY_IS_VERIFIED, null);
    }

    public String getSemester() {
        return sharedPreferences.getString(KEY_SEMESTER, null);
    }

    public int getIntake() {
        return sharedPreferences.getInt(KEY_INTAKE, 0);
    }

    public int getSection() {
        return sharedPreferences.getInt(KEY_SECTION, 0);
    }

    public String getStudentId() {
        return sharedPreferences.getString(KEY_STUDENT_ID, null);
    }

    public String getCgpa() {
        return sharedPreferences.getString(KEY_CGPA, null);
    }

    public int getProgramId() {
        return sharedPreferences.getInt(KEY_PROGRAM_ID, 0);
    }

    public String getProgramName() {
        return sharedPreferences.getString(KEY_PROGRAM_NAME, null);
    }

    public String getProgramCode() {
        return sharedPreferences.getString(KEY_PROGRAM_CODE, null);
    }

    // For backward compatibility - returns program code
    public String getProgram() {
        return getProgramCode();
    }

    public String getDepartment() {
        return sharedPreferences.getString(KEY_DEPARTMENT, null);
    }

    public String getFacultyCode() {
        return sharedPreferences.getString(KEY_FACULTY_CODE, null);
    }

    public String getDesignation() {
        return sharedPreferences.getString(KEY_DESIGNATION, null);
    }

    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public String getProfilePicture() {
        return sharedPreferences.getString(KEY_PROFILE_PICTURE, null);
    }

    // Roles management
    public void saveUserRoles(List<String> roles) {
        Set<String> rolesSet = new HashSet<>(roles);
        editor.putStringSet(KEY_USER_ROLES, rolesSet);
        editor.apply();
    }

    public Set<String> getUserRoles() {
        return sharedPreferences.getStringSet(KEY_USER_ROLES, new HashSet<>());
    }

    public boolean hasRole(String role) {
        Set<String> roles = getUserRoles();
        return roles.contains(role);
    }

    // Clear session
    public void clear() {
        editor.clear().apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}