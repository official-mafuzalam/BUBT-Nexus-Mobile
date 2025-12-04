package com.octosync.bubtnexus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.octosync.bubtnexus.models.CreateRideRequest;
import com.octosync.bubtnexus.models.RideResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateRideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker pickupMarker, dropoffMarker;
    private LatLng pickupLocation, dropoffLocation;

    private EditText etFromLocation, etToLocation;
    private EditText etSeats, etFare, etNotes, etVehicleType, etVehicleNumber;
    private TextView tvPickLocationHint;
    private Button btnCreateRide;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private SharedPrefManager sharedPrefManager;
    private boolean isSelectingPickup = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        initializeViews();
        setupClickListeners();
        initializeMap();
    }

    private void initializeViews() {
        etFromLocation = findViewById(R.id.etFromLocation);
        etToLocation = findViewById(R.id.etToLocation);
        etSeats = findViewById(R.id.etSeats);
        etFare = findViewById(R.id.etFare);
        etNotes = findViewById(R.id.etNotes);
        etVehicleType = findViewById(R.id.etVehicleType);
        etVehicleNumber = findViewById(R.id.etVehicleNumber);
        tvPickLocationHint = findViewById(R.id.tvPickLocationHint);
        btnCreateRide = findViewById(R.id.btnCreateRide);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCreateRide.setOnClickListener(v -> createRide());

        // Map will handle location selection
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Request location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }

        // Set map click listener
        googleMap.setOnMapClickListener(latLng -> {
            if (isSelectingPickup) {
                setPickupLocation(latLng);
                tvPickLocationHint.setText("Now select drop-off location");
                isSelectingPickup = false;
            } else {
                setDropoffLocation(latLng);
                tvPickLocationHint.setText("Both locations selected");
                isSelectingPickup = true; // Reset for next selection
            }
        });

        // Get current location
        getCurrentLocation();
    }

    private void enableMyLocation() {
        if (googleMap != null && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    }
                });
    }

    private void setPickupLocation(LatLng latLng) {
        pickupLocation = latLng;

        // Remove existing marker
        if (pickupMarker != null) {
            pickupMarker.remove();
        }

        // Add new marker
        pickupMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Pickup Location")
                .snippet("Tap to change"));

        // Move camera
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // You can reverse geocode here to get address
        etFromLocation.setText(String.format("%.6f, %.6f", latLng.latitude, latLng.longitude));
    }

    private void setDropoffLocation(LatLng latLng) {
        dropoffLocation = latLng;

        // Remove existing marker
        if (dropoffMarker != null) {
            dropoffMarker.remove();
        }

        // Add new marker
        dropoffMarker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Drop-off Location")
                .snippet("Tap to change"));

        etToLocation.setText(String.format("%.6f, %.6f", latLng.latitude, latLng.longitude));
    }

    private void createRide() {
        // Validate inputs
        if (pickupLocation == null || dropoffLocation == null) {
            Toast.makeText(this, "Please select both pickup and drop-off locations", Toast.LENGTH_SHORT).show();
            return;
        }

        String seatsStr = etSeats.getText().toString().trim();
        String fareStr = etFare.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String vehicleType = etVehicleType.getText().toString().trim();
        String vehicleNumber = etVehicleNumber.getText().toString().trim();

        if (seatsStr.isEmpty()) {
            etSeats.setError("Enter number of seats");
            return;
        }

        if (fareStr.isEmpty()) {
            etFare.setError("Enter fare per seat");
            return;
        }

        int seats = Integer.parseInt(seatsStr);
        double fare = Double.parseDouble(fareStr);

        if (seats < 1 || seats > 10) {
            etSeats.setError("Seats must be between 1 and 10");
            return;
        }

        // Get current time + 30 minutes for departure
        long departureTimeMillis = System.currentTimeMillis() + (30 * 60 * 1000);
        String departureTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(departureTimeMillis));

        // Create ride request
        CreateRideRequest rideRequest = new CreateRideRequest();
        rideRequest.setFromLocation(etFromLocation.getText().toString());
        rideRequest.setToLocation(etToLocation.getText().toString());
        rideRequest.setFromLat(pickupLocation.latitude);
        rideRequest.setFromLng(pickupLocation.longitude);
        rideRequest.setToLat(dropoffLocation.latitude);
        rideRequest.setToLng(dropoffLocation.longitude);
        rideRequest.setTotalSeats(seats);
        rideRequest.setFarePerSeat(fare);
        rideRequest.setDepartureTime(departureTime);
        rideRequest.setNotes(notes);
        rideRequest.setVehicleType(vehicleType);
        rideRequest.setVehicleNumber(vehicleNumber);

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnCreateRide.setEnabled(false);

        // Get token
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        // Call API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RideResponse> call = apiService.createRide("Bearer " + token, rideRequest);

        call.enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnCreateRide.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreateRideActivity.this,
                            "Ride created successfully!", Toast.LENGTH_SHORT).show();

                    // Go to ride details
                    Intent intent = new Intent(CreateRideActivity.this, RideDetailsActivity.class);
                    intent.putExtra("ride_id", response.body().getData().getId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateRideActivity.this,
                            "Failed to create ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnCreateRide.setEnabled(true);
                Toast.makeText(CreateRideActivity.this,
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
                enableMyLocation();
                getCurrentLocation();
            }
        }
    }
}