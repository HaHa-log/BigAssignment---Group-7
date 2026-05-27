package com.group7.dto.auction;

import java.time.LocalDateTime;

public class CreateAuctionRequest {
    private int itemId;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;

    public CreateAuctionRequest() {}

    public CreateAuctionRequest(int itemId, LocalDateTime startingTime, LocalDateTime endingTime) {
        this.itemId = itemId;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public LocalDateTime getStartingTime() { return startingTime; }
    public void setStartingTime(LocalDateTime startingTime) { this.startingTime = startingTime; }

    public LocalDateTime getEndingTime() { return endingTime; }
    public void setEndingTime(LocalDateTime endingTime) { this.endingTime = endingTime; }
}