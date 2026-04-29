package Branch;

import Branch.Common.Price;
//Coder's note: Đây là thông tin của riêng Item

public class Item extends Entity {
    private Price startingPrice;
    private String description;
    public enum Status {
        AVAILABLE,
        IN_AUCTION,
        SOLD
    }
    private Status status = Status.AVAILABLE;

    public Item(String name, Price startingPrice, String description) {
        this(0, name, startingPrice, description, Status.AVAILABLE);
    }

    public Item(int id, String name, Price startingPrice, String description, Status status) {
        super(id, name);
        this.startingPrice = startingPrice;
        this.description = description;
        this.status = status;
    }

    public void updateDetails(Price newPrice, String newDescription) {
        if (status != Status.AVAILABLE) {
            throw new IllegalStateException("Cannot update item details while in auction or sold");
        }
        this.startingPrice = newPrice;
        this.description = newDescription;
    }

    //Coder's note: Sau khi bấm nút start
    //status phải mặc định chuyển sang in_auction
    public boolean isAvailable() {
        return this.status == Status.AVAILABLE;
    }
    //nếu còn hàng trong inventory thì phải là available
    public void setStatus(Status status) { this.status = status; }

    public double getStartingPrice() { return startingPrice.getPrice(); }

    public String getDescription() {
        return description;
    }

    public Status getStatus() { return status; }

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
