package services.dto.auction;

import java.time.LocalDateTime;

public class CreateAuctionRequest {
    private int ownerId;
    private String itemName;
    private String description;
    private double startingPrice;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private String imagePath;

    public CreateAuctionRequest(
            int ownerId,
            String itemName,
            String description,
            double startingPrice,
            LocalDateTime startingTime,
            LocalDateTime endingTime,
            String imagePath) {
        this.ownerId = ownerId;
        this.itemName = itemName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
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
