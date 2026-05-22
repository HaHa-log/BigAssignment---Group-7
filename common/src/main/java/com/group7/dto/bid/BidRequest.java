package com.group7.dto.bid;

public class BidRequest {
    private int bidderId;
    private double amount;

    // Default empty constructor for Jackson JSON parsing
    public BidRequest() {
    }

    // Parameterized constructor for AuctionApiService client
    public BidRequest(int bidderId, double amount) {
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public int getBidderId() {
        return bidderId;
    }

    public void setBidderId(int bidderId) {
        this.bidderId = bidderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}