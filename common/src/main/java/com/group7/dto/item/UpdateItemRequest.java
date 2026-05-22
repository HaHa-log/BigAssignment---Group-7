package com.group7.dto.item;

public class UpdateItemRequest {
    private String name;
    private String description;
    private Double startingPrice;
    private String status;
    private String imagePath;

    public UpdateItemRequest() {}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getStartingPrice() {
        return startingPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }
}
