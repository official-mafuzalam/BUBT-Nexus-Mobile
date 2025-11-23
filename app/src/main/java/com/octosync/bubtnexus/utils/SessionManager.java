package com.octosync.bubtnexus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.octosync.bubtnexus.models.LoginResponse;

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
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_INTAKE = "intake";
    private static final String KEY_PROGRAM = "program";
    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_CGPA = "cgpa";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_FACULTY_ID = "faculty_id";
    private static final String KEY_DESIGNATION = "designation";
    private static final String KEY_OFFICE_ROOM = "office_room";
    private static final String KEY_OFFICE_HOURS = "office_hours";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_EMERGENCY_CONTACT = "emergency_contact";
    private static final String KEY_PROFILE_PICTURE_URI = "profile_picture_uri";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void saveUserData(String name, String email) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
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

    public void saveUserDetails(LoginResponse.User.UserDetails details) {
        if (details != null) {
            editor.putString(KEY_SEMESTER, details.getSemester());
            editor.putString(KEY_INTAKE, details.getIntake());
            editor.putString(KEY_PROGRAM, details.getProgram());
            editor.putString(KEY_STUDENT_ID, details.getStudentId());
            editor.putString(KEY_DEPARTMENT, details.getDepartment());
            editor.putString(KEY_FACULTY_ID, details.getFacultyId());
            editor.putString(KEY_DESIGNATION, details.getDesignation());
            editor.putString(KEY_OFFICE_ROOM, details.getOfficeRoom());
            editor.putString(KEY_OFFICE_HOURS, details.getOfficeHours());
            editor.putString(KEY_PHONE, details.getPhone());
            editor.putString(KEY_ADDRESS, details.getAddress());
            editor.putString(KEY_DATE_OF_BIRTH, details.getDateOfBirth());
            editor.putString(KEY_EMERGENCY_CONTACT, details.getEmergencyContact());

            if (details.getCgpa() != null) {
                editor.putFloat(KEY_CGPA, details.getCgpa().floatValue());
            } else {
                editor.remove(KEY_CGPA);
            }
            editor.apply();
        }
    }

    // Profile Picture Methods (Local storage only)
    public void saveProfilePictureUri(String uriString) {
        editor.putString(KEY_PROFILE_PICTURE_URI, uriString);
        editor.apply();
    }

    public String getProfilePictureUri() {
        return sharedPreferences.getString(KEY_PROFILE_PICTURE_URI, null);
    }

    public void removeProfilePicture() {
        editor.remove(KEY_PROFILE_PICTURE_URI);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void saveUserId(int userId) {
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public void saveUserRoles(List<String> roles) {
        Set<String> rolesSet = new HashSet<>(roles);
        editor.putStringSet(KEY_USER_ROLES, rolesSet);
        editor.apply();
    }

    public Set<String> getUserRoles() {
        return sharedPreferences.getStringSet(KEY_USER_ROLES, new HashSet<>());
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
    public String getSemester() {
        return sharedPreferences.getString(KEY_SEMESTER, null);
    }

    public String getIntake() {
        return sharedPreferences.getString(KEY_INTAKE, null);
    }

    public String getProgram() {
        return sharedPreferences.getString(KEY_PROGRAM, null);
    }

    public String getStudentId() {
        return sharedPreferences.getString(KEY_STUDENT_ID, null);
    }

    public String getDepartment() {
        return sharedPreferences.getString(KEY_DEPARTMENT, null);
    }

    public String getFacultyId() {
        return sharedPreferences.getString(KEY_FACULTY_ID, null);
    }

    public String getDesignation() {
        return sharedPreferences.getString(KEY_DESIGNATION, null);
    }

    public String getOfficeRoom() {
        return sharedPreferences.getString(KEY_OFFICE_ROOM, null);
    }

    public String getOfficeHours() {
        return sharedPreferences.getString(KEY_OFFICE_HOURS, null);
    }

    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    public String getAddress() {
        return sharedPreferences.getString(KEY_ADDRESS, null);
    }

    public String getDateOfBirth() {
        return sharedPreferences.getString(KEY_DATE_OF_BIRTH, null);
    }

    public String getEmergencyContact() {
        return sharedPreferences.getString(KEY_EMERGENCY_CONTACT, null);
    }

    public Float getCgpa() {
        if (sharedPreferences.contains(KEY_CGPA)) {
            return sharedPreferences.getFloat(KEY_CGPA, 0.0f);
        }
        return null;
    }

    public void clear() {
        editor.clear().apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // Additional utility methods
    public boolean hasRole(String role) {
        Set<String> roles = getUserRoles();
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole("admin") || hasRole("super_admin");
    }

    public void updateUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public void updateUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void updateUserPhone(String phone) {
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    public void updateStudentId(String studentId) {
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.apply();
    }

    public void updateUserDepartment(String department) {
        editor.putString(KEY_DEPARTMENT, department);
        editor.apply();
    }

    public void updateUserSemester(String semester) {
        editor.putString(KEY_SEMESTER, semester);
        editor.apply();
    }

    public void updateIntake(String intake) {
        editor.putString(KEY_INTAKE, intake);
        editor.apply();
    }

    public void updateUserAddress(String address) {
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

}