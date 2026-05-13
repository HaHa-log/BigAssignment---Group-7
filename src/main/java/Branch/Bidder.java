package Branch;

import model.BidsDAO;
import model.impl.DaoFactory;

import java.util.List;

public interface Bidder {
    double getBalance();
    BidsDAO bidsDb = DaoFactory.createBidsDAO();

    default boolean placeBid(Auction auction, double amount){
        if (this instanceof User && ((User) this).isBlocked()) {
            throw new IllegalArgumentException("[Error]: Your account is blocked");
        }

        if (amount > ((User) this).getBalance()) {
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

        User thisUser = (User) this;

        List<Bid> userBids = bidsDb.getByAuctionId(thisUser.getId());
        for (Bid existingBid : userBids) {
            if (existingBid.getBidder() == null) {
                continue;
            }
            if (existingBid.getBidder().isEqual(thisUser)) {
                if (existingBid.getBidPrice().getPrice() > lastTimeBidAmount) {
                    lastTimeBidAmount = existingBid.getBidPrice().getPrice();
                }
            }
        }
        return lastTimeBidAmount;
    }
}