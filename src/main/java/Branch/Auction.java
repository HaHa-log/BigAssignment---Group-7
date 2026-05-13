package Branch;

import Branch.Common.Price;
import Branch.Exceptions.AuctionClosedException;
import Branch.Exceptions.AuthenticationException;
import Branch.Exceptions.CustomisedException;
import Branch.Exceptions.InvalidBidException;
import model.AuctionsDAO;
import model.AutoBidDAO;
import model.BidsDAO;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;

public class Auction extends Entity implements Serializable {
    private int auctionId;
    private final Member owner;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private final Item item;
    private AuctionStatus status;
    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }
    private double startingPrice;
    private volatile double currentPrice;
    private boolean isInCountDown;
    private Bidder winner;
    private transient List<AuctionObserver> observers = new ArrayList<>();
    private transient List<User> participants = new ArrayList<>();
    private int extendCount = 0;
    private static final int MAX_EXTENDS = 5;
    private List<Bid> bids = new ArrayList<>();
    private final transient model.AutoBidDAO autoBidDb = DaoFactory.createAutoBidDAO();

    private final transient ReentrantLock lock = new ReentrantLock();

    AuctionsDAO auctionsDb = DaoFactory.createAuctionsDAO();
    BidsDAO bidsDb = DaoFactory.createBidsDAO();
    UsersDAO usersDb = DaoFactory.createUsersDAO();
    AutoBidDAO autoBidDAO = DaoFactory.createAutoBidDAO();

    public List<AuctionObserver> getObservers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        return observers;
    }

    public void addObserver(AuctionObserver observer) {
        if (observer != null && !getObservers().contains(observer)) {
            getObservers().add(observer);
            System.out.println("[System]: " + ((User) observer).getFullName() + " is now viewing this auction");
        }
    }

    public Auction(Member owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        auctionId = 0;
        this.owner = owner;
        this.item = item;
        this.status = AuctionStatus.OPEN;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
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

            if (this.status == AuctionStatus.OPEN && startingTime != null && now.isAfter(startingTime)) {
                this.start();
            }

            if (this.status == AuctionStatus.RUNNING && endingTime != null && now.isAfter(endingTime)) {
                AuctionManager.getInstance().closeAuction(this);            }

            return status;
        } finally {
            lock.unlock();
        }
    }

    public AuctionStatus getRawStatus() {
        return status;
    }
    
    public String start() {
        LocalDateTime now = LocalDateTime.now();

        if (startingTime != null && now.isBefore(startingTime)) {
            this.status = AuctionStatus.OPEN;
            return "[System]: Auction will be available at " + startingTime + "";
        } else {
            transitionTo(AuctionStatus.RUNNING);
        }

        return "[System]: An auction has been started!";
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
                AuctionStatus oldStatus = this.status;
                this.status = nextStatus;
                System.out.println("[Auction] Status changing from: " + oldStatus + " to " + nextStatus);

                if (auctionsDb != null) {
                    auctionsDb.update(this);
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

    public boolean placeBid(Bidder bidder, double amount)
            throws AuctionClosedException, AuthenticationException, InvalidBidException, IllegalArgumentException {
        lock.lock();
        try {
            Bidder previousWinner = this.winner;

            if (!(bidder instanceof User user)) {
                throw new AuthenticationException("[Error]: Invalid Bidder type.");
            }

            Price bidAmount = new Price(amount);

            if (getStatus() != AuctionStatus.RUNNING) {
                throw new AuctionClosedException(this.status.toString());
            }
            if (user.isEqual(owner)) {
                throw new AuthenticationException("[Error]: Sellers cannot bid on their own listings!");
            }

            if (bidAmount.getPrice() <= currentPrice) {
                throw new InvalidBidException(currentPrice, bidAmount.getPrice());
            }

            double lastTimeBidAmount = bidder.getHighestBid(this);
            double amountToDeduct = bidAmount.getPrice() - lastTimeBidAmount;

            if (user.getBalance() < amountToDeduct) {
                throw new IllegalArgumentException("[Error]: Insufficient balance to cover the bid increase of " + amountToDeduct);
            }

            this.currentPrice = bidAmount.getPrice();
            this.winner = bidder;

            handleSniping();
            auctionsDb.update(this);

            Bid bid = new Bid(this, (Member) bidder, bidAmount);
            bidsDb.save(bid);

            for (AuctionObserver observer : getObservers()) {
                observer.onBidPlaced(this, bidder, amount);
            }

            if (previousWinner != null && ((User) previousWinner).getId() != ((User) bidder).getId()) {
                AutoBid config = AuctionManager.getInstance().getAutoBidConfig(this.auctionId, ((Member) previousWinner).getId());
                if (config != null) {
                    AuctionManager.getInstance().processAutoBids(this, config);
                }
            }

            return true;

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

    public void setStatus(AuctionStatus status) {
        boolean check = transitionTo(status);

        if (!check) {
            throw new CustomisedException("[System]: Status transition failure from " + this.status + " to " + status);
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

    public List<Bid> getBids() {
        return bidsDb.getByAuctionId(getId());
    };

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
        participants.clear();
        bids = bidsDb.getByAuctionId(auctionId);
        for (Bid bid : bids) {
            if (!participants.contains(bid.getBidder())) {
                participants.add(bid.getBidder());
            }
        }
        return participants;
    }

    public User getWinner() {
        return (User) winner;
    }
}