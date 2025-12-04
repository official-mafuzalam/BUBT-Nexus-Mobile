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

import com.octosync.bubtnexus.adapters.RideRequestsAdapter;
import com.octosync.bubtnexus.models.RideRequest;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideRequestsActivity extends AppCompatActivity {

    private int rideId; // 0 means show all requests, otherwise show for specific ride

    private RecyclerView recyclerViewRequests;
    private ProgressBar progressBar;
    private TextView tvError, tvNoRequests;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnBack;

    private RideRequestsAdapter requestsAdapter;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_requests);

        // Get ride ID from intent (optional)
        rideId = getIntent().getIntExtra("ride_id", 0);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadRideRequests();
    }

    private void initializeViews() {
        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvNoRequests = findViewById(R.id.tvNoRequests);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        swipeRefreshLayout.setOnRefreshListener(this::loadRideRequests);
    }

    private void setupRecyclerView() {
        requestsAdapter = new RideRequestsAdapter(this, null,
                new RideRequestsAdapter.OnRequestActionListener() {
                    @Override
                    public void onAccept(RideRequest request) {
                        handleRequest(request.getId(), "accept");
                    }

                    @Override
                    public void onReject(RideRequest request) {
                        handleRequest(request.getId(), "reject");
                    }
                });

        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRequests.setAdapter(requestsAdapter);
    }

    private void loadRideRequests() {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvNoRequests.setVisibility(View.GONE);

        // Note: You need to create an API endpoint to get ride requests
        // This is a placeholder - adjust based on your actual API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // You'll need to implement the actual API call here
        // For now, show no requests
        showNoRequests();
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    private void handleRequest(int requestId, String action) {
        if (rideId == 0) {
            Toast.makeText(this, "Invalid ride", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        com.octosync.bubtnexus.models.ActionRequest actionRequest =
                new com.octosync.bubtnexus.models.ActionRequest(action);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<com.octosync.bubtnexus.models.ApiResponse> call =
                apiService.handleRideRequest("Bearer " + token, rideId, requestId, actionRequest);

        call.enqueue(new Callback<com.octosync.bubtnexus.models.ApiResponse>() {
            @Override
            public void onResponse(Call<com.octosync.bubtnexus.models.ApiResponse> call,
                                   Response<com.octosync.bubtnexus.models.ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RideRequestsActivity.this,
                            "Request " + action + "ed", Toast.LENGTH_SHORT).show();
                    loadRideRequests(); // Refresh list
                } else {
                    Toast.makeText(RideRequestsActivity.this,
                            "Failed to " + action + " request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.octosync.bubtnexus.models.ApiResponse> call, Throwable t) {
                Toast.makeText(RideRequestsActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRequests(List<RideRequest> requests) {
        recyclerViewRequests.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvNoRequests.setVisibility(View.GONE);
        requestsAdapter.updateData(requests);
    }

    private void showNoRequests() {
        recyclerViewRequests.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        tvNoRequests.setVisibility(View.VISIBLE);
        if (rideId == 0) {
            tvNoRequests.setText("No ride requests");
        } else {
            tvNoRequests.setText("No pending requests for this ride");
        }
    }

    private void showError(String message) {
        recyclerViewRequests.setVisibility(View.GONE);
        tvNoRequests.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}