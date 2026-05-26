package models;

import models.Exceptions.CustomisedException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class Auction extends Entity implements Serializable {
    private int auctionId;
    private final User owner;
    private final Item item;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private AuctionStatus status;
    private double startingPrice;
    private volatile double currentPrice;
    private User winner;
    private List<Bid> bids;
    private transient ReentrantLock lock;

    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }

    public Auction(User owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        this(owner, item, AuctionStatus.OPEN, startingTime, endingTime,
                item.getStartingPrice(), item.getStartingPrice(), null);
    }

    public Auction(User owner, Item item, AuctionStatus status, LocalDateTime startingTime, LocalDateTime endingTime) {
        this(owner, item, status, startingTime, endingTime,
                item.getStartingPrice(), item.getStartingPrice(), null);
    }

    public Auction(User owner, Item item, AuctionStatus status, LocalDateTime startingTime, LocalDateTime endingTime,
                   double startingPrice, double currentPrice, User winner) {
        this.auctionId = 0;
        this.owner = owner;
        this.item = item;
        this.status = status;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.winner = winner;
        this.bids = new ArrayList<>();
    }

    @Override
    public int getId() {
        return auctionId;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int idOfAuction) {
        lock().lock();
        try {
            this.auctionId = idOfAuction;
        } finally {
            lock().unlock();
        }
    }

    public User getOwner() {
        return owner;
    }

    public Item getItem() {
        return item;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public AuctionStatus getStatus() {
        lock().lock();
        try {
            refreshTimedStatus();
            return status;
        } finally {
            lock().unlock();
        }
    }

    public void setStatus(AuctionStatus status) {
        boolean changed = transitionTo(status);
        if (!changed) {
            throw new CustomisedException("[System]: Status transition failure from " + this.status + " to " + status);
        }
        System.out.println("[System]: The auction is now " + this.status);
    }

    public List<Bid> getBids() {
        if (bids == null) {
            bids = new ArrayList<>();
        }
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public User getWinner() {
        return winner;
    }

    public String start() {
        lock().lock();
        try {
            if (startingTime != null && LocalDateTime.now().isBefore(startingTime)) {
                status = AuctionStatus.OPEN;
                return "[System]: Auction will be available at " + startingTime;
            }
            transitionTo(AuctionStatus.RUNNING);
            return "[System]: An auction has been started!";
        } finally {
            lock().unlock();
        }
    }

    public boolean transitionTo(AuctionStatus nextStatus) {
        lock().lock();
        try {
            if (!isValidTransition(nextStatus)) {
                System.out.println("[Auction] Cannot change from " + status + " to " + nextStatus);
                return false;
            }
            AuctionStatus oldStatus = status;
            status = nextStatus;
            System.out.println("[Auction] Status changing from: " + oldStatus + " to " + nextStatus);
            return true;
        } finally {
            lock().unlock();
        }
    }

    private boolean isValidTransition(AuctionStatus nextStatus) {
        if (status == nextStatus) return true;
        return switch (status) {
            case OPEN -> nextStatus == AuctionStatus.RUNNING || nextStatus == AuctionStatus.CANCELED;
            case RUNNING -> nextStatus == AuctionStatus.FINISHED || nextStatus == AuctionStatus.CANCELED;
            case FINISHED -> nextStatus == AuctionStatus.PAID || nextStatus == AuctionStatus.CANCELED;
            case PAID, CANCELED -> false;
        };
    }

    private void refreshTimedStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (status == AuctionStatus.OPEN && startingTime != null && now.isAfter(startingTime)) {
            start();
        }
        if (status == AuctionStatus.RUNNING && endingTime != null && now.isAfter(endingTime)) {
            boolean transitioned = transitionTo(AuctionStatus.FINISHED);
            if (transitioned && winner != null) {
                double winningPrice = currentPrice;
                winner.spendFrozenMoney(winningPrice);
                if (owner != null) {
                    owner.depositMoney(winningPrice);
                }
            }
        }
    }

    private ReentrantLock lock() {
        if (lock == null) {
            lock = new ReentrantLock();
        }
        return lock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Auction auction)) return false;
        return this.getAuctionId() == auction.getAuctionId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuctionId());
    }
}
