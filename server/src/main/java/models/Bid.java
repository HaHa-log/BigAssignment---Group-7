package models;

import models.Common.Price;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import repositories.BidsDAO;
import repositories.impl.DaoFactory;

public class Bid extends Entity implements Serializable {
    private final Auction auction;
    private final User bidder;
    private final Price bidPrice;
    private final LocalDateTime bidTime;
    private final BidsDAO bidDb = DaoFactory.createBidsDAO();

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

    public User getBidder() {
        return bidder;
    }

    public Price getBidPrice() {
        return bidPrice;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void saveBid(Bid bid) {
        bidDb.save(bid);
    }

    public List<Bid> getBidsByAuctionId(int auctionId) {
        return bidDb.getByAuctionId(auctionId);
    }
}