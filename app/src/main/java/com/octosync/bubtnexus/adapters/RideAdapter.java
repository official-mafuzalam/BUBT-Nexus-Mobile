package com.octosync.bubtnexus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.octosync.bubtnexus.R;
import com.octosync.bubtnexus.models.Ride;
import com.octosync.bubtnexus.utils.SessionManager;
import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> rides;
    private OnRideClickListener listener;
    private SessionManager sessionManager;
    private int currentUserId;

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
        void onActionButtonClick(Ride ride, boolean isMyRide);
    }

    public RideAdapter(List<Ride> rides, OnRideClickListener listener, Context context) {
        this.rides = rides;
        this.listener = listener;
        this.sessionManager = new SessionManager(context);
        this.currentUserId = sessionManager.getUserId();
    }

    public void updateRides(List<Ride> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride);

        // Check if this ride is created by current user
        boolean isMyRide = ride.getDriverId() == currentUserId;

        // Set button text based on ownership
        if (isMyRide) {
            holder.btnAction.setText("Ride Details");
        } else {
            holder.btnAction.setText("Book Ride");
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRideClick(ride);
            }
        });

        holder.btnAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionButtonClick(ride, isMyRide);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides != null ? rides.size() : 0;
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromLocation, tvToLocation, tvDepartureTime, tvSeats, tvFare, tvDistance, tvDriverName;
        Button btnAction;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromLocation = itemView.findViewById(R.id.tvFromLocation);
            tvToLocation = itemView.findViewById(R.id.tvToLocation);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvFare = itemView.findViewById(R.id.tvFare);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Ride ride) {
            tvFromLocation.setText(ride.getFromLocation());
            tvToLocation.setText(ride.getToLocation());
            tvDepartureTime.setText(ride.getDepartureTime());
            tvSeats.setText(String.format("%d/%d seats", ride.getAvailableSeats(), ride.getTotalSeats()));
            tvFare.setText(String.format("৳%s per seat", ride.getFarePerSeat()));
            tvDistance.setText(String.format("%.1f km", ride.getDistance()));

            // Set driver name
            if (ride.getDriver() != null) {
                tvDriverName.setText("Driver: " + ride.getDriver().getName());
            } else {
                tvDriverName.setText("Driver: Unknown");
            }
        }
    }
}