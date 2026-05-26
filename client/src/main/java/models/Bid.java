package models;

import models.Common.Price;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Bid extends Entity implements Serializable {
    private final Auction auction;
    private final User bidder;
    private final Price bidPrice;
    private final LocalDateTime bidTime;

    public Bid(Auction auction, User bidder, Price bidPrice) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = bidPrice;
        this.bidTime = LocalDateTime.now();
    }

    public Bid(Auction auction, User bidder, double bidPrice, LocalDateTime bidTime) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = new Price(bidPrice);
        this.bidTime = bidTime;
    }

    public Auction getAuction() {
        return auction;
    }

    public Price getBidPrice() {
        return bidPrice;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }
}