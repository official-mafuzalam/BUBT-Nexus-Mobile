// adapters/PassengersAdapter.java
package com.octosync.bubtnexus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.octosync.bubtnexus.R;
import com.octosync.bubtnexus.models.Passenger;
import java.util.List;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.ViewHolder> {
    private List<Passenger> passengers;
    private Context context;

    public PassengersAdapter(Context context, List<Passenger> passengers) {
        this.context = context;
        this.passengers = passengers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Passenger passenger = passengers.get(position);

        holder.tvPassengerName.setText(passenger.getPassengerName());
        holder.tvSeats.setText(String.format("%d seat(s)", passenger.getRequestedSeats()));
    }

    @Override
    public int getItemCount() {
        return passengers != null ? passengers.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPassengerName, tvSeats;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPassengerName = itemView.findViewById(R.id.tvPassengerName);
            tvSeats = itemView.findViewById(R.id.tvSeats);
        }
    }
}