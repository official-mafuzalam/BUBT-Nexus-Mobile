package com.octosync.bubtnexus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.octosync.bubtnexus.adapters.RidesAdapter;
import com.octosync.bubtnexus.models.Ride;
import com.octosync.bubtnexus.models.RidesResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindRideActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;

    private RecyclerView recyclerViewRides;
    private ProgressBar progressBar;
    private TextView tvError, tvNoRides;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnBack;
    private Button btnRefresh;

    private RidesAdapter ridesAdapter;
    private SharedPrefManager sharedPrefManager;
    private FusedLocationProviderClient fusedLocationClient;

    private double currentLat = 0;
    private double currentLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();

        // Check location permission
        if (checkLocationPermission()) {
            getCurrentLocationAndLoadRides();
        } else {
            requestLocationPermission();
        }
    }

    private void initializeViews() {
        recyclerViewRides = findViewById(R.id.recyclerViewRides);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvNoRides = findViewById(R.id.tvNoRides);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRefresh.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocationAndLoadRides();
            } else {
                requestLocationPermission();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (checkLocationPermission()) {
                getCurrentLocationAndLoadRides();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                requestLocationPermission();
            }
        });
    }

    private void setupRecyclerView() {
        ridesAdapter = new RidesAdapter(this, null, new RidesAdapter.OnRideClickListener() {
            @Override
            public void onRideClick(Ride ride) {
                // Open ride details
                Intent intent = new Intent(FindRideActivity.this, RideDetailsActivity.class);
                intent.putExtra("ride_id", ride.getId());
                startActivity(intent);
            }

            @Override
            public void onJoinClick(Ride ride) {
                // Show join dialog
                showJoinRideDialog(ride);
            }
        });

        recyclerViewRides.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRides.setAdapter(ridesAdapter);
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocationAndLoadRides() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvNoRides.setVisibility(View.GONE);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        loadNearbyRides(currentLat, currentLng);
                    } else {
                        // Use default location (BUBT coordinates)
                        currentLat = 23.810331;
                        currentLng = 90.412521;
                        loadNearbyRides(currentLat, currentLng);
                    }
                })
                .addOnFailureListener(e -> {
                    // Use default location on failure
                    currentLat = 23.810331;
                    currentLng = 90.412521;
                    loadNearbyRides(currentLat, currentLng);
                });
    }

    private void loadNearbyRides(double latitude, double longitude) {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RidesResponse> call = apiService.getNearbyRides(
                "Bearer " + token,
                latitude,
                longitude,
                20, // 20km radius
                null,
                null
        );

        call.enqueue(new Callback<RidesResponse>() {
            @Override
            public void onResponse(Call<RidesResponse> call, Response<RidesResponse> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Ride> rides = response.body().getData();
                    if (rides != null && !rides.isEmpty()) {
                        showRides(rides);
                    } else {
                        showNoRides();
                    }
                } else {
                    showError("Failed to load rides");
                }
            }

            @Override
            public void onFailure(Call<RidesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showRides(List<Ride> rides) {
        recyclerViewRides.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        tvNoRides.setVisibility(View.GONE);
        ridesAdapter.updateData(rides);
    }

    private void showNoRides() {
        recyclerViewRides.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        tvNoRides.setVisibility(View.VISIBLE);
        tvNoRides.setText("No rides available nearby");
    }

    private void showError(String message) {
        recyclerViewRides.setVisibility(View.GONE);
        tvNoRides.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private void showJoinRideDialog(Ride ride) {
        // Create and show join dialog
        JoinRideDialog dialog = new JoinRideDialog(this, ride, (seats, message) -> {
            joinRide(ride.getId(), seats, message);
        });
        dialog.show();
    }

    private void joinRide(int rideId, int seats, String message) {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        // Create request
        com.octosync.bubtnexus.models.RideRequestRequest request =
                new com.octosync.bubtnexus.models.RideRequestRequest(seats, message);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<com.octosync.bubtnexus.models.ApiResponse> call =
                apiService.requestRide("Bearer " + token, rideId, request);

        call.enqueue(new Callback<com.octosync.bubtnexus.models.ApiResponse>() {
            @Override
            public void onResponse(Call<com.octosync.bubtnexus.models.ApiResponse> call,
                                   Response<com.octosync.bubtnexus.models.ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FindRideActivity.this,
                            "Ride request sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindRideActivity.this,
                            "Failed to send request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.octosync.bubtnexus.models.ApiResponse> call, Throwable t) {
                Toast.makeText(FindRideActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndLoadRides();
            } else {
                Toast.makeText(this, "Location permission required to find rides",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}