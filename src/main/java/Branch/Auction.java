package Branch;

import Branch.Common.Price;
import Branch.Exceptions.AuctionClosedException;
import Branch.Exceptions.AuthenticationException;
import Branch.Exceptions.InvalidBidException;
import model.AuctionsDAO;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;

public class Auction extends Entity implements Serializable {
    private int auctionId;
    private Member owner;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private Item item;
    private AuctionStatus status;
    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }
    private double startingPrice;
    private volatile double currentPrice;
    private boolean isInCountDown;
    private Bidder winner;
    private final transient List<AuctionObserver> observers = new ArrayList<>();
    private final transient List<User> participants = new ArrayList<>();
    private int extendCount = 0;
    private static final int MAX_EXTENDS = 5;

    private List<User> participants;
    private List<Bid> bids;

    private final transient ReentrantLock lock = new ReentrantLock();

    AuctionsDAO auctionsDb = DaoFactory.createAuctionsDAO();

    public Auction(Member owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        auctionId = 0;
        this.owner = owner;
        this.item = item;
        this.status = AuctionStatus.OPEN;
        this.startingPrice = item.getStartingPrice();
        this.currentPrice = startingPrice;
        this.isInCountDown = false;
    }

    public Auction(Member owner, Item item, AuctionStatus status, LocalDateTime startingTime, LocalDateTime endingTime,
                   double startingPrice, double currentPrice, Bidder winner) {
        this.owner = owner;
        this.item = item;
        this.status = status;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.status = status;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.winner = winner;
    }

    public Auction(Member owner, Item item, AuctionStatus status, LocalDateTime startingTime, LocalDateTime endingTime) {
        this.owner = owner;
        this.item = item;
        this.status = status;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public AuctionStatus getStatus() {
        lock.lock();
        try {
            LocalDateTime now = LocalDateTime.now();

            if (this.status == AuctionStatus.OPEN && now.isAfter(startingTime)) {
                this.start();
            }

            if (this.status == AuctionStatus.RUNNING && now.isAfter(endingTime)) {
                this.transitionTo(AuctionStatus.FINISHED);
            }

            return status;
        } finally {
            lock.unlock();
        }
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
                    this.endingTime = LocalDateTime.now();
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
        String name = "Unknown";
        if (bidder instanceof User) {
            name = ((User) bidder).getFullName();
        }
        System.out.println("[Announcement]: " + name + " has the highest bid of " + bidderAmount);
    }

    public boolean placeBid(Bidder bidder, double amount)
            throws AuctionClosedException, AuthenticationException, InvalidBidException {
        lock.lock();
        try {
            Price bidAmount = new Price(amount);
            if (status != AuctionStatus.RUNNING) {
                throw new AuctionClosedException(status.toString());
            }

            if (bidder instanceof User && ((User) bidder).getId() == owner.getId()) {
                throw new AuthenticationException("[Error]: Sellers are prohibited from bidding on their own listings!");
            }

            if (bidAmount.getPrice() > currentPrice) {
                currentPrice = bidAmount.getPrice();
                winner = bidder;

                handleSniping();
                auctionsDb.update(this);

                Bid bid = new Bid(this, (Member) bidder, bidAmount, LocalDateTime.now());
                bids.add(bid);
                notifyAllBidders(bidder, bidAmount.getPrice());
                return true;
            } else {
                throw new InvalidBidException(currentPrice, bidAmount.getPrice());
            }
        } finally {
            lock.unlock();
        }
    }

    private void handleSniping() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(endingTime) && now.isAfter(endingTime.minusMinutes(5))) {
            if (extendCount < MAX_EXTENDS) {
                this.endingTime = now.plusMinutes(5);
                this.extendCount++;
                this.isInCountDown = false;
                System.out.println("[System]: Auction extended (" + extendCount + "/" + MAX_EXTENDS + "). New end time: " + endingTime);
            } else {
                this.isInCountDown = true;
                System.out.println("[System]: Max extensions reached! Final countdown active.");
            }
        }
    }


    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
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

    public void setCurrentPrice(Double currentPrice) {
        lock.lock();
        try {
            this.currentPrice = currentPrice;
        }
        finally {
            lock.unlock(); }
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

    public double getStartingPrice() {
        return startingPrice;
    }

    public Member getOwner() {
        return owner;
    }

    public int getOwnerId() {
        return owner.getId();
    }

    public LocalDateTime getEndingTime() {
        return endingTime;
    }

    public LocalDateTime getStartingTime() {
        return startingTime;
    }

    public Item getItem() {
        return item;
    }

    public int getItemId() {
        return item.getId();
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public User getWinner() {
        return (User) winner;
    }
}