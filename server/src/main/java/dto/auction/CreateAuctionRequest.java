package dto.auction;

import java.time.LocalDateTime;

public class CreateAuctionRequest {
    private int ownerId;
    private String itemName;
    private String description;
    private double startingPrice;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private String imagePath;


    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public String getImagePath() {
        return imagePath;
    }
}
