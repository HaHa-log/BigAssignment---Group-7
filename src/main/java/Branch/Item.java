package Branch;

//Tạm thời sửa item từ abstract thành concrete class

import java.time.LocalDateTime;

public class Item extends Entity {
    private double startingPrice;
    private String description;
    public enum Status {
        AVAILABLE,
        IN_AUCTION,
        SOLD
    }
    private Status status = Status.AVAILABLE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Item(String name, double startingPrice, String description) {
        super(name);
        this.startingPrice = startingPrice;
        this.description = description;
    }

    public void setStartingPrice(double startPrice) {
        this.startingPrice = startPrice;
    }

    public void setDescription(String narrative) {
        this.description = narrative;
    }

    public void setStatus(Status status) { this.status = status; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public double getStartingPrice() {
        return startingPrice;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isAvailable() {
        return this.status == Status.AVAILABLE;
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
}
