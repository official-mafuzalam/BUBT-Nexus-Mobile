package com.octosync.bubtnexus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.octosync.bubtnexus.adapters.RideAdapter;
import com.octosync.bubtnexus.models.NearbyRidesResponse;
import com.octosync.bubtnexus.models.Ride;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideSharingActivity extends AppCompatActivity implements RideAdapter.OnRideClickListener {

    // UI Components
    private RecyclerView recyclerViewRides;
    private ProgressBar progressBar;
    private TextView tvError, tvLocation;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnBack, btnCreate;
    private LinearLayout llEmpty;

    // Adapter and Session
    private RideAdapter rideAdapter;
    private SessionManager sessionManager;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private double currentLatitude = 0;
    private double currentLongitude = 0;

    // Filter parameters
    private int radius = 20; // Default radius in km
    private Integer maxSeats = null;
    private Double maxFare = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check if user is logged in, if not redirect to login
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_ride_sharing);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();

        // Request location permission
        requestLocationPermission();

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadNearbyRides);
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RideSharingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvLocation = findViewById(R.id.tvLocation);
        recyclerViewRides = findViewById(R.id.recyclerViewRides);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnBack = findViewById(R.id.btnBack);
        btnCreate = findViewById(R.id.btnCreate);
        llEmpty = findViewById(R.id.llEmpty);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(RideSharingActivity.this, RideCreateActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void setupRecyclerView() {
        rideAdapter = new RideAdapter(null, this, this);
        recyclerViewRides.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRides.setAdapter(rideAdapter);
    }

    @Override
    public void onRideClick(Ride ride) {
        // Open ride details when item is clicked
        Intent intent = new Intent(RideSharingActivity.this, RideDetailsActivity.class);
        intent.putExtra("ride_id", ride.getId());
        startActivity(intent);
    }

    @Override
    public void onActionButtonClick(Ride ride, boolean isMyRide) {
        if (isMyRide) {
            // If it's my ride, open ride details/management
            Intent intent = new Intent(RideSharingActivity.this, RideDetailsActivity.class);
            intent.putExtra("ride_id", ride.getId());
            intent.putExtra("is_my_ride", true);
            startActivity(intent);
        } else {
            // If it's not my ride, open booking activity
            Intent intent = new Intent(RideSharingActivity.this, BookRideActivity.class);
            intent.putExtra("ride_id", ride.getId());
            startActivity(intent);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                showError("Location permission denied. Showing default rides.");
                // Use default location or show error
                currentLatitude = 23.810331;
                currentLongitude = 90.412521;
                tvLocation.setText("Dhaka, Bangladesh (Default)");
                loadNearbyRides();
            }
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            tvLocation.setText("Lat: " + String.format("%.4f", currentLatitude) +
                                    ", Lng: " + String.format("%.4f", currentLongitude));
                            loadNearbyRides();
                        } else {
                            showError("Unable to get location. Using default.");
                            currentLatitude = 23.810331;
                            currentLongitude = 90.412521;
                            tvLocation.setText("Dhaka, Bangladesh (Default)");
                            loadNearbyRides();
                        }
                    });
        }
    }

    private void loadNearbyRides() {
        showLoading(true);

        if (currentLatitude == 0 || currentLongitude == 0) {
            getCurrentLocation();
            return;
        }

        String token = sessionManager.getToken();
        if (token == null) {
            showError("Please login again");
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<NearbyRidesResponse> call = apiService.getNearbyRides(
                "Bearer " + token,
                currentLatitude,
                currentLongitude,
                radius,
                maxSeats,
                maxFare
        );

        call.enqueue(new Callback<NearbyRidesResponse>() {
            @Override
            public void onResponse(Call<NearbyRidesResponse> call, Response<NearbyRidesResponse> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Ride> rides = response.body().getRides();
                    if (rides != null && !rides.isEmpty()) {
                        showRides(rides);
                    } else {
                        showEmptyState();
                    }
                } else {
                    showError("Failed to load rides. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NearbyRidesResponse> call, Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showRides(List<Ride> rides) {
        recyclerViewRides.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        rideAdapter.updateRides(rides);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            tvError.setVisibility(View.GONE);
            recyclerViewRides.setVisibility(View.GONE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        recyclerViewRides.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        showToast(message);
    }

    private void showEmptyState() {
        llEmpty.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        recyclerViewRides.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh rides when returning from create activity
            loadNearbyRides();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh rides when activity resumes
        if (rideAdapter == null || rideAdapter.getItemCount() == 0) {
            loadNearbyRides();
        }
    }
}