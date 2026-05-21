package models.dto.auction;

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

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    public String getItemImagePath() { return itemImagePath; }
    public void setItemImagePath(String itemImagePath) { this.itemImagePath = itemImagePath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public LocalDateTime getStartingTime() { return startingTime; }
    public void setStartingTime(LocalDateTime startingTime) { this.startingTime = startingTime; }
    public LocalDateTime getEndingTime() { return endingTime; }
    public void setEndingTime(LocalDateTime endingTime) { this.endingTime = endingTime; }
    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }
}
