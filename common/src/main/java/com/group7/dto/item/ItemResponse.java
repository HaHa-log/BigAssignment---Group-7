package com.group7.dto.item;

public class ItemResponse {
    private int id;
    private String name;
    private double startingPrice;
    private String description;
    private String status;
    private String imagePath;
    private int ownerId;
    private String ownerName;
    private Integer activeAuctionId;
    private Double currentAuctionPrice;

    public ItemResponse() {}

    public ItemResponse(int id, String name, double startingPrice, String description, String status, String imagePath, int ownerId, String ownerName) {
        this.id = id;
        this.name = name;
        this.startingPrice = startingPrice;
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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
}