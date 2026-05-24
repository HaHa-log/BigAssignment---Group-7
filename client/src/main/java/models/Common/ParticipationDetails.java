package models.Common;

import models.Auction;
import models.Item;
import models.User; // ĐỒNG BỘ: Import lớp thực thể User mới thay thế Member

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
        this.idForDetails = auction != null ? auction.getAuctionId() : 0;
        this.overallStatus = auction != null ? auction.getStatus() : null;
        this.itemSold = auction != null ? auction.getItem() : null;
        this.initialPrice = (auction != null && auction.getItem() != null)
                ? new Price(auction.getItem().getStartingPrice()) : new Price(0);
        this.finalPrice = auction != null ? new Price(auction.getCurrentPrice()) : new Price(0);
        this.startTime = auction != null ? auction.getStartingTime() : null;
        this.endTime = auction != null ? auction.getEndingTime() : null;
        this.leadingBidder = auction != null ? auction.getWinner() : null;
    }

    public boolean isUserWinning(User currentUser) {
        return leadingBidder != null && currentUser != null &&
                leadingBidder.getId() == currentUser.getId();
    }

    public int getIdForDetails() { return idForDetails; }

    public String getItemSold() { return itemSold != null ? itemSold.getName() : "Unknown"; }

    public double getInitialPrice() { return initialPrice.getPrice(); }

    public double getFinalPrice() { return finalPrice.getPrice(); }

    public LocalDateTime getStartTime() { return startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public User getLeadingBidder() { return leadingBidder; }

    public Auction.AuctionStatus getOverallStatus() { return overallStatus; }
}
