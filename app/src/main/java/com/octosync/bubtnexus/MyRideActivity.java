package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.octosync.bubtnexus.adapters.MyRidesPagerAdapter;
import com.octosync.bubtnexus.models.MyRidesResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRidesActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private TextView tvError;
    private ImageButton btnBack;

    private SharedPrefManager sharedPrefManager;
    private MyRidesPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        initializeViews();
        setupClickListeners();
        loadMyRides();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadMyRides() {
        String token = sharedPrefManager.getToken();
        if (token == null) {
            redirectToLogin();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MyRidesResponse> call = apiService.getMyRides("Bearer " + token);

        call.enqueue(new Callback<MyRidesResponse>() {
            @Override
            public void onResponse(Call<MyRidesResponse> call, Response<MyRidesResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    setupViewPager(response.body());
                } else {
                    showError("Failed to load your rides");
                }
            }

            @Override
            public void onFailure(Call<MyRidesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void setupViewPager(MyRidesResponse response) {
        pagerAdapter = new MyRidesPagerAdapter(this, response.getData());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("As Driver");
            } else {
                tab.setText("As Passenger");
            }
        }).attach();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}