package com.group7.dto.auction;

import java.time.LocalDateTime;

public class AuctionResponse {
    private int id;

    private int ownerId;
    private String ownerName;

    private int itemId;
    private String itemName;
    private String itemDescription;
    private String itemImagePath;

    private String status;
    private double startingPrice;
    private double currentPrice;

    private LocalDateTime startingTime;
    private LocalDateTime endingTime;

    private Integer winnerId;
    private String winnerName;

    public AuctionResponse() {
    }

    public AuctionResponse(int id,
            int ownerId,
            String ownerName,
            int itemId,
            String itemName,
            String itemDescription,
            String itemImagePath,
            String status,
            double startingPrice,
            double currentPrice,
            LocalDateTime startingTime,
            LocalDateTime endingTime,
            Integer winnerId,
            String winnerName) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemImagePath = itemImagePath;
        this.status = status;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.winnerId = winnerId;
        this.winnerName = winnerName;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemImagePath() {
        return itemImagePath;
    }

    public String getStatus() {
        return status;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }
}
