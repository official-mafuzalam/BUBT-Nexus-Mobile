package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.octosync.bubtnexus.models.RoutineResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutineActivity extends AppCompatActivity {

    private static final String TAG = "RoutineActivity";

    // UI Components
    private ProgressBar progressBar;
    private TextView tvError, tvProgram, tvSemester;
    private LinearLayout dynamicDaysContainer;
    private ImageButton btnBack;
    private BottomNavigationView bottomNavigation;

    // Session and API
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check if user is logged in, if not redirect to login
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_routine);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        initApiService();
        loadRoutineData();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RoutineActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvProgram = findViewById(R.id.tvProgram);
        tvSemester = findViewById(R.id.tvSemester);
        dynamicDaysContainer = findViewById(R.id.dynamicDaysContainer);
        btnBack = findViewById(R.id.btnBack);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupBottomNavigation() {
        // Set routine as selected by default
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navigateToMainActivity();
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

    private void navigateToMainActivity() {
        Intent intent = new Intent(RoutineActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToTaskActivity() {
        Intent intent = new Intent(RoutineActivity.this, TaskActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(RoutineActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
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

    private void initApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void loadRoutineData() {
        showLoading(true);

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            showError("Please login first");
            return;
        }

        // Get program info from session
        String program = sessionManager.getProgram() != null ? sessionManager.getProgram() : "019";
        String semester = sessionManager.getSemester() != null ? sessionManager.getSemester() : "611";
        String intake = sessionManager.getIntake() != null ? sessionManager.getIntake() : "49 - 1";

        Log.d(TAG, "Loading routine - Program: " + program + ", Semester: " + semester + ", Intake: " + intake);

        Call<RoutineResponse> call = apiService.getRoutine("Bearer " + token, program, semester, intake);
        call.enqueue(new Callback<RoutineResponse>() {
            @Override
            public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    RoutineResponse routineResponse = response.body();
                    if (routineResponse.isStatus()) {
                        displayRoutineData(routineResponse);
                    } else {
                        showError("No routine data available for your program");
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<RoutineResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Routine API call failed: " + t.getMessage());
                showError("Network error. Please check your connection.");
            }
        });
    }

    private void handleApiError(Response<RoutineResponse> response) {
        String errorMessage = "Failed to load routine";
        if (response.code() == 401) {
            errorMessage = "Session expired. Please login again.";
            sessionManager.clear();
            redirectToLogin();
            return;
        } else if (response.code() == 404) {
            errorMessage = "Routine not found for your program/semester";
        } else if (response.errorBody() != null) {
            try {
                errorMessage = "Error: " + response.errorBody().string();
            } catch (Exception e) {
                errorMessage = "Error: " + response.message();
            }
        }
        showError(errorMessage);
    }

    private void displayRoutineData(RoutineResponse routineResponse) {
        // Set program and semester info
        if (routineResponse.getProgram() != null) {
            tvProgram.setText(routineResponse.getProgram());
        }
        if (routineResponse.getSemester() != null) {
            tvSemester.setText(routineResponse.getSemester());
        }

        // Clear all existing day views
        dynamicDaysContainer.removeAllViews();

        // Populate routine for each day dynamically
        if (routineResponse.getRoutine() != null && !routineResponse.getRoutine().isEmpty()) {
            for (com.octosync.bubtnexus.models.RoutineDay day : routineResponse.getRoutine()) {
                if (day.getDay() != null && day.getClasses() != null) {
                    createDayCard(day);
                }
            }
        } else {
            showError("No classes scheduled for this semester");
            return;
        }

        dynamicDaysContainer.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    private void createDayCard(com.octosync.bubtnexus.models.RoutineDay day) {
        // Inflate day card layout
        View dayCardView = LayoutInflater.from(this).inflate(R.layout.day_card_layout, dynamicDaysContainer, false);

        // Get references to the views inside the card
        TextView tvDayName = dayCardView.findViewById(R.id.tvDayName);
        TextView tvClassCount = dayCardView.findViewById(R.id.tvClassCount);
        LinearLayout classesContainer = dayCardView.findViewById(R.id.classesContainer);

        // Set day name
        String fullDayName = getFullDayName(day.getDay());
        tvDayName.setText(fullDayName);

        // Set class count
        int classCount = day.getClasses().size();
        tvClassCount.setText(classCount + " Class" + (classCount > 1 ? "es" : ""));

        // Add class items
        if (day.getClasses() != null && !day.getClasses().isEmpty()) {
            for (com.octosync.bubtnexus.models.ClassItem classItem : day.getClasses()) {
                View classItemView = getLayoutInflater().inflate(R.layout.class_item, classesContainer, false);

                TextView tvTime = classItemView.findViewById(R.id.tvTime);
                TextView tvEndTime = classItemView.findViewById(R.id.tvEndTime);
                TextView tvCourseCode = classItemView.findViewById(R.id.tvCourseCode);
                TextView tvFaculty = classItemView.findViewById(R.id.tvFaculty);
                TextView tvRoom = classItemView.findViewById(R.id.tvRoom);

                // Parse time (format: "10:30 AM to 11:45 AM")
                if (classItem.getTime() != null) {
                    String[] timeParts = classItem.getTime().split(" to ");
                    if (timeParts.length == 2) {
                        tvTime.setText(timeParts[0].trim());
                        tvEndTime.setText(timeParts[1].trim());
                    } else {
                        tvTime.setText(classItem.getTime());
                        tvEndTime.setText("");
                    }
                }

                if (classItem.getCourseCode() != null) {
                    tvCourseCode.setText(classItem.getCourseCode());
                }
                if (classItem.getFacultyCode() != null) {
                    tvFaculty.setText(classItem.getFacultyCode());
                } else {
                    tvFaculty.setText("N/A");
                }
                if (classItem.getRoom() != null && !classItem.getRoom().isEmpty()) {
                    tvRoom.setText("Room: " + classItem.getRoom());
                } else {
                    tvRoom.setText("Room: N/A");
                }

                classesContainer.addView(classItemView);
            }
        } else {
            addNoClassesMessage(classesContainer);
        }

        dynamicDaysContainer.addView(dayCardView);
    }

    private String getFullDayName(String dayAbbreviation) {
        switch (dayAbbreviation.toUpperCase()) {
            case "SUN":
                return "SUNDAY";
            case "MON":
                return "MONDAY";
            case "TUE":
                return "TUESDAY";
            case "WED":
                return "WEDNESDAY";
            case "THU":
                return "THURSDAY";
            case "THR":
                return "THURSDAY"; // Handle both abbreviations
            case "FRI":
                return "FRIDAY";
            case "SAT":
                return "SATURDAY";
            default:
                return dayAbbreviation;
        }
    }

    private void addNoClassesMessage(LinearLayout dayContainer) {
        TextView noClassText = new TextView(this);
        noClassText.setText("No classes scheduled");
        noClassText.setTextColor(getResources().getColor(R.color.text_secondary));
        noClassText.setTextSize(14);
        noClassText.setPadding(16, 16, 16, 16);
        noClassText.setGravity(Gravity.CENTER);
        dayContainer.addView(noClassText);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            dynamicDaysContainer.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        dynamicDaysContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}