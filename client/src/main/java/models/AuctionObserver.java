package models;

public interface AuctionObserver {
    void onBidPlaced(Auction auction, Bidder bidder, double amount);
}
