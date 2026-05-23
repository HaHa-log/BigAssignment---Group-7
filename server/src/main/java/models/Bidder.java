package models;

import java.util.List;
import repositories.BidsDAO;
import repositories.impl.DaoFactory;

public interface Bidder {
    double getBalance();

    default boolean placeBid(Auction auction, double amount) {
        if (this instanceof Member member && member.isBlocked()) {
            throw new IllegalArgumentException("[Error]: Your account is blocked");
        }

        if (this instanceof Member member && amount > member.getBalance()) {
            throw new IllegalArgumentException("Bid cannot be greater than your balance");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("[Error]: Bid must be greater than zero");
        }

        auction.placeBid(this, amount);
        return true;
    }

    default double getHighestBid(Auction auction) {
        double lastTimeBidAmount = 0;

        if (this instanceof Member thisMember) {
            List<Bid> userBids = auction.getId() > 0 ? bidsDb().getByAuctionId(auction.getId()) : auction.getBids();
            for (Bid existingBid : userBids) {
                if (existingBid.getBidder() == null) {
                    continue;
                }
                if (existingBid.getBidder().isEqual(thisMember)) {
                    if (existingBid.getBidPrice().getPrice() > lastTimeBidAmount) {
                        lastTimeBidAmount = existingBid.getBidPrice().getPrice();
                    }
                }
            }
        }
        return lastTimeBidAmount;
    }

    private static BidsDAO bidsDb() {
        return DaoFactory.createBidsDAO();
    }
}