package services.dto.auction;

public class BidRequest {
    private int bidderId;
    private double amount;

    public BidRequest(int bidderId, double amount) {
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public int getBidderId() {
        return bidderId;
    }

    public double getAmount() {
        return amount;
    }
}
