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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RidesAdapter extends RecyclerView.Adapter<RidesAdapter.ViewHolder> {
    private List<Ride> rides;
    private Context context;
    private OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
        void onJoinClick(Ride ride);
    }

    public RidesAdapter(Context context, List<Ride> rides, OnRideClickListener listener) {
        this.context = context;
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);

        holder.tvFromLocation.setText(ride.getFromLocation());
        holder.tvToLocation.setText(ride.getToLocation());
        holder.tvDriverName.setText(ride.getDriverName());
        holder.tvAvailableSeats.setText(String.format("Seats: %d/%d",
                ride.getAvailableSeats(), ride.getTotalSeats()));
        holder.tvFare.setText(String.format("৳%.2f per seat", ride.getFarePerSeat()));

        // Format departure time
        if (ride.getDepartureTime() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
                Date date = inputFormat.parse(ride.getDepartureTime());
                holder.tvDepartureTime.setText(outputFormat.format(date));
            } catch (ParseException e) {
                holder.tvDepartureTime.setText(ride.getDepartureTime());
            }
        }

        // Show distance if available
        if (ride.getDistance() > 0) {
            holder.tvDistance.setText(String.format("%.1f km away", ride.getDistance()));
            holder.tvDistance.setVisibility(View.VISIBLE);
        } else {
            holder.tvDistance.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRideClick(ride);
            }
        });

        holder.btnJoin.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJoinClick(ride);
            }
        });

        // Disable join button if no seats available
        holder.btnJoin.setEnabled(ride.getAvailableSeats() > 0);
    }

    @Override
    public int getItemCount() {
        return rides != null ? rides.size() : 0;
    }

    public void updateData(List<Ride> newRides) {
        rides = newRides;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromLocation, tvToLocation, tvDriverName, tvAvailableSeats;
        TextView tvFare, tvDepartureTime, tvDistance;
        Button btnJoin;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromLocation = itemView.findViewById(R.id.tvFromLocation);
            tvToLocation = itemView.findViewById(R.id.tvToLocation);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvFare = itemView.findViewById(R.id.tvFare);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            btnJoin = itemView.findViewById(R.id.btnJoin);
        }
    }
}