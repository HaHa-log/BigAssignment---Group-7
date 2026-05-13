package Branch;

import Branch.Exceptions.CustomisedException;

import java.io.Serializable;

public class AutoBid implements Serializable {
    private final int auctionId;
    private final Member user;
    private double maxBid;
    private double increment;

    public AutoBid(int auctionId, Member user, double maxBid, double increment) {
        this.auctionId = auctionId;
        this.user = user;
        this.maxBid = maxBid;
        this.increment = increment;
    }

    public void setMaxBid(double maximum) {
        if (maximum <= ((User) user).getBalance()) {
            this.maxBid = maximum;
        } else {
            throw new CustomisedException("[Error]: Maximum bid cannot exceed balance");
        }
    }

    public void setIncrement(double step) {
        if (step <= 0) {
            throw new CustomisedException("[Error]: Step must be greater than 0.");
        }
        if (step < maxBid) {
            this.increment = step;
        } else {
            throw new CustomisedException("[Error]: Invalid increment, increment must be lesser than maximum bid");
        }
    }

    public int getAuctionId() { return auctionId; }
    public Member getUser() { return user; }
    public double getMaxBid() { return maxBid; }
    public double getIncrement() { return increment; }
}