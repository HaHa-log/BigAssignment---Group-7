package models;

import models.Common.Price;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Bid extends Entity implements Serializable {
    private final Auction auction;
    private final Member bidder;
    private final Price bidPrice;
    private final LocalDateTime bidTime;
    //private BidsDAO bidDb = DaoFactory.createBidsDAO();

    public Bid(Auction auction, Member bidder, Price bidPrice) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = bidPrice;
        this.bidTime = LocalDateTime.now();
    }

    public Bid(Auction auction, Member bidder, double bidPrice, LocalDateTime bidTime) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = new Price(bidPrice);
        this.bidTime = bidTime;
    }

    public void saveBid(Bid bid) {
        //bidDb.save(bid);
    }

    //public List<Bid> getBidsByAuctionId(int auctionId) {
        //return bidDb.getByAuctionId(auctionId);
    //}

    public Auction getAuction() {
        return auction;
    }

    public Member getBidder() {
        return bidder;
    }

    public Price getBidPrice() {
        return bidPrice;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }
}