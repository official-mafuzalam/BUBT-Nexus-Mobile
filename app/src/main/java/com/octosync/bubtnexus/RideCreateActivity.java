package com.octosync.bubtnexus;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.octosync.bubtnexus.models.RideCreateRequest;
import com.octosync.bubtnexus.models.RideCreateResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideCreateActivity extends AppCompatActivity {

    // UI Components
    private EditText etFromLocation, etToLocation, etTotalSeats, etFarePerSeat;
    private EditText etVehicleType, etVehicleNumber, etNotes;
    private EditText etFromLat, etFromLng, etToLat, etToLng;
    private Button btnDepartureTime;
    private ImageButton btnBack, btnSave, btnFromMap, btnToMap; // Added btnFromMap and btnToMap
    private TextView tvDepartureTime, tvError;
    private ProgressBar progressBar;

    // Session
    private SessionManager sessionManager;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private static final int REQUEST_MAP_PICKER = 102;

    // Date and Time
    private Calendar departureCalendar = Calendar.getInstance();
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_create);

        // Check if user is logged in
        sessionManager = new SessionManager(this);
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeViews();
        setupClickListeners();

        // Set default values
        setDefaultValues();

        // Request location permission for current location feature
        requestLocationPermission();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void redirectToLogin() {
        Intent intent = new Intent(RideCreateActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        // EditText fields
        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);
        etTotalSeats = findViewById(R.id.etTotalSeats);
        etFarePerSeat = findViewById(R.id.etFarePerSeat);
        etVehicleType = findViewById(R.id.etVehicleType);
        etVehicleNumber = findViewById(R.id.etVehicleNumber);
        etNotes = findViewById(R.id.etNotes);

        // Hidden coordinate fields
        etFromLat = findViewById(R.id.etFromLat);
        etFromLng = findViewById(R.id.etFromLng);
        etToLat = findViewById(R.id.etToLat);
        etToLng = findViewById(R.id.etToLng);

        // Buttons
        btnDepartureTime = findViewById(R.id.btnDepartureTime);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        btnFromMap = findViewById(R.id.btnFromMap); // New
        btnToMap = findViewById(R.id.btnToMap);     // New

        // TextViews
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvError = findViewById(R.id.tvError);

        // Progress bar
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> createRide());

        btnDepartureTime.setOnClickListener(v -> showDateTimePicker());

        // Set BUBT as default "To" location
        setDefaultBUBTLocation();

        // Add click listeners for location selection
        etFromLocation.setOnClickListener(v -> openMapPicker("from"));
        btnFromMap.setOnClickListener(v -> openMapPicker("from"));

        etToLocation.setOnClickListener(v -> openMapPicker("to"));
        btnToMap.setOnClickListener(v -> openMapPicker("to"));
    }

    private void setDefaultBUBTLocation() {
        // Set BUBT as default "To" location
        etToLocation.setText("BUBT, Dhaka");
        etToLat.setText("23.811706");
        etToLng.setText("90.357175");
    }

    private void openMapPicker(String locationType) {
        Intent intent = new Intent(RideCreateActivity.this, MapPickerActivity.class);
        intent.putExtra("location_type", locationType);
        startActivityForResult(intent, REQUEST_MAP_PICKER);
    }

    private void openMapPickerForFrom() {
        openMapPicker("from");
    }

    private void openMapPickerForTo() {
        openMapPicker("to");
    }

    private void setDefaultValues() {
        // Set default coordinates (optional)
        etFromLat.setText("23.810331");
        etFromLng.setText("90.412521");
        etToLat.setText("23.755861");
        etToLng.setText("90.389982");

        // Set default values for better UX
        etTotalSeats.setText("4");
        etFarePerSeat.setText("50.0");
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocationForAddress() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();

                            // Set the coordinates
                            etFromLat.setText(String.valueOf(currentLatitude));
                            etFromLng.setText(String.valueOf(currentLongitude));

                            // Try to get address from coordinates (you can use Geocoder here)
                            String address = "Lat: " + String.format("%.4f", currentLatitude) +
                                    ", Lng: " + String.format("%.4f", currentLongitude);
                            etFromLocation.setText("Current Location");
                            etFromLocation.setHint(address);

                            Toast.makeText(this, "Current location set", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        currentDate.add(Calendar.HOUR_OF_DAY, 1); // Default to 1 hour from now

        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    departureCalendar.set(Calendar.YEAR, year);
                    departureCalendar.set(Calendar.MONTH, month);
                    departureCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Time Picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                departureCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                departureCalendar.set(Calendar.MINUTE, minute);

                                // Update UI
                                String dateTime = dateTimeFormat.format(departureCalendar.getTime());
                                tvDepartureTime.setText(dateTime);
                                btnDepartureTime.setText("Change Date & Time");

                            }, departureCalendar.get(Calendar.HOUR_OF_DAY),
                            departureCalendar.get(Calendar.MINUTE), false);

                    timePickerDialog.show();

                }, departureCalendar.get(Calendar.YEAR),
                departureCalendar.get(Calendar.MONTH),
                departureCalendar.get(Calendar.DAY_OF_MONTH));

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void createRide() {
        // Validate form
        if (!validateForm()) {
            return;
        }

        showLoading(true);

        String token = sessionManager.getToken();
        if (token == null) {
            showError("Please login again");
            redirectToLogin();
            return;
        }

        try {
            // Prepare request
            RideCreateRequest request = new RideCreateRequest(
                    etFromLocation.getText().toString().trim(),
                    etToLocation.getText().toString().trim(),
                    Double.parseDouble(etFromLat.getText().toString()),
                    Double.parseDouble(etFromLng.getText().toString()),
                    Double.parseDouble(etToLat.getText().toString()),
                    Double.parseDouble(etToLng.getText().toString()),
                    Integer.parseInt(etTotalSeats.getText().toString()),
                    Double.parseDouble(etFarePerSeat.getText().toString()),
                    tvDepartureTime.getText().toString(),
                    etNotes.getText().toString().trim(),
                    etVehicleType.getText().toString().trim(),
                    etVehicleNumber.getText().toString().trim()
            );

            // Make API call
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<RideCreateResponse> call = apiService.createRide("Bearer " + token, request);

            call.enqueue(new Callback<RideCreateResponse>() {
                @Override
                public void onResponse(Call<RideCreateResponse> call, Response<RideCreateResponse> response) {
                    showLoading(false);

                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus().equals("success")) {
                            // Ride created successfully
                            Toast.makeText(RideCreateActivity.this,
                                    "Ride created successfully!", Toast.LENGTH_SHORT).show();

                            // Set result and finish
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showError(response.body().getMessage());
                        }
                    } else {
                        // Handle error response
                        if (response.code() == 422) {
                            showError("Validation failed. Please check your inputs.");
                        } else if (response.code() == 401) {
                            showError("Session expired. Please login again.");
                            redirectToLogin();
                        } else {
                            showError("Failed to create ride. Error: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<RideCreateResponse> call, Throwable t) {
                    showLoading(false);
                    showError("Network error: " + t.getMessage());
                }
            });
        } catch (NumberFormatException e) {
            showLoading(false);
            showError("Invalid number format. Please check your inputs.");
        } catch (Exception e) {
            showLoading(false);
            showError("Error: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        // Reset error
        tvError.setVisibility(View.GONE);

        // Check required fields
        if (TextUtils.isEmpty(etFromLocation.getText())) {
            showError("From location is required");
            return false;
        }

        if (TextUtils.isEmpty(etToLocation.getText())) {
            showError("To location is required");
            return false;
        }

        if (TextUtils.isEmpty(etTotalSeats.getText())) {
            showError("Total seats is required");
            return false;
        }

        try {
            int seats = Integer.parseInt(etTotalSeats.getText().toString());
            if (seats < 1 || seats > 10) {
                showError("Seats must be between 1 and 10");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid seats number");
            return false;
        }

        if (TextUtils.isEmpty(etFarePerSeat.getText())) {
            showError("Fare per seat is required");
            return false;
        }

        try {
            double fare = Double.parseDouble(etFarePerSeat.getText().toString());
            if (fare < 0) {
                showError("Fare cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid fare amount");
            return false;
        }

        // Check departure time
        if (tvDepartureTime.getText().toString().equals("Not selected")) {
            showError("Please select departure time");
            return false;
        }

        // Check if departure time is in the future
        Calendar now = Calendar.getInstance();
        if (departureCalendar.before(now)) {
            showError("Departure time must be in the future");
            return false;
        }

        // Check coordinates
        if (TextUtils.isEmpty(etFromLat.getText()) || TextUtils.isEmpty(etFromLng.getText()) ||
                TextUtils.isEmpty(etToLat.getText()) || TextUtils.isEmpty(etToLng.getText())) {
            showError("Location coordinates are required");
            return false;
        }

        return true;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnBack.setEnabled(!show);
        btnDepartureTime.setEnabled(!show);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Add this method to handle map picker result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP_PICKER && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String address = data.getStringExtra("address");
            String locationType = data.getStringExtra("location_type");

            if (latitude != 0 && longitude != 0) {
                if ("from".equals(locationType)) {
                    etFromLocation.setText(address);
                    etFromLat.setText(String.valueOf(latitude));
                    etFromLng.setText(String.valueOf(longitude));
                } else if ("to".equals(locationType)) {
                    etToLocation.setText(address);
                    etToLat.setText(String.valueOf(latitude));
                    etToLng.setText(String.valueOf(longitude));
                }
            }
        }
    }
}