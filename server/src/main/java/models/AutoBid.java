package models;

import models.Common.Price;
import models.Exceptions.CustomisedException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoBid implements Serializable, Cloneable {
    private static final Logger log = LoggerFactory.getLogger(AutoBid.class);

    private Auction auction;
    private final User user;
    private Price maxBid;
    private double increment;

    public AutoBid(Auction auction, User user, double maxBid, double increment) {
        this.auction = auction;
        this.user = user;
        this.maxBid = new Price(maxBid);
        this.increment = increment;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getUser() {
        return user;
    }

    public double getMaxBid() {
        return maxBid.getPrice();
    }

    public void setMaxBid(double maximum) {
        if (maximum <= auction.getCurrentPrice()) {
            throw new IllegalArgumentException("Max bid must be higher than current price.");
        }
        if (maximum > user.getBalance()) {
            throw new CustomisedException("[Error]: Maximum bid cannot exceed balance");
        }
        this.maxBid = new Price(maximum);
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double step) {
        if (step <= 0) {
            throw new CustomisedException("[Error]: Step must be greater than 0.");
        }

        double currentPriceOfAuction = this.auction.getCurrentPrice();
        if (!BidStepConfiguration.isValidStep(currentPriceOfAuction, step)) {
            throw new CustomisedException("[Error]: Step " + step + " is invalid for the current auction price! "
                    + "Allowed steps for this range are: " + BidStepConfiguration.getAllowedSteps(currentPriceOfAuction));
        }

        if (step < maxBid.getPrice()) {
            this.increment = step;
        } else {
            throw new CustomisedException("[Error]: Invalid increment, increment must be lesser than maximum bid");
        }
    }
}