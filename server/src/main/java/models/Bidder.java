package models;

import java.util.List;
import repositories.BidsDAO;
import repositories.impl.DaoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Bidder {
    Logger log = LoggerFactory.getLogger(Bidder.class);

    double getBalance();

    default boolean placeBid(Auction auction, double amount) {
        if (this instanceof User user && user.isBlocked()) {
            log.warn("Bidding operation terminated: User '{}' profile is currently blacklisted.", user.getFullName());
            throw new IllegalArgumentException("[Error]: Your account is blocked");
        }

        if (this instanceof User user && amount > user.getBalance()) {
            log.warn("Bidding operation rejected: User '{}' has insufficient liquidity. Balance: ${} | Attempted: ${}",
                    user.getFullName(), user.getBalance(), amount);
            throw new IllegalArgumentException("Bid cannot be greater than your balance");
        }

        if (amount <= 0) {
            log.warn("Bidding operation rejected: Bidding value cannot be zero or negative (${})", amount);
            throw new IllegalArgumentException("[Error]: Bid must be greater than zero");
        }

        auction.placeBid(this, amount);
        if (this instanceof User user) {
            log.info("Bid successfully validation passed. Bidder: '{}' -> Placed: ${} on Auction ID: {}",
                    user.getFullName(), amount, auction.getId());
        }
        return true;
    }

    default double getHighestBid(Auction auction) {
        double lastTimeBidAmount = 0;

        if (this instanceof User thisUser) {
            List<Bid> userBids = auction.getId() > 0 ? bidsDb().getByAuctionId(auction.getId()) : auction.getBids();
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
            log.debug("Evaluated historical peak bid value for User '{}' on Auction ID {}: ${}",
                    thisUser.getFullName(), auction.getId(), lastTimeBidAmount);
        }
        return lastTimeBidAmount;
    }

    private static BidsDAO bidsDb() {
        return DaoFactory.createBidsDAO();
    }
}