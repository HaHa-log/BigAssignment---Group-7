package com.group7.dto.item;

public class ItemResponse {
    private int id;
    private String name;
    private double startingPrice;
    private String description;
    private String status; // Kept as String for easy network serialization (e.g., "AVAILABLE", "IN_AUCTION")
    private String imagePath;
    private int ownerId;

    public ItemResponse() {}

    public ItemResponse(int id, String name, double startingPrice, String description, String status, String imagePath, int ownerId) {
        this.id = id;
        this.name = name;
        this.startingPrice = startingPrice;
        this.description = description;
        this.status = status;
        this.imagePath = imagePath;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
}