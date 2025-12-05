package com.octosync.bubtnexus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.octosync.bubtnexus.R;
import com.octosync.bubtnexus.models.Ride;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> rides;
    private OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideBookClick(Ride ride);
    }

    public RideAdapter(List<Ride> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
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
    }

    @Override
    public int getItemCount() {
        return rides != null ? rides.size() : 0;
    }

    class RideViewHolder extends RecyclerView.ViewHolder {
        TextView tvDriverName, tvSeats, tvFare, tvFromLocation, tvToLocation;
        TextView tvDepartureTime, tvDistance, tvNotes;
        Button btnBookRide;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvFare = itemView.findViewById(R.id.tvFare);
            tvFromLocation = itemView.findViewById(R.id.tvFromLocation);
            tvToLocation = itemView.findViewById(R.id.tvToLocation);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            btnBookRide = itemView.findViewById(R.id.btnBookRide);
        }

        public void bind(Ride ride) {
            // Driver Info
            if (ride.getDriver() != null) {
                tvDriverName.setText(ride.getDriver().getName());
            }

            // Seats
            tvSeats.setText(String.format(Locale.getDefault(),
                    "%d/%d seats available", ride.getAvailableSeats(), ride.getTotalSeats()));

            // Fare
            tvFare.setText(String.format("৳%s", ride.getFarePerSeat()));

            // Locations
            tvFromLocation.setText(ride.getFromLocation());
            tvToLocation.setText(ride.getToLocation());

            // Departure Time
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                Date date = inputFormat.parse(ride.getDepartureTime());
                tvDepartureTime.setText(outputFormat.format(date));
            } catch (ParseException e) {
                tvDepartureTime.setText(ride.getDepartureTime());
            }

            // Distance
            tvDistance.setText(String.format(Locale.getDefault(), "%.1f km", ride.getDistance()));

            // Notes
            if (ride.getNotes() != null && !ride.getNotes().isEmpty()) {
                tvNotes.setText(String.format("Notes: %s", ride.getNotes()));
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Book Button
            btnBookRide.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRideBookClick(ride);
                }
            });

            // Disable button if no seats available
            btnBookRide.setEnabled(ride.getAvailableSeats() > 0);
            if (ride.getAvailableSeats() <= 0) {
                btnBookRide.setText("Full");
                btnBookRide.setAlpha(0.5f);
            } else {
                btnBookRide.setText("Book Ride");
                btnBookRide.setAlpha(1f);
            }
        }
    }
}