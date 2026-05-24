package models;

import java.time.LocalDateTime;

public class Transaction {
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        REFUNDED
    }

    private int transactionId;
    private final Auction auction;
    private final User buyer;
    private final User seller;
    private final double finalAmount;
    private final LocalDateTime paidAt;
    private LocalDateTime completedAt;
    private TransactionStatus status;
    private LocalDateTime expiryTime;

    public Transaction(Auction auction, User buyer, User seller, double finalAmount) {
        this.auction = auction;
        this.buyer = buyer;
        this.seller = seller;
        this.finalAmount = finalAmount;
        this.paidAt = LocalDateTime.now();
        this.completedAt = null;
        this.status = TransactionStatus.PENDING;
        this.expiryTime = LocalDateTime.now().plusMinutes(30);
    }

    public Transaction(Auction auction, User buyer, User seller, double finalAmount, LocalDateTime paidAt, LocalDateTime completedAt, TransactionStatus status, LocalDateTime expiryTime) {
        this.auction = auction;
        this.buyer = buyer;
        this.seller = seller;
        this.finalAmount = finalAmount;
        this.paidAt = paidAt;
        this.completedAt = completedAt;
        this.status = status;
        this.expiryTime = expiryTime;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int id) {
        this.transactionId = id;
    }

    public Auction getAuction() {
        return auction;
    }

    public User getBuyer() {
        return buyer;
    }

    public User getSeller() {
        return seller;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime time) {
        this.expiryTime = time;
    }

    public boolean isExpired() {
        return status == TransactionStatus.PENDING && expiryTime != null && LocalDateTime.now().isAfter(expiryTime);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", auctionId=" + (auction != null ? auction.getAuctionId() : "N/A") +
                ", seller=" + (seller != null ? seller.getFullName() : "N/A") +
                ", item=" + (auction != null && auction.getItem() != null ? auction.getItem().getName() : "N/A") +
                ", amount=" + finalAmount +
                ", status=" + status +
                ", paidAt=" + paidAt +
                ", completedAt=" + completedAt +
                '}';
    }
}
