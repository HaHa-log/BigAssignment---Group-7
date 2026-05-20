package Branch.Common;

import Branch.Auction;

public record AuctionAlert(NotificationType type, String itemName, double currentPrice) {
    public AuctionAlert(NotificationType type, Auction auction) {
        this(type, auction.getItem().getName(), auction.getCurrentPrice());
    }
}