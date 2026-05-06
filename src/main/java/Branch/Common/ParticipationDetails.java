package Branch.Common;

import Branch.Auction;
import Branch.Bidder;
import Branch.User;
import java.time.LocalDateTime;

public class ParticipationDetails {
    private final int idForDetails;
    private final Auction.AuctionStatus overallStatus;
    private final String itemSold;
    private final double initialPrice;
    private final double finalPrice;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Bidder leadingBidder;

    public ParticipationDetails(Auction auction, User accountUser) {
        this.idForDetails = auction.getAuctionId();
        this.overallStatus = auction.getStatus();
        this.itemSold = auction.getItem().getName();
        this.initialPrice = auction.getItem().getStartingPrice();
        this.finalPrice = auction.getCurrentPrice();
        this.startTime = auction.getStartingTime();
        this.endTime = auction.getEndingTime();
        this.leadingBidder = auction.getWinner();
    }

    public boolean isUserWinning(User currentUser) {
        return leadingBidder != null &&
                ((User) leadingBidder).getId() == currentUser.getId();
    }

    public int getIdForDetails() { return idForDetails; }

    public String getItemSold() { return itemSold; }

    public double getInitialPrice() { return initialPrice; }

    public double getFinalPrice() { return finalPrice; }

    public LocalDateTime getStartTime() { return startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public Bidder getLeadingBidder() { return leadingBidder; }

    public Auction.AuctionStatus getOverallStatus() { return overallStatus; }
}
