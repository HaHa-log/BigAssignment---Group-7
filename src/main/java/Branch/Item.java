package Branch;

//Tạm thời sửa item từ abstract thành concrete class

public class Item extends Entity {
    private double startingPrice;
    private String description;

    public Item(int id, String name, float startingPrice, String description) {
        super(id, name);
        this.startingPrice = startingPrice;
        this.description = description;
    }

    public void setStartingPrice(float startPrice) {
        this.startingPrice = startPrice;
    }

    public void setDescription(String narrative) {
        this.description = narrative;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Item{" +
                "startingPrice=" + startingPrice +
                ", name='" + getName() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
