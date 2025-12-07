package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.octosync.bubtnexus.models.NoticesResponse;
import com.octosync.bubtnexus.models.SemesterOption;
import com.octosync.bubtnexus.models.SemesterOptionsResponse;
import com.octosync.bubtnexus.models.UserResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView tvWelcome, tvDate, tvUserInfo, tvSectionTitle;
    private TextInputEditText etSearch;
    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;
    private GridLayout gridButtons; // This is GridLayout in XML
    private CardView contentCard;

    // Individual button views
    private LinearLayout btnRoutine, btnAssignments, btnNotices, btnCommunity, btnEvents, btnRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check if user is logged in, if not redirect to login
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        setCurrentDate();

        // Show content based on user type
        showContentBasedOnUserType();

        // Verify token with server and update user data if needed
        verifyTokenAndUpdateUser();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvDate = findViewById(R.id.tvDate);
        etSearch = findViewById(R.id.etSearch);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        gridButtons = findViewById(R.id.gridButtons); // This is GridLayout
        contentCard = findViewById(R.id.contentCard);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);

        // Initialize individual buttons
        btnRoutine = findViewById(R.id.btnRoutine);
        btnAssignments = findViewById(R.id.btnAssignments);
        btnNotices = findViewById(R.id.btnNotices);
        btnCommunity = findViewById(R.id.btnCommunity);
        btnEvents = findViewById(R.id.btnEvents);
        btnRide = findViewById(R.id.btnRide);

        // Set welcome message with stored user name
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Welcome, " + userName + "!");
        } else {
            tvWelcome.setText("Welcome!");
        }
    }

    private void showContentBasedOnUserType() {
        String userType = sessionManager.getUserType();

        if (userType != null) {
            if (userType.equals("Student")) {
                showStudentContent();
            } else if (userType.equals("Faculty")) {
                showFacultyContent();
            } else {
                showDefaultContent();
            }
        } else {
            // If user type is not set, check isStudent/isFaculty flags
            if (sessionManager.isStudent()) {
                showStudentContent();
            } else if (sessionManager.isFaculty()) {
                showFacultyContent();
            } else {
                showDefaultContent();
            }
        }
    }

    private void showStudentContent() {
        // Show all grid buttons for students
        gridButtons.setVisibility(View.VISIBLE);

        // Show all student-specific buttons
        btnRoutine.setVisibility(View.VISIBLE);
        btnAssignments.setVisibility(View.VISIBLE);
        btnNotices.setVisibility(View.VISIBLE);
        btnCommunity.setVisibility(View.VISIBLE);
        btnEvents.setVisibility(View.VISIBLE);
        btnRide.setVisibility(View.VISIBLE);

        if (tvSectionTitle != null) {
            tvSectionTitle.setVisibility(View.VISIBLE);
        }

        // Get student data from session manager
        String studentId = sessionManager.getStudentId();
        String programName = sessionManager.getProgramName();
        int intake = sessionManager.getIntake();
        int section = sessionManager.getSection();
        String cgpa = sessionManager.getCgpa();

        // Build student info string
        StringBuilder studentInfo = new StringBuilder();

        // Student ID
        studentInfo.append("Student ID: ").append(studentId != null ? studentId : "N/A");

        // Program
        studentInfo.append("\nProgram: ").append(programName);

        // Intake and Section
        if (intake > 0) {
            studentInfo.append("\nIntake: ").append(intake);
            if (section > 0) {
                studentInfo.append(" | Section: ").append(section);
            }
        }

        // CGPA
        if (cgpa != null && !cgpa.isEmpty()) {
            try {
                // Format CGPA to 2 decimal places if it's a number
                double cgpaValue = Double.parseDouble(cgpa);
                studentInfo.append("\nCGPA: ").append(String.format("%.2f", cgpaValue));
            } catch (NumberFormatException e) {
                // If it's not a valid number, display as is
                studentInfo.append("\nCGPA: ").append(cgpa);
            }
        }

        // Update UI
        if (tvUserInfo != null) {
            tvUserInfo.setText(studentInfo.toString());
            tvUserInfo.setVisibility(View.VISIBLE);
        }
    }

    private void showFacultyContent() {
        // Show the grid for faculty
        gridButtons.setVisibility(View.VISIBLE);

        // Hide student-specific buttons
        btnRoutine.setVisibility(View.GONE);
        btnAssignments.setVisibility(View.GONE);

        // Show common buttons for faculty (including Ride button)
        btnNotices.setVisibility(View.VISIBLE);
        btnCommunity.setVisibility(View.VISIBLE);
        btnEvents.setVisibility(View.VISIBLE);
        btnRide.setVisibility(View.VISIBLE);

        if (tvSectionTitle != null) {
            tvSectionTitle.setVisibility(View.VISIBLE);
            tvSectionTitle.setText("Quick Access");
        }

        // Update welcome message for faculty
        String designation = sessionManager.getDesignation();
        String department = sessionManager.getDepartment();
        String facultyCode = sessionManager.getFacultyCode();
        String phone = sessionManager.getPhone();

        String facultyInfo = "Designation: " + (designation != null ? designation : "N/A") +
                "\nDepartment: " + (department != null ? department : "N/A") +
                (facultyCode != null ? "\nFaculty Code: " + facultyCode : "") +
                (phone != null ? "\nPhone: " + phone : "");

        if (tvUserInfo != null) {
            tvUserInfo.setText(facultyInfo);
            tvUserInfo.setVisibility(View.VISIBLE);
        }

        // Debug log to check which buttons are visible
        Log.d(TAG, "Faculty Content - Ride button visibility: " + btnRide.getVisibility());
    }

    private void showDefaultContent() {
        // Show default content for other user types
        gridButtons.setVisibility(View.VISIBLE);

        // Show all buttons by default
        btnRoutine.setVisibility(View.VISIBLE);
        btnAssignments.setVisibility(View.VISIBLE);
        btnNotices.setVisibility(View.VISIBLE);
        btnCommunity.setVisibility(View.VISIBLE);
        btnEvents.setVisibility(View.VISIBLE);
        btnRide.setVisibility(View.VISIBLE);

        if (tvSectionTitle != null) {
            tvSectionTitle.setVisibility(View.VISIBLE);
        }
        if (tvUserInfo != null) {
            tvUserInfo.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Student-specific buttons - only visible for students
        btnRoutine.setOnClickListener(v -> {
            if (sessionManager.isStudent() || "Student".equals(sessionManager.getUserType())) {
                Intent intent = new Intent(MainActivity.this, RoutineActivity.class);
                startActivity(intent);
            } else {
                showToast("This feature is for students only");
            }
        });

        btnAssignments.setOnClickListener(v -> {
            if (sessionManager.isStudent() || "Student".equals(sessionManager.getUserType())) {
                Intent intent = new Intent(MainActivity.this, AssignmentActivity.class);
                startActivity(intent);
            } else {
                showToast("This feature is for students only");
            }
        });

        btnNotices.setOnClickListener(v -> {
            // Notices should be visible to both students and faculty
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        });

        // Common buttons for all users
        btnCommunity.setOnClickListener(v -> openSection("Community"));
        btnEvents.setOnClickListener(v -> openSection("Events"));
        btnRide.setOnClickListener(v -> {
            navigateToRideSharingActivity();
        });

        etSearch.setOnClickListener(v -> showToast("Search opened"));
    }

    private void setupBottomNavigation() {
        // Set home as selected by default
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on home page
                return true;
            } else if (id == R.id.nav_task) {
                navigateToTaskActivity();
                return true;
            } else if (id == R.id.nav_profile) {
                navigateToProfileActivity();
                return true;
            } else if (id == R.id.nav_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
    }

    private void navigateToTaskActivity() {
        Intent intent = new Intent(MainActivity.this, TaskActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void navigateToRideSharingActivity() {
        Intent intent = new Intent(MainActivity.this, RideSharingActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void logoutUser() {
        // Call logout API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.logout(sessionManager.getToken());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Clear session regardless of API response
                sessionManager.clear();
                showToast("Logged out successfully");
                redirectToLogin();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Still clear local session even if API call fails
                sessionManager.clear();
                showToast("Logged out");
                redirectToLogin();
            }
        });
    }

    private void verifyTokenAndUpdateUser() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Get token and ensure it has Bearer prefix
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            showToast("Please login again");
            sessionManager.clear();
            redirectToLogin();
            return;
        }

        // Ensure token has Bearer prefix
        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        Call<UserResponse> call = apiService.getUser(authToken);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();

                    // Check if the API call was successful
                    if (userResponse.isSuccess() && userResponse.getData() != null && userResponse.getData().getUser() != null) {
                        com.octosync.bubtnexus.models.User user = userResponse.getData().getUser();
                        String currentUserName = sessionManager.getUserName();
                        String newUserName = user.getName();
                        String userType = user.getUserType();

                        // Update if user data changed or not stored
                        if (currentUserName == null || !currentUserName.equals(newUserName)) {
                            // Save basic user data
                            sessionManager.saveUserData(
                                    user.getName(),
                                    user.getEmail(),
                                    user.getUserType(),
                                    user.isStudent(),
                                    user.isFaculty()
                            );

                            // Save user ID
                            sessionManager.saveUserId(user.getId());

                            // Update user details if available
                            if (user.getDetails() != null) {
                                sessionManager.saveUserDetails(user.getDetails());
                            }

                            // Update roles if available
                            if (user.getRoles() != null) {
                                sessionManager.saveUserRoles(user.getRoles());
                            }

                            // Update welcome message
                            if (tvWelcome != null) {
                                tvWelcome.setText("Welcome, " + newUserName + "!");
                            }

                            // Refresh content based on updated user type
                            showContentBasedOnUserType();

                            showToast("Profile updated");
                        }
                    } else {
                        // API returned success: false or no user data
                        String errorMessage = userResponse.getMessage() != null ?
                                userResponse.getMessage() : "Failed to load user data";
                        showToast(errorMessage);

                        // If token is invalid, clear session
                        if (errorMessage.contains("token") || errorMessage.contains("expired") ||
                                errorMessage.contains("unauthorized")) {
                            sessionManager.clear();
                            redirectToLogin();
                        }
                    }
                } else {
                    // HTTP error handling
                    String errorMessage = "Failed to verify user";
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please login again.";
                        sessionManager.clear();
                        redirectToLogin();
                    } else if (response.code() == 403) {
                        errorMessage = "Access denied. Please login again.";
                        sessionManager.clear();
                        redirectToLogin();
                    } else if (response.code() == 404) {
                        errorMessage = "User not found. Please login again.";
                        sessionManager.clear();
                        redirectToLogin();
                    } else if (response.code() == 500) {
                        errorMessage = "Server error. Please try again later.";
                    } else {
                        errorMessage = "Failed to load user data. Error code: " + response.code();
                    }
                    showToast(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showToast("Network error, using cached data" + t.getMessage());
            }
        });
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvDate.setText(currentDate);
    }

    private void openSection(String sectionName) {
        showToast(sectionName + " section opened");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}