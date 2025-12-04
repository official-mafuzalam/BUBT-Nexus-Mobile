package com.octosync.bubtnexus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.octosync.bubtnexus.models.Ride;

public class JoinRideDialog extends Dialog {

    private Ride ride;
    private OnJoinClickListener listener;

    private TextView tvFromTo, tvSeatsAvailable, tvFare;
    private EditText etSeats, etMessage;
    private Button btnCancel, btnJoin;

    public interface OnJoinClickListener {
        void onJoinClick(int seats, String message);
    }

    public JoinRideDialog(@NonNull Context context, Ride ride, OnJoinClickListener listener) {
        super(context);
        this.ride = ride;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_join_ride);

        initializeViews();
        setupClickListeners();
        updateUI();
    }

    private void initializeViews() {
        tvFromTo = findViewById(R.id.tvFromTo);
        tvSeatsAvailable = findViewById(R.id.tvSeatsAvailable);
        tvFare = findViewById(R.id.tvFare);
        etSeats = findViewById(R.id.etSeats);
        etMessage = findViewById(R.id.etMessage);
        btnCancel = findViewById(R.id.btnCancel);
        btnJoin = findViewById(R.id.btnJoin);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());

        btnJoin.setOnClickListener(v -> {
            String seatsStr = etSeats.getText().toString().trim();
            if (seatsStr.isEmpty()) {
                etSeats.setError("Enter number of seats");
                return;
            }

            int seats = Integer.parseInt(seatsStr);
            if (seats < 1) {
                etSeats.setError("Enter valid number of seats");
                return;
            }

            if (seats > ride.getAvailableSeats()) {
                etSeats.setError("Only " + ride.getAvailableSeats() + " seats available");
                return;
            }

            String message = etMessage.getText().toString().trim();
            if (listener != null) {
                listener.onJoinClick(seats, message);
            }
            dismiss();
        });
    }

    private void updateUI() {
        tvFromTo.setText(ride.getFromLocation() + " → " + ride.getToLocation());
        tvSeatsAvailable.setText("Available seats: " + ride.getAvailableSeats());
        tvFare.setText("Fare: ৳" + ride.getFarePerSeat() + " per seat");

        // Set default seats to 1
        etSeats.setText("1");
    }
}