package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.octosync.bubtnexus.adapters.NoticeAdapter;
import com.octosync.bubtnexus.models.Notice;
import com.octosync.bubtnexus.models.NoticesResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends AppCompatActivity {

    // UI Components
    private RecyclerView recyclerViewNotices;
    private ProgressBar progressBar;
    private TextView tvError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnBack;

    // Adapter and Session
    private NoticeAdapter noticeAdapter;
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

        setContentView(R.layout.activity_notice);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadNotices();

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadNotices);
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(NoticeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        recyclerViewNotices = findViewById(R.id.recyclerViewNotices);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }
    private void navigateToMainActivity() {
        Intent intent = new Intent(NoticeActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToTaskActivity() {
        Intent intent = new Intent(NoticeActivity.this, TaskActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(NoticeActivity.this, ProfileActivity.class);
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

    private void setupRecyclerView() {
        noticeAdapter = new NoticeAdapter(null);
        recyclerViewNotices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotices.setAdapter(noticeAdapter);
    }

    private void loadNotices() {
        showLoading(true);

        String token = sessionManager.getToken();
        if (token == null) {
            showError("Please login again");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<NoticesResponse> call = apiService.getNotices("Bearer " + token);

        call.enqueue(new Callback<NoticesResponse>() {
            @Override
            public void onResponse(Call<NoticesResponse> call, Response<NoticesResponse> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Notice> notices = response.body().getData();
                    if (notices != null && !notices.isEmpty()) {
                        showNotices(notices);
                    } else {
                        showError("No notices available");
                    }
                } else {
                    showError("Failed to load notices. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<NoticesResponse> call, Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showNotices(List<Notice> notices) {
        if (recyclerViewNotices != null) {
            recyclerViewNotices.setVisibility(View.VISIBLE);
        }
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
        if (noticeAdapter != null) {
            noticeAdapter.updateNotices(notices);
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (show) {
            if (tvError != null) {
                tvError.setVisibility(View.GONE);
            }
            if (recyclerViewNotices != null) {
                recyclerViewNotices.setVisibility(View.GONE);
            }
        }
    }

    private void showError(String message) {
        if (tvError != null) {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
        }
        if (recyclerViewNotices != null) {
            recyclerViewNotices.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notices when activity resumes
        if (noticeAdapter == null || noticeAdapter.getItemCount() == 0) {
            loadNotices();
        }
    }
}