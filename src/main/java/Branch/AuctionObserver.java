package Branch;

public interface AuctionObserver {
    String onBidPlaced(Auction auction, Bid bid);
}
