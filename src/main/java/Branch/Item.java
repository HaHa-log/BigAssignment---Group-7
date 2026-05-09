package Branch;

//Tạm thời sửa item từ abstract thành concrete class

import model.ItemsDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;

public class Item extends Entity {
    private double startingPrice;
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

    private ItemsDAO itemsDb = DaoFactory.createItemDAO();

    public Item(String name, double startingPrice, String description) {
        this.name = name;
        this.startingPrice = startingPrice;
        this.description = description;
        this.imagePath = null;
    }

    public Item(String name, double startingPrice, String description, Status status, LocalDateTime createdAt, LocalDateTime updatedAt, String imagePath) {
        this.name = name;
        this.startingPrice = startingPrice;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.imagePath = imagePath;
    }

    public void setStartingPrice(double startPrice) {
        this.startingPrice = startPrice;
        itemsDb.update(this);
    }

    public void setDescription(String narrative) {
        this.description = narrative;
        itemsDb.update(this);
    }

    public void setStatus(Status status) {
        this.status = status;
        itemsDb.update(this);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        itemsDb.update(this);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        itemsDb.update(this);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        itemsDb.update(this);
    }

    public double getStartingPrice() {
        return startingPrice;
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
}
