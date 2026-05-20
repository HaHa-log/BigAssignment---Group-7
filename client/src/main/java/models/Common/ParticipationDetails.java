package models.Common;

import models.Auction;
import models.Item;
import models.User;

import java.time.LocalDateTime;

public class ParticipationDetails {
    private final int idForDetails;
    private final Auction.AuctionStatus overallStatus;
    private final Item itemSold;
    private final Price initialPrice;
    private final Price finalPrice;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final User leadingBidder;

    public ParticipationDetails(Auction auction, User accountUser) {
        this.idForDetails = auction.getId();
        this.overallStatus = auction.getStatus();
        this.itemSold = auction.getItem();
        this.initialPrice = new Price(auction.getItem().getStartingPrice());
        this.finalPrice = new Price(auction.getCurrentPrice());
        this.startTime = auction.getStartingTime();
        this.endTime = auction.getEndingTime();
        this.leadingBidder = auction.getWinner();
    }

    public boolean isUserWinning(User currentUser) {
        return leadingBidder != null &&
                ((User) leadingBidder).getId() == currentUser.getId();
    }

    public int getIdForDetails() { return idForDetails; }

    public String getItemSold() { return itemSold.getName(); }

    public double getInitialPrice() { return initialPrice.getPrice(); }

    public double getFinalPrice() { return finalPrice.getPrice(); }

    public LocalDateTime getStartTime() { return startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public User getLeadingBidder() { return leadingBidder; }

    public Auction.AuctionStatus getOverallStatus() { return overallStatus; }
}
