package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.octosync.bubtnexus.utils.SessionManager;

public class AssignmentActivity extends AppCompatActivity {

    // UI Components
    private ProgressBar progressBar;
    private TextView tvError;
    private ImageButton btnBack;

    // Session
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

        setContentView(R.layout.activity_assignment);

        initializeViews();
        setupClickListeners();
        loadAssignmentData();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AssignmentActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }


    private void navigateToMainActivity() {
        Intent intent = new Intent(AssignmentActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToTaskActivity() {
        Intent intent = new Intent(AssignmentActivity.this, TaskActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(AssignmentActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void logoutUser() {
        // Clear local session
        sessionManager.clear();
        showToast("Logged out successfully");
        redirectToLogin();
    }

    private void loadAssignmentData() {
        showLoading(true);

        // Simulate API call delay for coming soon message
        new Handler().postDelayed(() -> {
            showLoading(false);
            showComingSoon();
        }, 1000); // 1 second delay to show loading state
    }

    private void showComingSoon() {
        // The coming soon message is already in the layout
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // You can show a toast to indicate the feature is coming soon
        showToast("Assignment feature coming soon!");
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (show) {
            if (tvError != null) {
                tvError.setVisibility(View.GONE);
            }
        }
    }

    private void showError(String message) {
        if (tvError != null) {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}