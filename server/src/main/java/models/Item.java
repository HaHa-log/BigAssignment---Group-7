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
    private String imagePath;
    private final ItemsDAO itemsDb = DaoFactory.createItemDAO();
    private Member owner;

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

    public void setName(String name) {
        this.name = name;
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

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
    public boolean isAvailable() {
        return this.status == Status.AVAILABLE;
    }
    public Member getOwner() {
        return owner;
    }
    public int getOwnerId() {
        return owner.getId();
    }
    public String getImagePath() {
        return imagePath;
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
