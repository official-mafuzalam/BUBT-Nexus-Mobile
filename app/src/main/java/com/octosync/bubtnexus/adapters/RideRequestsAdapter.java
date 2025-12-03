// adapters/RideRequestsAdapter.java
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
import com.octosync.bubtnexus.models.RideRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideRequestsAdapter extends RecyclerView.Adapter<RideRequestsAdapter.ViewHolder> {
    private List<RideRequest> requests;
    private Context context;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(RideRequest request);
        void onReject(RideRequest request);
    }

    public RideRequestsAdapter(Context context, List<RideRequest> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideRequest request = requests.get(position);

        holder.tvPassengerName.setText(request.getPassengerName());
        holder.tvSeatsRequested.setText(String.format("%d seat(s) requested", request.getRequestedSeats()));

        if (request.getMessage() != null && !request.getMessage().isEmpty()) {
            holder.tvMessage.setText(request.getMessage());
            holder.tvMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        // Format request time
        if (request.getRequestedAt() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
                Date date = inputFormat.parse(request.getRequestedAt());
                holder.tvRequestTime.setText(outputFormat.format(date));
            } catch (ParseException e) {
                holder.tvRequestTime.setText(request.getRequestedAt());
            }
        }

        // Show status
        holder.tvStatus.setText(request.getStatus().toUpperCase());

        // Set button visibility based on status
        if ("pending".equals(request.getStatus())) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(request);
            }
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReject(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests != null ? requests.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPassengerName, tvSeatsRequested, tvMessage, tvRequestTime, tvStatus;
        Button btnAccept, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPassengerName = itemView.findViewById(R.id.tvPassengerName);
            tvSeatsRequested = itemView.findViewById(R.id.tvSeatsRequested);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvRequestTime = itemView.findViewById(R.id.tvRequestTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}