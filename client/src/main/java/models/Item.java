package models;

import models.Common.Price;

public class Item extends Entity {
    private Price startingPrice;
    private String description;
    private String name;
    public enum Status {
        AVAILABLE,
        IN_AUCTION,
    }
    private Status status;
    private String imagePath;
    private int ownerId;
    private String ownerName;
    private int auctionId;
    private Double currentAuctionPrice;

    public Item(String name, double startingPrice, String description, Status status, String imagePath) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
    }

    public Item(String name, double startingPrice, String description, Status status, String imagePath, int ownerId, String ownerName, int auctionId, double currentAuctionPrice) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.auctionId = auctionId;
        this.currentAuctionPrice = currentAuctionPrice;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public double getStartingPrice() {
        return startingPrice.getPrice();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Double getCurrentAuctionPrice() {
        return currentAuctionPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "startingPrice=" + startingPrice +
                ", name='" + getName() + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}