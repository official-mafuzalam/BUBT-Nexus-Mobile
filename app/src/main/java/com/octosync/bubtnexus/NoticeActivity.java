package com.octosync.bubtnexus;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.octosync.bubtnexus.adapters.NoticeAdapter;
import com.octosync.bubtnexus.models.Notice;
import com.octosync.bubtnexus.models.NoticesResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotices;
    private ProgressBar progressBar;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NoticeAdapter noticeAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        sessionManager = new SessionManager(this);
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadNotices();

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadNotices);
    }

    private void initializeViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewNotices = findViewById(R.id.recyclerViewNotices);
        progressBar = findViewById(R.id.progressBar);
        textViewError = findViewById(R.id.textViewError);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        noticeAdapter = new NoticeAdapter(null);
        recyclerViewNotices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotices.setAdapter(noticeAdapter);
    }

    private void loadNotices() {
        // Hide error and show loading
        textViewError.setVisibility(View.GONE);

        if (!swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String token = sessionManager.getToken();
        if (token == null) {
            showError("Please login again");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<NoticesResponse> call = apiService.getNotices(token);

        call.enqueue(new Callback<NoticesResponse>() {
            @Override
            public void onResponse(Call<NoticesResponse> call, Response<NoticesResponse> response) {
                progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showNotices(List<Notice> notices) {
        recyclerViewNotices.setVisibility(View.VISIBLE);
        textViewError.setVisibility(View.GONE);
        noticeAdapter.updateNotices(notices);
    }

    private void showError(String message) {
        recyclerViewNotices.setVisibility(View.GONE);
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notices when activity resumes
        if (noticeAdapter.getItemCount() == 0) {
            loadNotices();
        }
    }
}