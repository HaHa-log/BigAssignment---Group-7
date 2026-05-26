package models;

import models.Common.Price;

import java.time.LocalDateTime;

public class Item extends Entity {
    private Price startingPrice;
    private String description;
    private String name;
    public enum Status {
        AVAILABLE,
        IN_AUCTION,
        SOLD
    }
    private Status status = Status.AVAILABLE;
    private String imagePath;
    private int ownerId;
    private Double currentAuctionPrice;

    public Item(String name, double startingPrice, String description) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.imagePath = null;
    }

    public Item(String name, double startingPrice, String description, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String imagePath) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
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