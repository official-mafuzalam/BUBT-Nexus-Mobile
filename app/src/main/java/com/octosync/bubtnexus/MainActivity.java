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
        findViewById(R.id.btnProfile).setOnClickListener(v -> openSection("Profile"));
        etSearch.setOnClickListener(v -> showToast("Search opened"));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showToast("Home selected");
                return true;
            } else if (id == R.id.nav_logout) {
                logoutUser();
                return true;
            }
            return false;
        });
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
                    // Token is valid, update user data if needed
                    UserResponse userResponse = response.body();
                    if (userResponse.getUser() != null) {
                        String currentUserName = sessionManager.getUserName();
                        String newUserName = userResponse.getUser().getName();

                        // Update if user name changed or not stored
                        if (currentUserName == null || !currentUserName.equals(newUserName)) {
                            sessionManager.saveUserData(
                                    userResponse.getUser().getName(),
                                    userResponse.getUser().getEmail()
                            );
                            tvWelcome.setText("Welcome, " + newUserName + "!");
                        }
                    }
                } else {
                    // Token is invalid, redirect to login
                    sessionManager.clear();
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Network error, but we'll keep the user logged in with stored data
                showToast("Network error, but you can continue using the app");
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