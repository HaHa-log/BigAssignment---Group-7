package models.Common;

import models.Auction;
import models.Item;
import models.Member;
import java.time.LocalDateTime;

public class ParticipationDetails {
    private final int idForDetails;
    private final Auction.AuctionStatus overallStatus;
    private final Item itemSold;
    private final Price initialPrice;
    private final Price finalPrice;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Member leadingBidder;

    public ParticipationDetails(Auction auction, Member accountUser) {
        this.idForDetails = auction.getId();
        this.overallStatus = auction.getStatus();
        this.itemSold = auction.getItem();
        this.initialPrice = new Price(auction.getItem().getStartingPrice());
        this.finalPrice = new Price(auction.getCurrentPrice());
        this.startTime = auction.getStartingTime();
        this.endTime = auction.getEndingTime();
        this.leadingBidder = auction.getWinner();
    }

    public int getIdForDetails() {
        return idForDetails;
    }

    public Auction.AuctionStatus getOverallStatus() {
        return overallStatus;
    }

    public String getItemSold() {
        return itemSold.getName();
    }

    public double getInitialPrice() {
        return initialPrice.getPrice();
    }

    public double getFinalPrice() {
        return finalPrice.getPrice();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Member getLeadingBidder() {
        return leadingBidder;
    }

    public boolean isUserWinning(Member currentUser) {
        return leadingBidder != null && leadingBidder.getId() == currentUser.getId();
    }
}