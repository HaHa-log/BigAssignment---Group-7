package Branch;

import Branch.Exceptions.*;
import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private final Auction auction;
    private final Member buyer;
    private final Member seller;
    private final double finalAmount;
    private final LocalDateTime paidAt;
    private LocalDateTime completedAt;
    private TransactionStatus status;
    private LocalDateTime expiryTime;

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
        this.expiryTime = LocalDateTime.now().plusMinutes(30);
    }


    public Transaction(Auction auction, Member buyer, Member seller, double finalAmount, LocalDateTime paidAt, LocalDateTime completedAt, TransactionStatus status) {
        this.auction = auction;
        this.buyer = buyer;
        this.seller = seller;
        this.finalAmount = finalAmount;
        this.paidAt = paidAt;
        this.completedAt = completedAt;
        this.status = status;
        this.expiryTime = expiryTime;
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
                auction.transitionTo(Auction.AuctionStatus.PAID);

                buyer.addItem(auction.getItem());
                seller.removeItem(auction.getItem());

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

    public void markRefunded() throws IllegalTransactionException {
        if (this.status != TransactionStatus.COMPLETED) {
            throw new IllegalTransactionException("[Error]: Cannot make a refund for incomplete transactions");
        }

        if (seller.withdrawMoney(finalAmount)) {
            try {
                buyer.depositMoney(finalAmount);

                buyer.removeItem(auction.getItem());
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

    public void setTransactionId(int id) {
        this.transactionId = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getStatus() { return status; }

    public double getFinalAmount() { return finalAmount; }

    public Member getBuyer() { return buyer; }

    public Member getSeller() { return seller; }

    public Auction getAuction() { return auction; }

    public LocalDateTime getPaidAt() { return paidAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setExpiryTime(LocalDateTime time) {
        this.expiryTime = time;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public boolean isExpired() {
        return status == TransactionStatus.PENDING && LocalDateTime.now().isAfter(expiryTime);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "auctionId=" + auction.getId() +
                ", seller=" + seller.getFullName() +
                ", item=" + auction.getItem().getName() +
                ", amount=" + finalAmount +
                ", status=" + status +
                ", paidAt=" + paidAt +
                ", completedAt=" + completedAt +
                '}';
    }
}