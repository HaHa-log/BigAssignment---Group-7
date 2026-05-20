package models.Common;

public class Price {
    private final double price;

    public Price(double price) {
        if (price < 0) throw new IllegalArgumentException("[Error]: Price cannot be negative!");
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%.2f", price);
    }
}
