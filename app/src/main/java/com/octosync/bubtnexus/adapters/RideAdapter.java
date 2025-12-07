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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> rides;
    private OnRideClickListener listener;
    private SessionManager sessionManager;
    private int currentUserId;

    // Date formatters
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

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

    class RideViewHolder extends RecyclerView.ViewHolder {
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

            // Format departure time
            String formattedTime = formatDepartureTime(ride.getDepartureTime());
            tvDepartureTime.setText(formattedTime);

            tvSeats.setText(String.format("%d/%d seats", ride.getAvailableSeats(), ride.getTotalSeats()));
            tvFare.setText(String.format("৳%s per seat", ride.getFarePerSeat()));
            tvDistance.setText(String.format("%.1f km away", ride.getDistance()));

            // Set driver name
            if (ride.getDriver() != null) {
                tvDriverName.setText(ride.getDriver().getName());
            } else {
                tvDriverName.setText("Driver: Unknown");
            }
        }

        private String formatDepartureTime(String departureTime) {
            if (departureTime == null || departureTime.isEmpty()) {
                return "Not specified";
            }

            try {
                // Parse the input date
                Date date = inputFormat.parse(departureTime);

                // Format date and time separately
                String dateStr = outputDateFormat.format(date);
                String timeStr = outputTimeFormat.format(date);

                // Return combined format: "DD-MM-YYYY at hh:mm a"
                return String.format("%s at %s", dateStr, timeStr);

            } catch (ParseException e) {
                // Try alternative formats if the first one fails
                try {
                    // Try without milliseconds
                    SimpleDateFormat altFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    Date date = altFormat.parse(departureTime);
                    String dateStr = outputDateFormat.format(date);
                    String timeStr = outputTimeFormat.format(date);
                    return String.format("%s at %s", dateStr, timeStr);

                } catch (ParseException e2) {
                    // Try one more format without 'Z'
                    try {
                        SimpleDateFormat altFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = altFormat2.parse(departureTime);
                        String dateStr = outputDateFormat.format(date);
                        String timeStr = outputTimeFormat.format(date);
                        return String.format("%s at %s", dateStr, timeStr);

                    } catch (ParseException e3) {
                        // If all parsing fails, return the original string
                        return departureTime;
                    }
                }
            } catch (Exception e) {
                // General exception handling
                return departureTime;
            }
        }
    }
}