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

    public void saveBid(Bid bid) {
        if (bid != null && bid.getAuction() != null) {
            bid.getAuction().getBids().add(bid);
        }
    }

    public List<Bid> getBidsByAuctionId(int auctionId) {
        return AuctionManager.getInstance().getAllSessions().stream()
                .filter(auction -> auction.getId() == auctionId)
                .findFirst()
                .map(Auction::getBids)
                .orElse(List.of());
    }

    public Auction getAuction() {
        return auction;
    }

    public User getBidder() {
        return bidder;
    }

    public Price getBidPrice() {
        return bidPrice;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }
}