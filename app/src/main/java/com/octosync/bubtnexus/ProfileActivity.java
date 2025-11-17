package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.octosync.bubtnexus.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;
    private TextView tvUserName, tvStudentId, tvDepartment, tvEmail, tvPhone, tvSemester, tvBatch, tvDate;
    private MaterialButton btnEditProfile, btnSettings; // Changed from ImageButton to MaterialButton
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        setCurrentDate();
        loadUserData();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void initializeViews() {
            // TextViews
            tvUserName = findViewById(R.id.tvUserName);
            tvStudentId = findViewById(R.id.tvStudentId);
            tvDepartment = findViewById(R.id.tvDepartment);
            tvEmail = findViewById(R.id.tvEmail);
            tvPhone = findViewById(R.id.tvPhone);
            tvSemester = findViewById(R.id.tvSemester);
            tvBatch = findViewById(R.id.tvBatch);
            tvDate = findViewById(R.id.tvDate);

            // Buttons - IMPORTANT: These are MaterialButtons, not ImageButtons
            btnBack = findViewById(R.id.btnBack);
            btnEditProfile = findViewById(R.id.btnEditProfile);
            btnSettings = findViewById(R.id.btnSettings);

            // Bottom Navigation
            bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupClickListeners() {
        Log.d(TAG, "setupClickListeners: Setting up click listeners");

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                navigateToMainActivity();
            });
        }

        btnEditProfile.setOnClickListener(v -> {
            showToast("Edit Profile clicked");
        });

        // Settings button - Add temporary button if not found
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                showToast("Settings clicked");
            });
        } else {
            Log.w(TAG, "Settings button not found in XML");
            // You can programmatically create a button here if needed
        }
    }

    private void setupBottomNavigation() {
        try {
            // Set profile as selected by default
            bottomNavigation.setSelectedItemId(R.id.nav_profile);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    navigateToMainActivity();
                    return true;
                } else if (id == R.id.nav_task) {
                    showToast("Task feature coming soon");
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Already on profile page
                    return true;
                } else if (id == R.id.nav_logout) {
                    logoutUser();
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
            Log.e(TAG, "setupBottomNavigation: Error", e);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void logoutUser() {
        sessionManager.clear();
        showToast("Logged out successfully");
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        try {
            // Get user data from session
            String userName = sessionManager.getUserName();
            String userEmail = sessionManager.getUserEmail();

            // Set basic user info
            if (tvUserName != null && userName != null) {
                tvUserName.setText(userName);
            }

            if (tvEmail != null && userEmail != null) {
                tvEmail.setText(userEmail);
            }

            // Set placeholder values
            if (tvStudentId != null) tvStudentId.setText("ID: 2023-123-456");
            if (tvDepartment != null) tvDepartment.setText("Computer Science & Engineering");
            if (tvPhone != null) tvPhone.setText("+880 1234 567890");
            if (tvSemester != null) tvSemester.setText("8th Semester");
            if (tvBatch != null) tvBatch.setText("2020-21");

        } catch (Exception e) {
            Log.e(TAG, "loadUserData: Error", e);
        }
    }

    private void setCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            if (tvDate != null) {
                tvDate.setText(currentDate);
            }
        } catch (Exception e) {
            Log.e(TAG, "setCurrentDate: Error", e);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        navigateToMainActivity();
    }
}