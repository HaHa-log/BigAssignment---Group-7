package Branch;

import Branch.Common.Price;
import Branch.Exceptions.CustomisedException;

import java.io.Serializable;

public class AutoBid implements Serializable {
    private final Auction auction;
    private final Member user;
    private Price maxBid;
    private double increment;

    public AutoBid(Auction auction, Member user, double maxBid, double increment) {
        this.auction = auction;
        this.user = user;
        this.maxBid = new Price(maxBid);
        this.increment = increment;
    }

    public void setMaxBid(double maximum) {
        if (maximum <= ((User) user).getBalance()) {
            this.maxBid = new Price(maximum);
        } else if (maxBid.getPrice() <= auction.getCurrentPrice()) {
            throw new IllegalArgumentException("Max bid must be higher than current price.");
        } else {
            throw new CustomisedException("[Error]: Maximum bid cannot exceed balance");
        }
    }

    public void setIncrement(double step) {
        if (step <= 0) {
            throw new CustomisedException("[Error]: Step must be greater than 0.");
        }
        if (step < maxBid.getPrice()) {
            this.increment = step;
        } else {
            throw new CustomisedException("[Error]: Invalid increment, increment must be lesser than maximum bid");
        }
    }

    public Auction getAuction() { return auction; }
    public Member getUser() { return user; }
    public double getMaxBid() { return maxBid.getPrice(); }
    public double getIncrement() { return increment; }
}