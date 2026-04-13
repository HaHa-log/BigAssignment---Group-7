package Branch;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;

public class Auction implements Serializable {
    private int auctionId;
    private String owner;
    private LocalDateTime createdAt;
    private LocalDateTime terminateAt;
    private boolean isInCountDown;
    private AuctionStatus status;
    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }
    private double currentPrice;
    private String winner;
    private final ReentrantLock lock = new ReentrantLock(); //Để xử lý concurrency

    public Auction() {
        this.status = AuctionStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public String start() {
        transitionTo(AuctionStatus.RUNNING);
        return "Starting an auction...";
    }

    private boolean isValidTransition(AuctionStatus next) {
        return switch (this.status) {
            case OPEN ->
                    next == AuctionStatus.RUNNING || next == AuctionStatus.CANCELED;

            case RUNNING ->
                    next == AuctionStatus.FINISHED || next == AuctionStatus.CANCELED;

            case FINISHED ->
                    next == AuctionStatus.PAID || next == AuctionStatus.CANCELED;

            case PAID, CANCELED ->
                    false;
        };
    }


    public void transitionTo(AuctionStatus nextStatus) {
        lock.lock();
        try {
            if (isValidTransition(nextStatus)) {
                System.out.println("[Auction] Status changing from: " + status + " to " + nextStatus);
                this.status = nextStatus;
            } else {
                System.out.println("[Auction] Cannot change from " + status + " to " + nextStatus);
            }
        } finally {
            lock.unlock();
        }
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public String notifyAllBidders(String bidderName, double bidderAmount) {
        return "[System] " + "Bidder: " + bidderName + " has the highest bid of " + bidderAmount;
    }

    public void placeBid(String bidderName, double bidAmount) {
        lock.lock();
        try {
            if (status != AuctionStatus.RUNNING) {
                System.out.println("The auction hasn't started or has already ended");
                return;
            }

            if (bidAmount > currentPrice) {
                currentPrice = bidAmount;
                winner = bidderName;

                notifyAllBidders(bidderName, bidAmount);
            } else {
                System.out.println("Bid price has to be greater than the current price");
            }
        } finally {
            lock.unlock();
        }
    }

    public void setStartingPrice(double startingPrice) {
        this.currentPrice = startingPrice;
    }
}