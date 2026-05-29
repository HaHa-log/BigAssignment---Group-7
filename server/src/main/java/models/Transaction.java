package models;

import models.Exceptions.IllegalTransactionException;
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
        return status == TransactionStatus.PENDING
                && expiryTime != null
                && LocalDateTime.now().isAfter(expiryTime);
    }

    public void markCompleted() throws IllegalTransactionException {
        if (this.isExpired()) {
            throw new IllegalTransactionException("[Error]: This transaction has expired (30 mins limit reached)!");
        }

        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalTransactionException("[Error]: The transaction isn't pending");
        }

        if (buyer.spendFrozenMoney(finalAmount)) {
            try {
                seller.depositMoney(finalAmount);
                buyer.addItem(auction.getItem());
                this.status = TransactionStatus.COMPLETED;
                this.completedAt = LocalDateTime.now();
                System.out.println("[System]: Transaction completed! " + buyer.getFullName() + " has made a payment of " + finalAmount);
            } catch (Exception e) {
                buyer.unfreezeMoney(finalAmount);
                throw new IllegalTransactionException("[Error]: Failure to transfer payment to seller. Refund issued to buyer");
            }
        } else {
            throw new IllegalTransactionException("[System]: Buyer does not have enough balance to complete the payment");
        }
    }
    //Refund tiền nếu hoàn trả hàng
    public void markRefunded() throws IllegalTransactionException {
        if (this.status != TransactionStatus.COMPLETED) {
            throw new IllegalTransactionException("[Error]: Cannot make a refund for incomplete transactions");
        }

        if (seller.withdrawMoney(finalAmount)) {
            try {
                buyer.depositMoney(finalAmount);
                seller.addItem(auction.getItem());
                this.status = TransactionStatus.REFUNDED;
                System.out.println("[Transaction]: Refund successful for Auction ID " + auction.getId());
            } catch (Exception e) {
                seller.depositMoney(finalAmount);
                throw new IllegalTransactionException("[Error]: Failure to refund during transfer. Seller has been reimbursed.");
            }
        } else {
            throw new IllegalTransactionException("[Error]: Seller does not have enough balance to issue a refund");
        }
    }

    // Refund tiền nếu hết hạn pending
    public boolean markExpiredRefund() throws IllegalTransactionException {
        if (!this.isExpired()) {
            return false;
        }
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalTransactionException("[Error]: Only PENDING transactions can be expired-refunded.");
        }
        buyer.unfreezeMoney(finalAmount);
        this.status = TransactionStatus.REFUNDED;
        System.out.println("[Transaction]: Expired refund for auction " + auction.getId());

        return true;
}

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", auctionId=" + (auction != null ? auction.getId() : "N/A") +
                ", seller=" + (seller != null ? seller.getFullName() : "N/A") +
                ", item=" + (auction != null && auction.getItem() != null ? auction.getItem().getName() : "N/A") +
                ", amount=" + finalAmount +
                ", status=" + status +
                ", paidAt=" + paidAt +
                ", completedAt=" + completedAt +
                '}';
    }
}
