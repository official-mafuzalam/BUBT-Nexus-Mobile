package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RentsResponse {
    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("data")
    private List<Rent> data;

    @SerializedName("total")
    private int total;

    @SerializedName("per_page")
    private int perPage;

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public List<Rent> getData() { return data; }
    public void setData(List<Rent> data) { this.data = data; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getPerPage() { return perPage; }
    public void setPerPage(int perPage) { this.perPage = perPage; }

}
