package com.group7.dto.item;

public class ItemRequest {
    private String name;
    private double startingPrice;
    private String description;
    private String imagePath;
    private Long ownerId; // Ties the new item to the Member logged into the client

    public ItemRequest() {}

    public ItemRequest(String name, double startingPrice, String description, String imagePath, Long ownerId) {
        this.name = name;
        this.startingPrice = startingPrice;
        this.description = description;
        this.imagePath = imagePath;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}