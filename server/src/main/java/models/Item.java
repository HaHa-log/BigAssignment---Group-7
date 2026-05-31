package models;

import models.Common.Price;

public class Item extends Entity {
    public enum Status {
        AVAILABLE,
        IN_AUCTION,
        SOLD
    }

    private Price startingPrice;
    private String description;
    private String name;
    private Status status = Status.AVAILABLE;
    private String imagePath;
    private User owner;
    private Integer activeAuctionId;
    private Double currentAuctionPrice;

    public Item(String name, double startingPrice, String description) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.imagePath = null;
    }

    public Item(String name, double startingPrice, String description, Status status, String imagePath) {
        this.name = name;
        this.startingPrice = new Price(startingPrice);
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStartingPrice() {
        return startingPrice.getPrice();
    }

    public void setStartingPrice(double startPrice) {
        this.startingPrice = new Price(startPrice);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String narrative) {
        this.description = narrative;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getOwnerId() {
        return owner.getId();
    }

    public void setOwnerId(int id) {
        this.owner.setId(id);
    }

    public Integer getActiveAuctionId() {
        return activeAuctionId;
    }

    public void setActiveAuctionId(Integer activeAuctionId) {
        this.activeAuctionId = activeAuctionId;
    }

    public Double getCurrentAuctionPrice() {
        return currentAuctionPrice;
    }

    public void setCurrentAuctionPrice(Double currentAuctionPrice) {
        this.currentAuctionPrice = currentAuctionPrice;
    }

    public boolean isAvailable() {
        return this.status == Status.AVAILABLE;
    }

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