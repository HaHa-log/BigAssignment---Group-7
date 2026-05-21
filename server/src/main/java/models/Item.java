package models;

//Tạm thời sửa item từ abstract thành concrete class

import models.Common.Price;

import java.time.LocalDateTime;
import repositories.ItemsDAO;
import repositories.impl.DaoFactory;

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
    private final ItemsDAO itemsDb = DaoFactory.createItemDAO();
    private int ownerId;

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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imagePath = imagePath;
    }

    public void setStartingPrice(double startPrice) {
        this.startingPrice = new Price(startPrice);
        updateIfPersisted();
    }

    public void setDescription(String narrative) {
        this.description = narrative;
        updateIfPersisted();
    }

    public void setStatus(Status status) {
        this.status = status;
        updateIfPersisted();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        updateIfPersisted();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        updateIfPersisted();
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        updateIfPersisted();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isAvailable() {
        return this.status == Status.AVAILABLE;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getImagePath() {
        return imagePath;
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

    public void saveItem() {
        itemsDb.save(this);
    }

    private void updateIfPersisted() {
        if (getId() > 0) {
            itemsDb.update(this);
        }
    }
}
