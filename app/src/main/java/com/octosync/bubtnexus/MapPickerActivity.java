package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import com.octosync.bubtnexus.models.NominatimResponse;
import com.octosync.bubtnexus.network.GeocodingClient;
import com.octosync.bubtnexus.network.GeocodingService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;
import java.util.List;

public class MapPickerActivity extends AppCompatActivity {

    // UI Components
    private MapView mapView;
    private EditText etSearch;
    private ImageButton btnBack, btnSearch;
    private Button btnSelect, btnSelectBUBT, btnUseMyLocation;
    private TextView tvSelectedAddress, tvSelectedCoordinates;
    private LinearLayout llLocationInfo;
    private ProgressBar progressBar;

    // Map Components
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    private Marker selectedMarker;
    private Marker bubtMarker;

    // Selected Location
    private double selectedLatitude = 0;
    private double selectedLongitude = 0;
    private String selectedAddress = "";

    // For coordinate formatting
    private DecimalFormat decimalFormat = new DecimalFormat("#.######");

    // Intent extras
    private String locationType = ""; // "from" or "to"

    // BUBT Coordinates
    private static final double BUBT_LATITUDE = 23.811706;
    private static final double BUBT_LONGITUDE = 90.357175;
    private static final String BUBT_ADDRESS = "BUBT, Dhaka";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OSMDroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_map_picker);

        // Get location type from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("location_type")) {
            locationType = intent.getStringExtra("location_type");
        }

        initializeViews();
        setupClickListeners();
        initializeMap();
    }

    private void initializeViews() {
        mapView = findViewById(R.id.mapView);
        etSearch = findViewById(R.id.etSearch);
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        btnSelect = findViewById(R.id.btnSelect);
        tvSelectedAddress = findViewById(R.id.tvSelectedAddress);
        tvSelectedCoordinates = findViewById(R.id.tvSelectedCoordinates);
        llLocationInfo = findViewById(R.id.llLocationInfo);
        progressBar = findViewById(R.id.progressBar);
        btnSelectBUBT = findViewById(R.id.btnSelectBUBT);
        btnUseMyLocation = findViewById(R.id.btnUseMyLocation);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSelect.setOnClickListener(v -> {
            if (selectedLatitude != 0 && selectedLongitude != 0) {
                returnSelectedLocation();
            } else {
                Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> {
            String searchQuery = etSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(searchQuery)) {
                searchAddress(searchQuery);
            } else {
                Toast.makeText(this, "Please enter an address to search", Toast.LENGTH_SHORT).show();
            }
        });

        // Quick location buttons
        btnSelectBUBT.setOnClickListener(v -> {
            GeoPoint bubtPoint = new GeoPoint(BUBT_LATITUDE, BUBT_LONGITUDE);
            handleMapTap(bubtPoint);
            mapController.setCenter(bubtPoint);
            etSearch.setText("BUBT, Dhaka");
        });

        btnUseMyLocation.setOnClickListener(v -> {
            if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                GeoPoint myLocation = myLocationOverlay.getMyLocation();
                selectedLatitude = myLocation.getLatitude();
                selectedLongitude = myLocation.getLongitude();

                // Update coordinates display
                tvSelectedCoordinates.setText(String.format("Lat: %s, Lng: %s",
                        decimalFormat.format(selectedLatitude),
                        decimalFormat.format(selectedLongitude)));

                // Add/update marker
                if (selectedMarker == null) {
                    selectedMarker = new Marker(mapView);
                    selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    selectedMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
                    mapView.getOverlays().add(selectedMarker);
                }

                selectedMarker.setPosition(myLocation);
                mapView.invalidate();

                // Show location info panel
                llLocationInfo.setVisibility(View.VISIBLE);

                // Center map on location
                mapController.setCenter(myLocation);

                // Get address from coordinates
                reverseGeocode(selectedLatitude, selectedLongitude);

                Toast.makeText(this, "Selected your current location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to get your location. Please enable GPS.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeMap() {
        // Configure map
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Set map controller
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        // Add current location overlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Add BUBT marker
        addBUBTMarker();

        // Add map tap listener
        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint point) {
                handleMapTap(point);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint point) {
                return false;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(0, mapEventsOverlay);

        // Center on BUBT by default
        GeoPoint bubtPoint = new GeoPoint(BUBT_LATITUDE, BUBT_LONGITUDE);
        mapController.setCenter(bubtPoint);
    }

    private void addBUBTMarker() {
        bubtMarker = new Marker(mapView);
        bubtMarker.setPosition(new GeoPoint(BUBT_LATITUDE, BUBT_LONGITUDE));
        bubtMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        bubtMarker.setTitle("BUBT Campus");
        bubtMarker.setSnippet("Bangladesh University of Business and Technology");
        bubtMarker.setIcon(getResources().getDrawable(R.drawable.ic_school)); // Use school icon
        mapView.getOverlays().add(bubtMarker);
    }

    private void handleMapTap(GeoPoint point) {
        selectedLatitude = point.getLatitude();
        selectedLongitude = point.getLongitude();

        // Update coordinates display
        tvSelectedCoordinates.setText(String.format("Lat: %s, Lng: %s",
                decimalFormat.format(selectedLatitude),
                decimalFormat.format(selectedLongitude)));

        // Add/update marker
        if (selectedMarker == null) {
            selectedMarker = new Marker(mapView);
            selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            selectedMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
            mapView.getOverlays().add(selectedMarker);
        }

        selectedMarker.setPosition(point);
        mapView.invalidate();

        // Show location info panel
        llLocationInfo.setVisibility(View.VISIBLE);

        // Always try to get address from coordinates using reverse geocoding
        reverseGeocode(selectedLatitude, selectedLongitude);

        // Check if near BUBT for special handling
        if (isNearBUBT(selectedLatitude, selectedLongitude)) {
            Toast.makeText(this, "Near BUBT Campus", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNearBUBT(double lat, double lng) {
        // Calculate distance between tapped point and BUBT (approx 50 meters radius)
        double distance = Math.sqrt(Math.pow(lat - BUBT_LATITUDE, 2) + Math.pow(lng - BUBT_LONGITUDE, 2));
        return distance < 0.001; // Approximately 100 meters
    }

    private void searchAddress(String query) {
        showLoading(true);

        // Handle BUBT search
        if (query.equalsIgnoreCase("bubt") || query.contains("bubt") ||
                query.contains("bangladesh university")) {
            GeoPoint bubtPoint = new GeoPoint(BUBT_LATITUDE, BUBT_LONGITUDE);
            handleMapTap(bubtPoint);
            mapController.setCenter(bubtPoint);
            selectedAddress = BUBT_ADDRESS;
            tvSelectedAddress.setText(selectedAddress);
            showLoading(false);
            return;
        }

        // Use Nominatim for forward geocoding
        GeocodingService geocodingService = GeocodingClient.getClient();
        Call<List<NominatimResponse>> call = geocodingService.forwardGeocode(
                query,
                "json",
                5 // limit results to 5
        );

        call.enqueue(new Callback<List<NominatimResponse>>() {
            @Override
            public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Get the first result
                    NominatimResponse result = response.body().get(0);

                    // Extract coordinates
                    double lat = Double.parseDouble(result.getLat());
                    double lon = Double.parseDouble(result.getLon());

                    // Create GeoPoint and select it
                    GeoPoint point = new GeoPoint(lat, lon);
                    handleMapTap(point);
                    mapController.setCenter(point);

                    // Update search field with found address
                    etSearch.setText(result.getDisplayName());

                    Toast.makeText(MapPickerActivity.this,
                            "Found location: " + result.getDisplayName(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapPickerActivity.this,
                            "No results found for: " + query,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MapPickerActivity.this,
                        "Search failed. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reverseGeocode(double latitude, double longitude) {
        showLoading(true);

        GeocodingService geocodingService = GeocodingClient.getClient();
        Call<NominatimResponse> call = geocodingService.reverseGeocode(
                latitude,
                longitude,
                "json",
                18, // zoom level
                1   // addressdetails
        );

        call.enqueue(new Callback<NominatimResponse>() {
            @Override
            public void onResponse(Call<NominatimResponse> call, Response<NominatimResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    NominatimResponse nominatimResponse = response.body();

                    // Get address from response
                    String address = nominatimResponse.getDisplayName();

                    // Alternative: Get formatted address from address object
                    if (nominatimResponse.getAddress() != null) {
                        String formattedAddress = nominatimResponse.getAddress().getFormattedAddress();
                        if (formattedAddress != null && !formattedAddress.isEmpty()) {
                            address = formattedAddress;
                        }
                    }

                    // Truncate address if too long
                    if (address.length() > 100) {
                        address = address.substring(0, 100) + "...";
                    }

                    selectedAddress = address;
                    tvSelectedAddress.setText(selectedAddress);

                } else {
                    // Fallback to coordinates if geocoding fails
                    selectedAddress = String.format("Lat: %s, Lng: %s",
                            decimalFormat.format(latitude),
                            decimalFormat.format(longitude));
                    tvSelectedAddress.setText(selectedAddress);
                    Toast.makeText(MapPickerActivity.this,
                            "Could not get address name. Using coordinates.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NominatimResponse> call, Throwable t) {
                showLoading(false);
                // Fallback to coordinates
                selectedAddress = String.format("Lat: %s, Lng: %s",
                        decimalFormat.format(latitude),
                        decimalFormat.format(longitude));
                tvSelectedAddress.setText(selectedAddress);
                Toast.makeText(MapPickerActivity.this,
                        "Network error. Using coordinates.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnSelectedLocation() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", selectedLatitude);
        resultIntent.putExtra("longitude", selectedLongitude);
        resultIntent.putExtra("address", selectedAddress);
        resultIntent.putExtra("location_type", locationType);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSelect.setEnabled(!show);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }
}