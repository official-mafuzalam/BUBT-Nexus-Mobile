package com.octosync.bubtnexus.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.octosync.bubtnexus.R;
import com.octosync.bubtnexus.models.Notice;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private List<Notice> notices;

    public NoticeAdapter(List<Notice> notices) {
        this.notices = notices;
    }

    public void updateNotices(List<Notice> newNotices) {
        this.notices = newNotices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        Notice notice = notices.get(position);
        holder.bind(notice);
    }

    @Override
    public int getItemCount() {
        return notices != null ? notices.size() : 0;
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        private TextView textCategory, textTitle, textPublishedDate, textViewLink;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.textCategory);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPublishedDate = itemView.findViewById(R.id.textPublishedDate);
            textViewLink = itemView.findViewById(R.id.textViewLink);

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                openLinkInBrowser();
            });

            // Set click listener specifically for the link text
            textViewLink.setOnClickListener(v -> {
                openLinkInBrowser();
            });
        }

        private void openLinkInBrowser() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Notice notice = (Notice) itemView.getTag();
                if (notice != null && notice.getLink() != null && !notice.getLink().isEmpty()) {
                    // Open notice link in browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(notice.getLink()));
                    itemView.getContext().startActivity(browserIntent);
                }
            }
        }

        public void bind(Notice notice) {
            itemView.setTag(notice);

            textCategory.setText(notice.getCategory());
            textTitle.setText(notice.getTitle());
            textPublishedDate.setText(notice.getPublishedAt());

            // Set different background colors based on category
            int colorRes = getCategoryColor(notice.getCategory());
            textCategory.setBackgroundColor(itemView.getContext().getResources().getColor(colorRes));
        }

        private int getCategoryColor(String category) {
            if (category == null) return R.color.purple;

            switch (category.toLowerCase()) {
                case "general":
                    return R.color.green;
                case "exam related":
                    return R.color.red;
                case "class related":
                    return R.color.blue;
                case "tender":
                    return R.color.orange;
                default:
                    return R.color.purple;
            }
        }
    }
}