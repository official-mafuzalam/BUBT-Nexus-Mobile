package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.octosync.bubtnexus.adapters.PassengersAdapter;
import com.octosync.bubtnexus.models.Passenger;
import com.octosync.bubtnexus.models.Ride;
import com.octosync.bubtnexus.models.RideResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.DateTimeUtils;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailsActivity extends AppCompatActivity {

    private int rideId;
    private Ride ride;
    private boolean isDriver = false;

    private TextView tvFromLocation, tvToLocation, tvDriverName, tvDepartureTime;
    private TextView tvAvailableSeats, tvFare, tvNotes, tvVehicleInfo;
    private RecyclerView recyclerViewPassengers;
    private Button btnChat, btnMap, btnManage;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private SharedPrefManager sharedPrefManager;
    private PassengersAdapter passengersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);

        // Get ride ID from intent
        rideId = getIntent().getIntExtra("ride_id", 0);
        if (rideId == 0) {
            Toast.makeText(this, "Invalid ride", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sharedPrefManager = SharedPrefManager.getInstance(this);

        initializeViews();
        setupClickListeners();
        loadRideDetails();
    }

    private void initializeViews() {
        tvFromLocation = findViewById(R.id.tvFromLocation);
        tvToLocation = findViewById(R.id.tvToLocation);
        tvDriverName = findViewById(R.id.tvDriverName);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvAvailableSeats = findViewById(R.id.tvAvailableSeats);
        tvFare = findViewById(R.id.tvFare);
        tvNotes = findViewById(R.id.tvNotes);
        tvVehicleInfo = findViewById(R.id.tvVehicleInfo);
        recyclerViewPassengers = findViewById(R.id.recyclerViewPassengers);
        btnChat = findViewById(R.id.btnChat);
        btnMap = findViewById(R.id.btnMap);
        btnManage = findViewById(R.id.btnManage);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Setup passengers recycler view
        passengersAdapter = new PassengersAdapter(this, null);
        recyclerViewPassengers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPassengers.setAdapter(passengersAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, RideChatActivity.class);
            intent.putExtra("ride_id", rideId);
            startActivity(intent);
        });

        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, RideMapActivity.class);
            intent.putExtra("ride_id", rideId);
            intent.putExtra("from_lat", ride.getFromLat());
            intent.putExtra("from_lng", ride.getFromLng());
            intent.putExtra("to_lat", ride.getToLat());
            intent.putExtra("to_lng", ride.getToLng());
            startActivity(intent);
        });

        btnManage.setOnClickListener(v -> {
            if (isDriver) {
                // Driver can manage requests
                Intent intent = new Intent(this, RideRequestsActivity.class);
                intent.putExtra("ride_id", rideId);
                startActivity(intent);
            } else {
                // Passenger can view their request status
                Toast.makeText(this, "You are a passenger on this ride", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRideDetails() {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RideResponse> call = apiService.getRideDetails("Bearer " + token, rideId);

        call.enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ride = response.body().getData();
                    updateUI();
                } else {
                    Toast.makeText(RideDetailsActivity.this,
                            "Failed to load ride details", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RideDetailsActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateUI() {
        if (ride == null) return;

        // Check if current user is the driver
        int currentUserId = sharedPrefManager.getUserId();
        isDriver = (ride.getDriverId() == currentUserId);

        // Update views
        tvFromLocation.setText(ride.getFromLocation());
        tvToLocation.setText(ride.getToLocation());
        tvDriverName.setText(ride.getDriverName());
        tvDepartureTime.setText(DateTimeUtils.formatDateTime(ride.getDepartureTime()));
        tvAvailableSeats.setText(String.format("Available: %d/%d",
                ride.getAvailableSeats(), ride.getTotalSeats()));
        tvFare.setText(String.format("৳%.2f per seat", ride.getFarePerSeat()));

        if (ride.getNotes() != null && !ride.getNotes().isEmpty()) {
            tvNotes.setText(ride.getNotes());
            tvNotes.setVisibility(View.VISIBLE);
        } else {
            tvNotes.setVisibility(View.GONE);
        }

        // Vehicle info
        String vehicleInfo = "";
        if (ride.getVehicleType() != null && !ride.getVehicleType().isEmpty()) {
            vehicleInfo += ride.getVehicleType();
        }
        if (ride.getVehicleNumber() != null && !ride.getVehicleNumber().isEmpty()) {
            vehicleInfo += " • " + ride.getVehicleNumber();
        }
        if (!vehicleInfo.isEmpty()) {
            tvVehicleInfo.setText(vehicleInfo);
            tvVehicleInfo.setVisibility(View.VISIBLE);
        } else {
            tvVehicleInfo.setVisibility(View.GONE);
        }

        // Update passengers list
        List<Passenger> passengers = ride.getConfirmedPassengers();
        if (passengers != null && !passengers.isEmpty()) {
            passengersAdapter.updateData(passengers);
            recyclerViewPassengers.setVisibility(View.VISIBLE);
        } else {
            recyclerViewPassengers.setVisibility(View.GONE);
        }

        // Update button text based on user role
        if (isDriver) {
            btnManage.setText("Manage Requests");
            btnManage.setVisibility(View.VISIBLE);
        } else {
            btnManage.setText("My Request");
            // Check if user is a passenger
            boolean isPassenger = false;
            if (passengers != null) {
                for (Passenger passenger : passengers) {
                    if (passenger.getPassengerId() == currentUserId) {
                        isPassenger = true;
                        break;
                    }
                }
            }
            btnManage.setVisibility(isPassenger ? View.VISIBLE : View.GONE);
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}