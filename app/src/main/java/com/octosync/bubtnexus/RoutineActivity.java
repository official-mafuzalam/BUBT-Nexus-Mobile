package com.octosync.bubtnexus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.octosync.bubtnexus.models.RoutineResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutineActivity extends AppCompatActivity {

    private static final String TAG = "RoutineActivity";

    private ApiService apiService;
    private SharedPreferencesManager prefs;

    private ProgressBar progressBar;
    private TextView tvError, tvProgram, tvSemester;
    private LinearLayout routineContainer;
    private ImageButton btnBack;

    // Day containers
    private LinearLayout sundayClasses, mondayClasses, tuesdayClasses, wednesdayClasses, thursdayClasses;
    private TextView tvSundayCount, tvMondayCount, tvTuesdayCount, tvWednesdayCount, tvThursdayCount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_routine);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        initApiService();
        loadRoutineData();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvProgram = findViewById(R.id.tvProgram);
        tvSemester = findViewById(R.id.tvSemester);
        routineContainer = findViewById(R.id.routineContainer);
        btnBack = findViewById(R.id.btnBack);

        // Initialize day containers
        sundayClasses = findViewById(R.id.sundayClasses);
        mondayClasses = findViewById(R.id.mondayClasses);
        tuesdayClasses = findViewById(R.id.tuesdayClasses);
        wednesdayClasses = findViewById(R.id.wednesdayClasses);
        thursdayClasses = findViewById(R.id.thursdayClasses);

        // Initialize class count text views
        tvSundayCount = findViewById(R.id.tvSundayCount);
        tvMondayCount = findViewById(R.id.tvMondayCount);
        tvTuesdayCount = findViewById(R.id.tvTuesdayCount);
        tvWednesdayCount = findViewById(R.id.tvWednesdayCount);
        tvThursdayCount = findViewById(R.id.tvThursdayCount);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void initApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
        prefs = new SharedPreferencesManager(this);
    }

    private void loadRoutineData() {
        showLoading(true);

        String token = prefs.getToken();
        if (token == null || token.isEmpty()) {
            showError("Please login first");
            return;
        }

        // You need to get these values from your user data or intent
        String program = "006"; // Replace with actual program code
        String semester = "611"; // Replace with actual semester code
        String intake = "50 - 1"; // Replace with actual intake

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
                        showError("Failed to load routine data");
                    }
                } else {
                    showError("Failed to load routine: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RoutineResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Routine API call failed: " + t.getMessage());
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void displayRoutineData(RoutineResponse routineResponse) {
        // Set program and semester info
        tvProgram.setText(routineResponse.getProgram());
        tvSemester.setText(routineResponse.getSemester());

        // Clear all existing class views
        clearAllClassViews();

        // Populate routine for each day
        if (routineResponse.getRoutine() != null) {
            for (com.octosync.bubtnexus.models.RoutineDay day : routineResponse.getRoutine()) {
                switch (day.getDay().toUpperCase()) {
                    case "SUN":
                        populateDayClasses(sundayClasses, tvSundayCount, day);
                        break;
                    case "MON":
                        populateDayClasses(mondayClasses, tvMondayCount, day);
                        break;
                    case "TUE":
                        populateDayClasses(tuesdayClasses, tvTuesdayCount, day);
                        break;
                    case "WED":
                        populateDayClasses(wednesdayClasses, tvWednesdayCount, day);
                        break;
                    case "THR":
                        populateDayClasses(thursdayClasses, tvThursdayCount, day);
                        break;
                }
            }
        }

        routineContainer.setVisibility(View.VISIBLE);
    }

    private void populateDayClasses(LinearLayout dayContainer, TextView countView, com.octosync.bubtnexus.models.RoutineDay day) {
        if (day.getClasses() != null && !day.getClasses().isEmpty()) {
            // Set class count
            int classCount = day.getClasses().size();
            countView.setText(classCount + " Class" + (classCount > 1 ? "es" : ""));

            // Add class items
            for (com.octosync.bubtnexus.models.ClassItem classItem : day.getClasses()) {
                View classItemView = getLayoutInflater().inflate(R.layout.class_item_background, dayContainer, false);

                TextView tvTime = classItemView.findViewById(R.id.tvTime);
                TextView tvEndTime = classItemView.findViewById(R.id.tvEndTime);
                TextView tvCourseCode = classItemView.findViewById(R.id.tvCourseCode);
                TextView tvFaculty = classItemView.findViewById(R.id.tvFaculty);
                TextView tvRoom = classItemView.findViewById(R.id.tvRoom);

                // Parse time (assuming format: "10:30 AM to 11:45 AM")
                String[] timeParts = classItem.getTime().split(" to ");
                if (timeParts.length == 2) {
                    tvTime.setText(timeParts[0]);
                    tvEndTime.setText(timeParts[1]);
                } else {
                    tvTime.setText(classItem.getTime());
                    tvEndTime.setText("");
                }

                tvCourseCode.setText(classItem.getCourseCode());
                tvFaculty.setText(classItem.getFacultyCode());
                tvRoom.setText("Room: " + classItem.getRoom());

                dayContainer.addView(classItemView);
            }
        } else {
            countView.setText("No Classes");
            // Add a "No classes" message
            TextView noClassText = new TextView(this);
            noClassText.setText("No classes scheduled");
            noClassText.setTextColor(getResources().getColor(R.color.text_secondary));
            noClassText.setTextSize(14);
            noClassText.setPadding(16, 16, 16, 16);
            noClassText.setGravity(android.view.Gravity.CENTER);
            dayContainer.addView(noClassText);
        }
    }

    private void clearAllClassViews() {
        sundayClasses.removeAllViews();
        mondayClasses.removeAllViews();
        tuesdayClasses.removeAllViews();
        wednesdayClasses.removeAllViews();
        thursdayClasses.removeAllViews();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            routineContainer.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        routineContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}