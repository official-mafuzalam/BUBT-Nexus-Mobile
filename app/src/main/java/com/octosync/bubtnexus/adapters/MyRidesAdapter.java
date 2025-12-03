// adapters/MyRidesAdapter.java
package com.octosync.bubtnexus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.ViewHolder> {
    private List<Ride> rides;
    private Context context;
    private OnRideClickListener listener;
    private String type; // "driver" or "passenger"

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
    }

    public MyRidesAdapter(Context context, List<Ride> rides, String type, OnRideClickListener listener) {
        this.context = context;
        this.rides = rides;
        this.type = type;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = rides.get(position);

        holder.tvFromLocation.setText(ride.getFromLocation());
        holder.tvToLocation.setText(ride.getToLocation());

        if ("driver".equals(type)) {
            holder.tvRole.setText("Driver");
            holder.tvPassengerCount.setText(String.format("%d passenger(s)",
                    ride.getConfirmedPassengers() != null ? ride.getConfirmedPassengers().size() : 0));
        } else {
            holder.tvRole.setText("Passenger");
            holder.tvDriverName.setText(String.format("Driver: %s", ride.getDriverName()));
        }

        holder.tvStatus.setText(ride.getStatus().toUpperCase());

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

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRideClick(ride);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides != null ? rides.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFromLocation, tvToLocation, tvRole, tvStatus;
        TextView tvDepartureTime, tvDriverName, tvPassengerCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromLocation = itemView.findViewById(R.id.tvFromLocation);
            tvToLocation = itemView.findViewById(R.id.tvToLocation);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvPassengerCount = itemView.findViewById(R.id.tvPassengerCount);
        }
    }
}