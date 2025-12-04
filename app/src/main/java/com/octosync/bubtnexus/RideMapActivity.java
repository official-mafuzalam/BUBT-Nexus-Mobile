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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.octosync.bubtnexus.models.LocationUpdateRequest;
import com.octosync.bubtnexus.models.ApiResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1003;
    private static final int LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds

    private int rideId;
    private double fromLat, fromLng, toLat, toLng;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Polyline routePolyline;

    private ImageButton btnBack;
    private Button btnStartSharing, btnStopSharing;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private SharedPrefManager sharedPrefManager;
    private boolean isSharingLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_map);

        // Get ride data from intent
        Intent intent = getIntent();
        rideId = intent.getIntExtra("ride_id", 0);
        fromLat = intent.getDoubleExtra("from_lat", 0);
        fromLng = intent.getDoubleExtra("from_lng", 0);
        toLat = intent.getDoubleExtra("to_lat", 0);
        toLng = intent.getDoubleExtra("to_lng", 0);

        if (rideId == 0) {
            Toast.makeText(this, "Invalid ride", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        sharedPrefManager = SharedPrefManager.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initializeViews();
        setupClickListeners();
        initializeMap();
        setupLocationCallback();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnStartSharing = findViewById(R.id.btnStartSharing);
        btnStopSharing = findViewById(R.id.btnStopSharing);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnStartSharing.setOnClickListener(v -> startLocationSharing());

        btnStopSharing.setOnClickListener(v -> stopLocationSharing());
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
            drawRoute();
        }
    }

    private void enableMyLocation() {
        if (googleMap != null && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void drawRoute() {
        if (fromLat == 0 || fromLng == 0 || toLat == 0 || toLng == 0) {
            return;
        }

        // Add markers
        LatLng pickup = new LatLng(fromLat, fromLng);
        LatLng dropoff = new LatLng(toLat, toLng);

        googleMap.addMarker(new MarkerOptions()
                .position(pickup)
                .title("Pickup Location"));

        googleMap.addMarker(new MarkerOptions()
                .position(dropoff)
                .title("Drop-off Location"));

        // Draw polyline
        if (routePolyline != null) {
            routePolyline.remove();
        }

        routePolyline = googleMap.addPolyline(new PolylineOptions()
                .add(pickup, dropoff)
                .width(5)
                .color(getResources().getColor(android.R.color.holo_blue_dark)));

        // Move camera to show both points
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                com.google.android.gms.maps.model.LatLngBounds.builder()
                        .include(pickup)
                        .include(dropoff)
                        .build(), 100));
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationToServer(location);
                }
            }
        };
    }

    private void startLocationSharing() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        isSharingLocation = true;
        updateSharingUI();

        // Create location request
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(LOCATION_UPDATE_INTERVAL / 2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, null);
    }

    private void stopLocationSharing() {
        isSharingLocation = false;
        updateSharingUI();

        // Remove location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updateSharingUI() {
        if (isSharingLocation) {
            btnStartSharing.setVisibility(View.GONE);
            btnStopSharing.setVisibility(View.VISIBLE);
            tvStatus.setText("Sharing location...");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            btnStartSharing.setVisibility(View.VISIBLE);
            btnStopSharing.setVisibility(View.GONE);
            tvStatus.setText("Location sharing stopped");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void updateLocationToServer(Location location) {
        String token = sharedPrefManager.getToken();
        if (token == null || rideId == 0) {
            return;
        }

        LocationUpdateRequest request = new LocationUpdateRequest(
                location.getLatitude(),
                location.getLongitude(),
                (double) location.getSpeed(),
                (double) location.getBearing(),
                (double) location.getAccuracy()
        );

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.updateLocation("Bearer " + token, rideId, request);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(RideMapActivity.this,
                            "Failed to update location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Silent fail for location updates
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                drawRoute();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop location sharing when activity is destroyed
        if (isSharingLocation) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}