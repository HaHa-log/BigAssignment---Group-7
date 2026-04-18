package Branch;

//Tạm thời sửa item từ abstract thành concrete class

public class Item extends Entity {
    private double startingPrice;
    private String description;
    public enum Status {
        AVAILABLE,
        IN_AUCTION,
        SOLD
    }
    private Status status = Status.AVAILABLE;

    public Item(String name, float startingPrice, String description) {
        super(name);
        this.startingPrice = startingPrice;
        this.description = description;
    }

    public void setStartingPrice(float startPrice) {
        this.startingPrice = startPrice;
    }

    public void setDescription(String narrative) {
        this.description = narrative;
    }

    public void setStatus(Status status) { this.status = status; }

    public double getStartingPrice() {
        return startingPrice;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() { return status; }

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
