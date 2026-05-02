package Branch;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;

public class Auction extends Entity implements Serializable {
    private int auctionId;
    private Member owner;
    private final LocalDateTime createdAt;
    private LocalDateTime terminatedAt;
    private boolean isInCountDown;
    private Item item;
    private AuctionStatus status;
    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }
    private double startingPrice;
    private volatile double currentPrice;
    private Bidder winner;
    private final transient ReentrantLock lock = new ReentrantLock(); //Để xử lý concurrency
    //Thêm transient vì ReentrantLock không thể serialize trực tiếp

    public Auction(Member owner, Item item) {
        this.status = AuctionStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        isInCountDown = false;
        this.owner = owner;
        this.item = item;
        this.winner = null;
        this.startingPrice = item.getStartingPrice();
        this.currentPrice = startingPrice;
    }

    public String start() {
        if (transitionTo(AuctionStatus.RUNNING)) {
            return "Starting an auction...";
        } else {
            return "Failed to start auction. Current status: " + status;
        }
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


    public boolean transitionTo(AuctionStatus nextStatus) {
        lock.lock();
        try {
            if (isValidTransition(nextStatus)) {
                System.out.println("[Auction] Status changing from: " + status + " to " + nextStatus);
                this.status = nextStatus;

                if (nextStatus == AuctionStatus.FINISHED) {
                    this.terminatedAt = LocalDateTime.now();
                }
                return true;

            } else {
                System.out.println("[Auction] Cannot change from " + status + " to " + nextStatus);
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public void notifyAllBidders(Bidder bidder, double bidderAmount) {
        String name = "Unknown Bidder";
        name = ((User) bidder).getFullName();
        System.out.println("[Announcement]: " + name + " has the highest bid of " + bidderAmount);
    }

    public synchronized boolean placeBid(Bidder bidder, double bidAmount) {
        lock.lock();
        try {
            if (status != AuctionStatus.RUNNING) {
                System.out.println("The auction hasn't started or has already ended");
                return false;
            }

            if (owner.isEqual((User) bidder)) {
                System.out.println("Auction owner cannot place bid");
                return false;
            }

            if (((User)bidder).getId()==(owner.getId())) {
                System.out.println("Auction owner cannot place bid");
                return false;
            }

            if (bidAmount > currentPrice) {
                currentPrice = bidAmount;
                winner = bidder;

                notifyAllBidders(bidder, bidAmount);
                return true;
            } else {
                System.out.println("Bid price has to be greater than the current price");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public void setAuctionId(int idOfAuction) {
        lock.lock();
        try { this.auctionId = idOfAuction; } finally { lock.unlock(); }
    }

    public void setStartingPrice(double startingPrice) {
        lock.lock();
        try {
            if (status == AuctionStatus.OPEN) {
                this.startingPrice = startingPrice;
                this.currentPrice = startingPrice;
            } else {
                System.out.println("[System]: Cannot change startingPrice when the auction is already started");
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void setOwner(Member clientOwner) {
        lock.lock();
        try {
            this.owner = clientOwner;
        }
        finally {
            lock.unlock(); }
    }

    public void setItem(Item newItem) {
        lock.lock();
        try {
            this.item = newItem;
        }
        finally {
            lock.unlock();
        }
    }

    public void setStatus(AuctionStatus status) {
        boolean check = transitionTo(status);

        if (!check) {
            System.out.println("[System]: Status transition failure from " + this.status + " to " + status);
        } else {
            System.out.println("[System]: The auction is now " + this.status);
        }
    }

    public int getId() {
        return auctionId;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public Member getSeller() {
        return owner;
    }

    public LocalDateTime getTerminatedAt() {
        return terminatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Item getItem() {
        return item;
    }

    public double getCurrentPrice() { return currentPrice; }

    public User getWinner() { return (User) winner; }
}