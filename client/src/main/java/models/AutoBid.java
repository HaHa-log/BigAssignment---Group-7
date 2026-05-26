package models;

import models.Common.Price;
import models.Exceptions.CustomisedException;

import java.io.Serializable;

public class AutoBid implements Serializable, Cloneable {
    private Auction auction;
    private final User user;
    private Price maxBid;
    private double increment;

    public AutoBid(Auction auction, User user, double maxBid, double increment) {
        this.auction = auction;
        this.user = user;
        this.maxBid = new Price(0);
        setMaxBid(maxBid);
        setIncrement(increment);
    }

    @Override
    public AutoBid clone() {
        try {
            AutoBid clonedAutoBid = (AutoBid) super.clone();
            clonedAutoBid.maxBid = new Price(this.getMaxBid());
            return clonedAutoBid;
        } catch (CloneNotSupportedException e) {
            System.out.println("[Error]: Failed to clone AutoBid configuration: " + e.getMessage());
            return null;
        }
    }

    public void setMaxBid(double maximum) {
        if (auction != null && maximum <= auction.getCurrentPrice()) {
            throw new IllegalArgumentException("Max bid must be higher than current price.");
        }

        if (user != null && maximum > user.getBalance()) {
            throw new CustomisedException("[Error]: Maximum bid cannot exceed balance");
        }

        this.maxBid = new Price(maximum);
    }

    public void setIncrement(double step) {
        if (step <= 0) {
            throw new CustomisedException("[Error]: Step must be greater than 0.");
        }

        if (this.auction != null) {
            double currentPriceOfAuction = this.auction.getCurrentPrice();
            if (!BidStepConfiguration.isValidStep(currentPriceOfAuction, step)) {
                throw new CustomisedException("[Error]: Step " + step + " is invalid for the current auction price! "
                        + "Allowed steps for this range are: " + BidStepConfiguration.getAllowedSteps(currentPriceOfAuction));
            }
        }

        if (maxBid != null && step <= maxBid.getPrice()) {
            this.increment = step;
        } else {
            throw new CustomisedException("[Error]: Invalid increment, increment must be lesser than or equal to maximum bid");
        }
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Auction getAuction() { return auction; }
    public User getUser() { return user; }
    public double getMaxBid() { return maxBid.getPrice(); }
}
