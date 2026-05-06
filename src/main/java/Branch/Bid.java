package Branch;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Bid extends Entity implements Serializable {
    private Auction auction;
    private Member bidder;
    private double bodPrice;
    private LocalDateTime bidTime;

    public Bid(Auction auction, Member bidder, double bodPrice, LocalDateTime bidTime) {
        this.auction = auction;
        this.bidder = bidder;
        this.bodPrice = bodPrice;
        this.bidTime = LocalDateTime.now();
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public void setBidder(Member bidder) {
        this.bidder = bidder;
    }

    public void setBodPrice(double bodPrice) {
        this.bodPrice = bodPrice;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    public Auction getAuction() {
        return auction;
    }

    public Member getBidder() {
        return bidder;
    }

    public double getBodPrice() {
        return bodPrice;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }
}