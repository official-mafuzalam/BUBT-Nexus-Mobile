package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.octosync.bubtnexus.models.UserResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvDate;
    private TextInputEditText etSearch;
    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;

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

        // Set welcome message with stored user name
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Welcome, " + userName + "!");
        } else {
            tvWelcome.setText("Welcome!");
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.btnRoutine).setOnClickListener(v -> openSection("Routine"));
        findViewById(R.id.btnAssignments).setOnClickListener(v -> openSection("Assignments"));
        findViewById(R.id.btnNotices).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btnCommunity).setOnClickListener(v -> openSection("Community"));
        findViewById(R.id.btnEvents).setOnClickListener(v -> openSection("Events"));
        findViewById(R.id.btnProfile).setOnClickListener(v -> {
            navigateToProfileActivity();
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
        Call<UserResponse> call = apiService.getUser(sessionManager.getToken());

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();

                    // Check if the API call was successful and data exists
                    if (userResponse.isSuccess() && userResponse.getData() != null && userResponse.getData().getUser() != null) {
                        String currentUserName = sessionManager.getUserName();
                        String newUserName = userResponse.getData().getUser().getName();

                        // Update if user name changed or not stored
                        if (currentUserName == null || !currentUserName.equals(newUserName)) {
                            sessionManager.saveUserData(
                                    userResponse.getData().getUser().getName(),
                                    userResponse.getData().getUser().getEmail(),
                                    userResponse.getData().getUser().getUserType(),
                                    userResponse.getData().getUser().isStudent(),
                                    userResponse.getData().getUser().isFaculty()
                            );

                            // Update roles if available
                            if (userResponse.getData().getUser().getRoles() != null) {
                                sessionManager.saveUserRoles(userResponse.getData().getUser().getRoles());
                            }

                            tvWelcome.setText("Welcome, " + newUserName + "!");
                        }
                    } else {
                        // API returned success: false or no user data
                        if (userResponse.getMessage() != null) {
                            showToast(userResponse.getMessage());
                        }
                        // Token might be invalid, redirect to login
                        sessionManager.clear();
                        redirectToLogin();
                    }
                } else {
                    // HTTP error (401, 500, etc.)
                    if (response.code() == 401) {
                        showToast("Session expired. Please login again.");
                    } else {
                        showToast("Failed to verify user. Please try again.");
                    }
                    sessionManager.clear();
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Network error, but we'll keep the user logged in with stored data
                showToast("Network error, but you can continue using the app");
                // You might want to show a subtle indicator that data might be outdated
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