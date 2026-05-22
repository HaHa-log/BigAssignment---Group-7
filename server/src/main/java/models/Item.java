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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imagePath;
    private int ownerId;

    public Item(String name, double startingPrice, String description) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.imagePath = null;
    }

    public Item(String name, double startingPrice, String description, Status status, String imagePath, int ownerId) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
        this.ownerId = ownerId;
    }

    public void setStartingPrice(double startPrice) {
        this.startingPrice = new Price(startPrice);
    }

    public void setDescription(String narrative) {
        this.description = narrative;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getStartingPrice() { return startingPrice.getPrice(); }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public boolean isAvailable() { return this.status == Status.AVAILABLE; }
    public int getOwnerId() { return ownerId; }
    public String getImagePath() { return imagePath; }

    @Override
    public String toString() {
        return "Item{" +
                "startingPrice=" + startingPrice +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}