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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.octosync.bubtnexus.models.RoutineResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        gson = new Gson();

        // Check if user is logged in, if not redirect to login
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_routine);

        initializeViews();
        setupClickListeners();
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
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
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

        // Get student info from session
        String programCode = sessionManager.getProgramCode(); // Use program code
        String intake = String.valueOf(sessionManager.getIntake());
        String section = String.valueOf(sessionManager.getSection());

        // Use hardcoded values for testing if not available
        if (programCode == null || programCode.isEmpty()) {
            programCode = "019"; // Default program code
        }
        if (intake.equals("0")) {
            intake = "46"; // From API response
        }
        if (section.equals("0")) {
            section = "1"; // From API response
        }

        Log.d(TAG, "Loading routine - Program Code: " + programCode + ", Intake: " + intake + ", Section: " + section);

        String authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;

        Call<RoutineResponse> call = apiService.getRoutine(authToken, programCode, intake, section);
        call.enqueue(new Callback<RoutineResponse>() {
            @Override
            public void onResponse(Call<RoutineResponse> call, Response<RoutineResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    RoutineResponse routineResponse = response.body();
                    if (routineResponse.isStatus()) {
                        displayRoutineData(routineResponse);
                    } else {
                        showError(routineResponse.getMessage());
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
            errorMessage = "Routine not found for your program/intake/section";
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
        if (routineResponse.getProgramName() != null) {
            tvProgram.setText(routineResponse.getProgramName());
        } else if (routineResponse.getProgram() != null) {
            tvProgram.setText("Program: " + routineResponse.getProgram());
        }

        if (routineResponse.getSemester() != null) {
            tvSemester.setText(routineResponse.getSemester());
        }

        // Clear all existing day views
        dynamicDaysContainer.removeAllViews();

        // Use detailed_routines for better display
        if (routineResponse.getDetailedRoutines() != null && !routineResponse.getDetailedRoutines().isEmpty()) {
            displayDetailedRoutines(routineResponse.getDetailedRoutines());
        }
        // Fallback to basic routine if detailed_routines is empty
        else if (routineResponse.getRoutine() != null && !routineResponse.getRoutine().isEmpty()) {
            displayBasicRoutines(routineResponse.getRoutine());
        } else {
            showError("No classes scheduled for this semester");
            return;
        }

        dynamicDaysContainer.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
    }

    private void displayDetailedRoutines(List<RoutineResponse.DetailedRoutine> detailedRoutines) {
        // Group by day
        List<String> daysOrder = List.of("SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT");

        for (String day : daysOrder) {
            List<RoutineResponse.DetailedRoutine> dayClasses = new ArrayList<>();

            // Collect all classes for this day
            for (RoutineResponse.DetailedRoutine routine : detailedRoutines) {
                if (routine.getDay() != null && routine.getDay().equalsIgnoreCase(day)) {
                    dayClasses.add(routine);
                }
            }

            // Create day card if there are classes
            if (!dayClasses.isEmpty()) {
                createDayCardWithDetailedRoutines(day, dayClasses);
            }
        }
    }

    private void createDayCardWithDetailedRoutines(String day, List<RoutineResponse.DetailedRoutine> dayClasses) {
        // Inflate day card layout
        View dayCardView = LayoutInflater.from(this).inflate(R.layout.day_card_layout, dynamicDaysContainer, false);

        // Get references to the views inside the card
        TextView tvDayName = dayCardView.findViewById(R.id.tvDayName);
        TextView tvClassCount = dayCardView.findViewById(R.id.tvClassCount);
        LinearLayout classesContainer = dayCardView.findViewById(R.id.classesContainer);

        // Set day name
        String fullDayName = getFullDayName(day);
        tvDayName.setText(fullDayName);

        // Set class count
        int classCount = dayClasses.size();
        tvClassCount.setText(classCount + " Class" + (classCount > 1 ? "es" : ""));

        // Add class items
        for (RoutineResponse.DetailedRoutine routine : dayClasses) {
            View classItemView = getLayoutInflater().inflate(R.layout.class_item, classesContainer, false);

            TextView tvTime = classItemView.findViewById(R.id.tvTime);
            TextView tvEndTime = classItemView.findViewById(R.id.tvEndTime);
            TextView tvCourseCode = classItemView.findViewById(R.id.tvCourseCode);
            TextView tvFaculty = classItemView.findViewById(R.id.tvFaculty);
            TextView tvRoom = classItemView.findViewById(R.id.tvRoom);

            // Parse formatted time (e.g., "09:15 AM to 10:30 AM")
            if (routine.getFormattedTime() != null) {
                String[] timeParts = routine.getFormattedTime().split(" to ");
                if (timeParts.length == 2) {
                    tvTime.setText(timeParts[0].trim());
                    tvEndTime.setText(timeParts[1].trim());
                } else {
                    tvTime.setText(routine.getFormattedTime());
                    tvEndTime.setText("");
                }
            } else if (routine.getTimeSlot() != null) {
                String[] timeParts = routine.getTimeSlot().split(" to ");
                if (timeParts.length == 2) {
                    tvTime.setText(timeParts[0].trim());
                    tvEndTime.setText(timeParts[1].trim());
                } else {
                    tvTime.setText(routine.getTimeSlot());
                    tvEndTime.setText("");
                }
            }

            if (routine.getCourseCode() != null) {
                tvCourseCode.setText(routine.getCourseCode());
            } else {
                tvCourseCode.setText("N/A");
            }

            if (routine.getTeacherCode() != null) {
                tvFaculty.setText(routine.getTeacherCode());
            } else {
                tvFaculty.setText("N/A");
            }

            if (routine.getRoomNumber() != null && !routine.getRoomNumber().isEmpty()) {
                tvRoom.setText("Room: " + routine.getRoomNumber());
            } else {
                tvRoom.setText("Room: N/A");
            }

            classesContainer.addView(classItemView);
        }

        dynamicDaysContainer.addView(dayCardView);
    }

    private void displayBasicRoutines(List<RoutineResponse.DailyRoutine> routineList) {
        // This is a fallback if detailed_routines is not available
        // We need to parse the dynamic JSON structure
        List<DayClass> parsedClasses = new ArrayList<>();

        try {
            // Parse the routine list
            for (RoutineResponse.DailyRoutine routine : routineList) {
                if (routine.getDay() != null && routine.getClassDetails() != null) {
                    DayClass dayClass = new DayClass();
                    dayClass.day = routine.getDay();
                    dayClass.timeSlot = routine.getTimeSlot();
                    dayClass.classDetails = routine.getClassDetails();
                    parsedClasses.add(dayClass);
                }
            }

            // Group by day and display
            for (DayClass dayClass : parsedClasses) {
                createBasicDayCard(dayClass);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing basic routine: " + e.getMessage());
            showError("Error parsing routine data");
        }
    }

    private void createBasicDayCard(DayClass dayClass) {
        View dayCardView = LayoutInflater.from(this).inflate(R.layout.day_card_layout, dynamicDaysContainer, false);

        TextView tvDayName = dayCardView.findViewById(R.id.tvDayName);
        TextView tvClassCount = dayCardView.findViewById(R.id.tvClassCount);
        LinearLayout classesContainer = dayCardView.findViewById(R.id.classesContainer);

        String fullDayName = getFullDayName(dayClass.day);
        tvDayName.setText(fullDayName);
        tvClassCount.setText("1 Class");

        View classItemView = getLayoutInflater().inflate(R.layout.class_item, classesContainer, false);
        TextView tvTime = classItemView.findViewById(R.id.tvTime);
        TextView tvEndTime = classItemView.findViewById(R.id.tvEndTime);
        TextView tvCourseCode = classItemView.findViewById(R.id.tvCourseCode);
        TextView tvFaculty = classItemView.findViewById(R.id.tvFaculty);
        TextView tvRoom = classItemView.findViewById(R.id.tvRoom);

        // Parse class details (format: "FIN 2102: ZS R: 4704")
        if (dayClass.classDetails != null) {
            String[] parts = dayClass.classDetails.split(":");
            if (parts.length >= 1) {
                tvCourseCode.setText(parts[0].trim());
            }
            if (parts.length >= 2) {
                tvFaculty.setText(parts[1].trim());
            }
            if (parts.length >= 3) {
                tvRoom.setText("Room: " + parts[2].replace("R:", "").trim());
            }
        }

        // Parse time slot
        if (dayClass.timeSlot != null) {
            String[] timeParts = dayClass.timeSlot.split(" to ");
            if (timeParts.length == 2) {
                tvTime.setText(timeParts[0].trim());
                tvEndTime.setText(timeParts[1].trim());
            } else {
                tvTime.setText(dayClass.timeSlot);
                tvEndTime.setText("");
            }
        }

        classesContainer.addView(classItemView);
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
                return "THURSDAY";
            case "FRI":
                return "FRIDAY";
            case "SAT":
                return "SATURDAY";
            default:
                return dayAbbreviation;
        }
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
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Helper class for basic routine parsing
    private static class DayClass {
        String day;
        String timeSlot;
        String classDetails;
    }
}