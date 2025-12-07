package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.octosync.bubtnexus.models.PassengerRequest;
import com.octosync.bubtnexus.models.PassengerRequestResponse;
import com.octosync.bubtnexus.models.Ride;
import com.octosync.bubtnexus.models.RideDetailsResponse;
import com.octosync.bubtnexus.models.UpdatePassengerRequestActionRequest;
import com.octosync.bubtnexus.models.UpdateRideStatusRequest;
import com.octosync.bubtnexus.models.UpdateRideStatusResponse;
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
    private TextView tvDistance, tvStatus, tvNotes;
    private ProgressBar progressBar;
    private TextView tvError;
    private LinearLayout llContent;
    private ImageButton btnBack;
    private MaterialCardView cardNotes;
    private MaterialCardView cardPassengerRequests;

    // Passenger requests views
    private LinearLayout llRequestsContainer;
    private TextView tvNoRequests;

    // Session and Data
    private SessionManager sessionManager;
    private int rideId;
    private Ride ride;
    private int currentUserId;

    // Status action views
    private MaterialCardView cardStatusActions;
    private com.google.android.material.button.MaterialButton btnStartRide;
    private com.google.android.material.button.MaterialButton btnCompleteRide;
    private com.google.android.material.button.MaterialButton btnCancelRide;
    private TextView tvStatusMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

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

        // Status action views
        cardStatusActions = findViewById(R.id.cardStatusActions);
        btnStartRide = findViewById(R.id.btnStartRide);
        btnCompleteRide = findViewById(R.id.btnCompleteRide);
        btnCancelRide = findViewById(R.id.btnCancelRide);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);

        // Other views
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        llContent = findViewById(R.id.llContent);
        btnBack = findViewById(R.id.btnBack);
        cardNotes = findViewById(R.id.cardNotes);
        cardPassengerRequests = findViewById(R.id.cardPassengerRequests);

        // Passenger requests views
        llRequestsContainer = findViewById(R.id.llRequestsContainer);
        tvNoRequests = findViewById(R.id.tvNoRequests);

        // Set ride ID in header
        tvRideId.setText(String.format("Ride #%d", rideId));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
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
                    RideDetailsResponse rideDetailsResponse = response.body();

                    // Check if response status is "success"
                    if ("success".equalsIgnoreCase(rideDetailsResponse.getStatus())) {
                        ride = rideDetailsResponse.getRide();
                        if (ride != null) {
                            updateUIWithRideData(ride);
                        } else {
                            showError("Failed to load ride details");
                        }
                    } else {
                        showError("Failed to load ride details. Status: " + rideDetailsResponse.getStatus());
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

    private void updateUIWithRideData(Ride ride) {
        runOnUiThread(() -> {
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
            updateStatusColor(ride.getStatus());

            // Notes
            if (ride.getNotes() != null && !ride.getNotes().isEmpty()) {
                tvNotes.setText(ride.getNotes());
                cardNotes.setVisibility(View.VISIBLE);
            } else {
                cardNotes.setVisibility(View.GONE);
            }

            // Setup status actions and passenger requests
            setupStatusActions(ride);
            setupPassengerRequests(ride);
        });
    }

    private void setupStatusActions(Ride ride) {
        // Show status actions only for the ride creator (driver)
        if (ride.getDriverId() != currentUserId) {
            cardStatusActions.setVisibility(View.GONE);
            return;
        }

        cardStatusActions.setVisibility(View.VISIBLE);

        // Reset all buttons
        btnStartRide.setVisibility(View.GONE);
        btnCompleteRide.setVisibility(View.GONE);
        btnCancelRide.setVisibility(View.GONE);
        tvStatusMessage.setVisibility(View.GONE);

        String currentStatus = ride.getStatus().toLowerCase();

        // Show appropriate buttons based on current status
        switch (currentStatus) {
            case "pending":
                btnStartRide.setVisibility(View.VISIBLE);
                btnCancelRide.setVisibility(View.VISIBLE);
                break;

            case "active":
                btnCompleteRide.setVisibility(View.VISIBLE);
                btnCancelRide.setVisibility(View.VISIBLE);
                break;

            case "completed":
            case "cancelled":
                tvStatusMessage.setText(String.format("This ride has been %s", currentStatus));
                tvStatusMessage.setVisibility(View.VISIBLE);
                break;

            default:
                cardStatusActions.setVisibility(View.GONE);
                return;
        }

        // Clear existing listeners before setting new ones
        btnStartRide.setOnClickListener(null);
        btnCompleteRide.setOnClickListener(null);
        btnCancelRide.setOnClickListener(null);

        // Setup click listeners
        btnStartRide.setOnClickListener(v -> {
            showConfirmStatusDialog("Start Ride",
                    "Are you sure you want to start this ride? This will change the status to ACTIVE.",
                    "active");
        });

        btnCompleteRide.setOnClickListener(v -> {
            showConfirmStatusDialog("Complete Ride",
                    "Are you sure you want to complete this ride? This will mark the ride as COMPLETED.",
                    "completed");
        });

        btnCancelRide.setOnClickListener(v -> {
            showConfirmStatusDialog("Cancel Ride",
                    "Are you sure you want to cancel this ride? This action cannot be undone.",
                    "cancelled");
        });
    }

    private void showConfirmStatusDialog(String title, String message, final String newStatus) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    updateRideStatus(newStatus);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateRideStatus(String newStatus) {
        showLoading(true);

        String token = sessionManager.getToken();
        if (token == null) {
            showToast("Please login again");
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        UpdateRideStatusRequest updateRequest = new UpdateRideStatusRequest(newStatus);
        Call<UpdateRideStatusResponse> call = apiService.updateRideStatus(
                "Bearer " + token,
                rideId,
                updateRequest
        );

        call.enqueue(new Callback<UpdateRideStatusResponse>() {
            @Override
            public void onResponse(Call<UpdateRideStatusResponse> call,
                                   Response<UpdateRideStatusResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    UpdateRideStatusResponse statusResponse = response.body();

                    // Check response status using the new isSuccess() method or directly
                    if (statusResponse.isSuccess()) { // or use: "success".equalsIgnoreCase(statusResponse.getStatus())
                        showToast(statusResponse.getMessage());

                        // Update the ride object with new data
                        if (statusResponse.getData() != null) {
                            ride = statusResponse.getData();
                            // Update UI with the new ride data
                            updateUIWithRideData(ride);

                            // Show success toast
                            showToast("Ride status updated successfully!");
                        } else {
                            // If no data in response, refresh from server
                            loadRideDetails();
                        }
                    } else {
                        showToast("Failed: " + statusResponse.getMessage());
                    }
                } else {
                    // Handle different error cases
                    if (response.code() == 403) {
                        showToast("You don't have permission to update this ride status.");
                    } else if (response.code() == 400) {
                        showToast("Invalid status update. Please try again.");
                    } else if (response.code() == 404) {
                        showToast("Ride not found.");
                    } else {
                        showToast("Failed to update ride status. Code: " + response.code());
                    }

                    // Try to get error message from response body
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            System.out.println("Error response: " + errorBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateRideStatusResponse> call, Throwable t) {
                showLoading(false);
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void updateStatusColor(String status) {
        String statusLower = status.toLowerCase();
        switch (statusLower) {
            case "active":
                tvStatus.setTextColor(getResources().getColor(R.color.success_color));
                break;
            case "completed":
                tvStatus.setTextColor(getResources().getColor(R.color.info_color));
                break;
            case "cancelled":
                tvStatus.setTextColor(getResources().getColor(R.color.error_color));
                break;
            case "pending":
                tvStatus.setTextColor(getResources().getColor(R.color.warning_color));
                break;
            default:
                tvStatus.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void setupPassengerRequests(Ride ride) {
        // Show passenger requests section only for the ride creator
        if (ride.getDriverId() != currentUserId) {
            cardPassengerRequests.setVisibility(View.GONE);
            return;
        }

        if (ride.getRequests() == null || ride.getRequests().isEmpty()) {
            tvNoRequests.setText("No passenger requests");
            tvNoRequests.setVisibility(View.VISIBLE);
            llRequestsContainer.setVisibility(View.GONE);
            cardPassengerRequests.setVisibility(View.VISIBLE);
            return;
        }

        cardPassengerRequests.setVisibility(View.VISIBLE);
        llRequestsContainer.removeAllViews();

        // Separate pending and processed requests
        List<PassengerRequest> pendingRequests = new ArrayList<>();
        List<PassengerRequest> processedRequests = new ArrayList<>();

        for (PassengerRequest request : ride.getRequests()) {
            if ("pending".equalsIgnoreCase(request.getStatus())) {
                pendingRequests.add(request);
            } else {
                processedRequests.add(request);
            }
        }

        // Show all requests
        if (pendingRequests.isEmpty() && processedRequests.isEmpty()) {
            tvNoRequests.setVisibility(View.VISIBLE);
            llRequestsContainer.setVisibility(View.GONE);
        } else {
            tvNoRequests.setVisibility(View.GONE);
            llRequestsContainer.setVisibility(View.VISIBLE);

            // Add pending requests first
            if (!pendingRequests.isEmpty()) {
                for (PassengerRequest request : pendingRequests) {
                    View requestView = createRequestView(request, true);
                    llRequestsContainer.addView(requestView);
                }
            }

            // Add processed requests
            if (!processedRequests.isEmpty()) {
                for (PassengerRequest request : processedRequests) {
                    View requestView = createRequestView(request, false);
                    llRequestsContainer.addView(requestView);
                }
            }
        }
    }

    private View createRequestView(PassengerRequest request, boolean isPending) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_passenger_request, llRequestsContainer, false);

        TextView tvPassengerName = view.findViewById(R.id.tvPassengerName);
        TextView tvRequestSeats = view.findViewById(R.id.tvRequestSeats);
        TextView tvRequestStatus = view.findViewById(R.id.tvRequestStatus);
        TextView tvRequestMessage = view.findViewById(R.id.tvRequestMessage);
        TextView tvRequestDate = view.findViewById(R.id.tvRequestDate);
        LinearLayout llActionButtons = view.findViewById(R.id.llActionButtons);
        com.google.android.material.button.MaterialButton btnAccept = view.findViewById(R.id.btnAccept);
        com.google.android.material.button.MaterialButton btnReject = view.findViewById(R.id.btnReject);

        // Set passenger info
        if (request.getPassenger() != null) {
            tvPassengerName.setText(request.getPassenger().getName());
        } else {
            tvPassengerName.setText("Unknown Passenger");
        }

        tvRequestSeats.setText(String.format(Locale.getDefault(), "%d seat%s requested",
                request.getRequestedSeats(),
                request.getRequestedSeats() > 1 ? "s" : ""));

        // Set status
        String statusText = request.getStatus().toUpperCase();
        tvRequestStatus.setText(statusText);

        // Set status background and color based on status
        int statusColor;
        int statusBackground;
        switch (request.getStatus().toLowerCase()) {
            case "accepted":
                statusColor = android.R.color.holo_green_dark;
                statusBackground = R.drawable.bg_status_accepted;
                tvRequestStatus.setTextColor(getResources().getColor(statusColor));
                tvRequestStatus.setBackgroundResource(statusBackground);
                llActionButtons.setVisibility(View.GONE);
                break;
            case "rejected":
                statusColor = android.R.color.holo_red_dark;
                statusBackground = R.drawable.bg_status_rejected;
                tvRequestStatus.setTextColor(getResources().getColor(statusColor));
                tvRequestStatus.setBackgroundResource(statusBackground);
                llActionButtons.setVisibility(View.GONE);
                break;
            default: // pending
                statusColor = android.R.color.holo_orange_dark;
                statusBackground = R.drawable.bg_status_pending;
                tvRequestStatus.setTextColor(getResources().getColor(statusColor));
                tvRequestStatus.setBackgroundResource(statusBackground);
                if (isPending) {
                    llActionButtons.setVisibility(View.VISIBLE);
                } else {
                    llActionButtons.setVisibility(View.GONE);
                }
        }

        // Set message
        if (!TextUtils.isEmpty(request.getMessage())) {
            tvRequestMessage.setText(request.getMessage());
            tvRequestMessage.setVisibility(View.VISIBLE);
        } else {
            tvRequestMessage.setVisibility(View.GONE);
        }

        // Set request date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(request.getCreatedAt());
            tvRequestDate.setText("Requested: " + outputFormat.format(date));
        } catch (ParseException e) {
            tvRequestDate.setText("Requested: " + request.getCreatedAt());
        }

        // Setup accept button (only for pending requests)
        if (isPending) {
            btnAccept.setOnClickListener(v -> {
                showConfirmDialog("Accept Request",
                        String.format("Accept %s's request for %d seat%s?",
                                request.getPassenger() != null ? request.getPassenger().getName() : "this passenger",
                                request.getRequestedSeats(),
                                request.getRequestedSeats() > 1 ? "s" : ""),
                        "accept", request.getId());
            });

            // Setup reject button (only for pending requests)
            btnReject.setOnClickListener(v -> {
                showConfirmDialog("Reject Request",
                        String.format("Reject %s's request for %d seat%s?",
                                request.getPassenger() != null ? request.getPassenger().getName() : "this passenger",
                                request.getRequestedSeats(),
                                request.getRequestedSeats() > 1 ? "s" : ""),
                        "reject", request.getId());
            });
        } else {
            llActionButtons.setVisibility(View.GONE);
        }

        return view;
    }

    private void showConfirmDialog(String title, String message, final String action, final int requestId) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    updateRequestStatus(requestId, action);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateRequestStatus(int requestId, String action) {
        showLoading(true);

        String token = sessionManager.getToken();
        if (token == null) {
            showToast("Please login again");
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Create the request with your class name
        UpdatePassengerRequestActionRequest updateRequest = new UpdatePassengerRequestActionRequest(action);
        Call<PassengerRequestResponse> call = apiService.updateRequestStatus(
                "Bearer " + token,
                rideId,
                requestId,
                updateRequest
        );

        call.enqueue(new Callback<PassengerRequestResponse>() {
            @Override
            public void onResponse(Call<PassengerRequestResponse> call,
                                   Response<PassengerRequestResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    PassengerRequestResponse passengerResponse = response.body();

                    // Check response status
                    if ("success".equalsIgnoreCase(passengerResponse.getStatus())) {
                        showToast(passengerResponse.getMessage());
                        // Refresh ride details to get updated data
                        loadRideDetails();
                    } else {
                        showToast("Failed: " + passengerResponse.getMessage());
                    }
                } else {
                    // Handle different error cases
                    if (response.code() == 404) {
                        showToast("Request not found. It may have been already processed.");
                    } else if (response.code() == 403) {
                        showToast("You don't have permission to update this request.");
                    } else if (response.code() == 400) {
                        showToast("Invalid action. Please try again.");
                    } else {
                        showToast("Failed to update request status. Code: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<PassengerRequestResponse> call, Throwable t) {
                showLoading(false);
                showToast("Network error: " + t.getMessage());
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