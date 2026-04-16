package Branch;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private Auction auction;
    private Member buyer;
    private Member seller;
    private double finalAmount;
    private LocalDateTime paidAt;
    private LocalDateTime completedAt;
    private TransactionStatus status;

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        REFUNDED
    }

    public Transaction(Auction auction, Member buyer, Member seller, double finalAmount) {
        this.auction = auction;
        this.buyer = buyer;
        this.seller = seller;
        this.finalAmount = finalAmount;
        this.paidAt = LocalDateTime.now();
        this.completedAt = null;
        this.status = TransactionStatus.PENDING;
    }

    public void markCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        System.out.println("[Transaction]: Payment completed. " + buyer.getName() + " paid " + finalAmount);
    }

    public void markRefunded() {
        this.status = TransactionStatus.REFUNDED;
        System.out.println("[Transaction]: Payment refunded for auction of ID " + auction.getAuctionId());
    }

    public TransactionStatus getStatus() { return status; }

    public double getFinalAmount() { return finalAmount; }

    public Member getBuyer() { return buyer; }

    public Member getSeller() { return seller; }

    public Auction getAuction() { return auction; }

    public LocalDateTime getPaidAt() { return paidAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }

    @Override
    public String toString() {
        return "Transaction{" +
                "auctionId=" + auction.getAuctionId() +
                ", seller=" + seller.getName() +
                ", item=" + auction.getItem().getName() +
                ", amount=" + finalAmount +
                ", status=" + status +
                ", paidAt=" + paidAt +
                ", completedAt=" + completedAt +
                '}';
    }
}