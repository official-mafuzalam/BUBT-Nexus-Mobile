package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

public class Notice {
    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("category")
    private String category;

    @SerializedName("published_at")
    private String publishedAt;

    @SerializedName("published_at_iso")
    private String publishedAtIso;

    // Getters
    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getCategory() { return category; }
    public String getPublishedAt() { return publishedAt; }
    public String getPublishedAtIso() { return publishedAtIso; }
}