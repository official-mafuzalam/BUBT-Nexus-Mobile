package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView; // Make sure this import is correct
import com.google.android.material.textfield.TextInputEditText;
import com.octosync.bubtnexus.models.Ride;
import com.octosync.bubtnexus.models.RideDetailsResponse;
import com.octosync.bubtnexus.models.RideRequest;
import com.octosync.bubtnexus.models.RideRequestResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideDetailsActivity extends AppCompatActivity {

    // UI Components
    private TextView tvTitle, tvRideId, tvDriverName, tvDriverStatus, tvFromLocation, tvToLocation;
    private TextView tvDepartureTime, tvAvailableSeats, tvFare, tvVehicleType, tvVehicleNumber;
    private TextView tvDistance, tvStatus, tvNotes, tvAlreadyRequested, tvRequestSent;
    private ProgressBar progressBar;
    private TextView tvError;
    private LinearLayout llContent;
    private ImageButton btnBack;
    private MaterialCardView cardNotes;
    private Spinner spinnerSeats;
    private TextInputEditText etMessage;
    private Button btnRequestRide;

    // Session and Data
    private SessionManager sessionManager;
    private int rideId;
    private Ride ride;
    private int selectedSeats = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Get ride ID from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("ride_id")) {
            rideId = intent.getIntExtra("ride_id", 0);
        } else {
            showToast("Invalid ride selection");
            finish();
            return;
        }

        setContentView(R.layout.activity_ride_details);
        initializeViews();
        setupClickListeners();
        loadRideDetails();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RideDetailsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        // TextViews
        tvTitle = findViewById(R.id.tvTitle);
        tvRideId = findViewById(R.id.tvRideId);
        tvDriverName = findViewById(R.id.tvDriverName);
        tvDriverStatus = findViewById(R.id.tvDriverStatus);
        tvFromLocation = findViewById(R.id.tvFromLocation);
        tvToLocation = findViewById(R.id.tvToLocation);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvAvailableSeats = findViewById(R.id.tvAvailableSeats);
        tvFare = findViewById(R.id.tvFare);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        tvVehicleNumber = findViewById(R.id.tvVehicleNumber);
        tvDistance = findViewById(R.id.tvDistance);
        tvStatus = findViewById(R.id.tvStatus);
        tvNotes = findViewById(R.id.tvNotes);
        tvAlreadyRequested = findViewById(R.id.tvAlreadyRequested);
        tvRequestSent = findViewById(R.id.tvRequestSent);

        // Other views
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        llContent = findViewById(R.id.llContent);
        btnBack = findViewById(R.id.btnBack);
        cardNotes = findViewById(R.id.cardNotes);
        spinnerSeats = findViewById(R.id.spinnerSeats);
        etMessage = findViewById(R.id.etMessage);
        btnRequestRide = findViewById(R.id.btnRequestRide);

        // Set ride ID in header
        tvRideId.setText(String.format("Ride #%d", rideId));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRequestRide.setOnClickListener(v -> {
            if (ride != null) {
                requestToJoinRide();
            }
        });

        // Setup spinner listener
        spinnerSeats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedSeats = position; // 1-based index
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSeats = 1;
            }
        });
    }

    private void loadRideDetails() {
        showLoading(true);

        String token = sessionManager.getToken();
        if (token == null) {
            showError("Please login again");
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RideDetailsResponse> call = apiService.getRideDetails(
                "Bearer " + token,
                rideId
        );

        call.enqueue(new Callback<RideDetailsResponse>() {
            @Override
            public void onResponse(Call<RideDetailsResponse> call, Response<RideDetailsResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ride = response.body().getRide();
                    if (ride != null) {
                        showRideDetails();
                    } else {
                        showError("Failed to load ride details");
                    }
                } else {
                    if (response.code() == 404) {
                        showError("Ride not found");
                    } else {
                        showError("Failed to load ride details. Code: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<RideDetailsResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showRideDetails() {
        llContent.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        // Set ride details
        tvTitle.setText(String.format("Ride to %s", ride.getToLocation()));

        // Driver info
        if (ride.getDriver() != null) {
            tvDriverName.setText(ride.getDriver().getName());
            tvDriverStatus.setText(ride.getDriver().getStatus());
            // Set status color
            if ("active".equalsIgnoreCase(ride.getDriver().getStatus())) {
                tvDriverStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvDriverStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }

        // Route
        tvFromLocation.setText(ride.getFromLocation());
        tvToLocation.setText(ride.getToLocation());

        // Format departure time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(ride.getDepartureTime());
            tvDepartureTime.setText(outputFormat.format(date));
        } catch (ParseException e) {
            tvDepartureTime.setText(ride.getDepartureTime());
        }

        // Ride info
        tvAvailableSeats.setText(String.format(Locale.getDefault(), "%d seats", ride.getAvailableSeats()));
        tvFare.setText(String.format("৳%s", ride.getFarePerSeat()));

        if (ride.getVehicleType() != null) {
            tvVehicleType.setText(ride.getVehicleType());
        }

        if (ride.getVehicleNumber() != null) {
            tvVehicleNumber.setText(ride.getVehicleNumber());
        }

        tvDistance.setText(String.format(Locale.getDefault(), "%.1f km", ride.getDistance()));
        tvStatus.setText(ride.getStatus());

        // Set status color
        if ("active".equalsIgnoreCase(ride.getStatus())) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnRequestRide.setEnabled(false);
            btnRequestRide.setText("Ride Not Available");
            btnRequestRide.setAlpha(0.5f);
        }

        // Notes
        if (ride.getNotes() != null && !ride.getNotes().isEmpty()) {
            tvNotes.setText(ride.getNotes());
            cardNotes.setVisibility(View.VISIBLE);
        }

        // Setup seats spinner
        setupSeatsSpinner();
    }

    private void setupSeatsSpinner() {
        int availableSeats = ride.getAvailableSeats();

        List<String> seatsOptions = new ArrayList<>();
        seatsOptions.add("Select seats");

        for (int i = 1; i <= availableSeats; i++) {
            seatsOptions.add(String.valueOf(i));
        }

        if (availableSeats == 0) {
            seatsOptions.add("No seats available");
            spinnerSeats.setEnabled(false);
            btnRequestRide.setEnabled(false);
            btnRequestRide.setText("No Seats Available");
            btnRequestRide.setAlpha(0.5f);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                seatsOptions
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeats.setAdapter(adapter);

        // Set default selection to 1 seat if available
        if (availableSeats >= 1) {
            spinnerSeats.setSelection(1); // Index 1 is "1"
        }
    }

    private void requestToJoinRide() {
        // Get selected seats from spinner
        String selectedSeatStr = (String) spinnerSeats.getSelectedItem();

        if (selectedSeatStr == null || selectedSeatStr.equals("Select seats") || selectedSeatStr.equals("No seats available")) {
            showToast("Please select number of seats");
            return;
        }

        int requestedSeats;
        try {
            requestedSeats = Integer.parseInt(selectedSeatStr);
        } catch (NumberFormatException e) {
            showToast("Invalid seat selection");
            return;
        }

        // Get message
        String message = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";

        // Validate seats
        if (requestedSeats > ride.getAvailableSeats()) {
            showToast("Not enough seats available");
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm Request")
                .setMessage(String.format("Request %d seat(s) for this ride?", requestedSeats))
                .setPositiveButton("Send Request", (dialog, which) -> {
                    sendRideRequest(requestedSeats, message);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendRideRequest(int requestedSeats, String message) {
        btnRequestRide.setEnabled(false);
        btnRequestRide.setText("Sending...");

        String token = sessionManager.getToken();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        RideRequest rideRequest = new RideRequest(requestedSeats, message);
        Call<RideRequestResponse> call = apiService.requestRide(
                "Bearer " + token,
                rideId,
                rideRequest
        );

        call.enqueue(new Callback<RideRequestResponse>() {
            @Override
            public void onResponse(Call<RideRequestResponse> call, Response<RideRequestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Request successful
                    tvRequestSent.setVisibility(View.VISIBLE);
                    btnRequestRide.setVisibility(View.GONE);
                    etMessage.setEnabled(false);
                    spinnerSeats.setEnabled(false);

                    showToast(response.body().getMessage());

                    // Refresh ride details to update available seats
                    loadRideDetails();

                } else if (response.code() == 400) {
                    // Duplicate request
                    tvAlreadyRequested.setVisibility(View.VISIBLE);
                    btnRequestRide.setVisibility(View.GONE);
                    showToast("You have already requested this ride");
                } else {
                    // Get error body to see what's wrong
                    String errorMessage = "Failed to send request. Please try again.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            // Parse error response
                            showToast("Error: " + response.code() + " - " + errorBody);
                            // You can parse the JSON if it's structured
                        } else {
                            showToast("Error: " + response.code() + " - No error body");
                        }
                    } catch (Exception e) {
                        showToast("Error: " + response.code() + " - " + e.getMessage());
                    }

                    btnRequestRide.setEnabled(true);
                    btnRequestRide.setText("Send Request");
                }
            }

            @Override
            public void onFailure(Call<RideRequestResponse> call, Throwable t) {
                showToast("Network error: " + t.getMessage());
                btnRequestRide.setEnabled(true);
                btnRequestRide.setText("Send Request");
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            tvError.setVisibility(View.GONE);
            llContent.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        llContent.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh ride details if needed
        if (ride == null) {
            loadRideDetails();
        }
    }
}