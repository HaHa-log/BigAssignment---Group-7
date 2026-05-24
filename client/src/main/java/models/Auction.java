package models;

import models.Common.Price;
import models.Exceptions.AuctionClosedException;
import models.Exceptions.AuthenticationException;
import models.Exceptions.CustomisedException;
import models.Exceptions.InvalidBidException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Auction extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int MAX_EXTENDS = 5;
    private static final int SNIPE_WINDOW_MINUTES = 5;

    private int auctionId;
    private final Member owner;
    private final Item item;
    private LocalDateTime startingTime;
    private LocalDateTime endingTime;
    private AuctionStatus status;
    private double startingPrice;
    private volatile double currentPrice;
    private boolean isInCountDown;
    private Bidder winner;
    private int extendCount;
    private List<Bid> bids;
    private transient List<AuctionObserver> observers;
    private transient List<Member> participants;
    private transient ReentrantLock lock;

    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }

    public Auction(Member owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        this(owner, item, AuctionStatus.OPEN, startingTime, endingTime,
                item.getStartingPrice(), item.getStartingPrice(), null);
    }

    public Auction(Member owner, Item item, AuctionStatus status, LocalDateTime startingTime, LocalDateTime endingTime,
                   double startingPrice, double currentPrice, Bidder winner) {
        this.auctionId = 0;
        this.owner = owner;
        this.item = item;
        this.status = status;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.winner = winner;
        this.isInCountDown = false;
        this.extendCount = 0;
        this.bids = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    public Auction(Member owner, Item item, AuctionStatus status, LocalDateTime startingTime, LocalDateTime endingTime) {
        this(owner, item, status, startingTime, endingTime,
                item.getStartingPrice(), item.getStartingPrice(), null);
    }

    public List<AuctionObserver> getObservers() {
        return observers();
    }

    public void addObserver(AuctionObserver observer) {
        if (observer != null && !observers().contains(observer)) {
            observers().add(observer);
            if (observer instanceof Member member) {
                System.out.println("[System]: " + member.getFullName() + " is now viewing this auction");
            }
        }
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

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        lock().lock();
        try {
            this.bids = bids != null ? bids : new ArrayList<>();
        } finally {
            lock().unlock();
        }
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

    public boolean placeBid(Bidder bidder, double amount)
            throws AuctionClosedException, AuthenticationException, InvalidBidException, IllegalArgumentException {
        lock().lock();
        try {
            Member member = validateBidder(bidder, amount);
            Price bidAmount = new Price(amount);
            Bidder previousWinner = winner;
            double previousSelfBid = bidder.getHighestBid(this);

            replaceSelfBidHold(member, previousSelfBid, bidAmount.getPrice());
            releasePreviousWinnerHold(previousWinner, member);
            applyWinningBid(bidder, bidAmount);
            persistBid(member, bidAmount);
            notifyBidPlaced(bidder, amount);
            processPreviousWinnerAutoBid(previousWinner, bidder);
            return true;
        } finally {
            lock().unlock();
        }
    }

    public void setStartingTime(LocalDateTime startingTime) {
        this.startingTime = startingTime;
    }

    public void setEndingTime(LocalDateTime endingTime) {
        this.endingTime = endingTime;
    }

    public void setAuctionId(int idOfAuction) {
        lock().lock();
        try {
            this.auctionId = idOfAuction;
        } finally {
            lock().unlock();
        }
    }

    public void setStartingPrice(double startingPrice) {
        lock().lock();
        try {
            if (status == AuctionStatus.OPEN) {
                this.startingPrice = startingPrice;
                this.currentPrice = startingPrice;
            } else {
                System.out.println("[System]: Cannot change startingPrice when the auction is already started");
            }
        } finally {
            lock().unlock();
        }
    }

    public void setCurrentPrice(Double currentPrice) {
        lock().lock();
        try {
            this.currentPrice = currentPrice;
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

    public List<Member> getParticipants() {
        participants().clear();
        for (Bid bid : getBids()) {
            Member bidder = bid.getBidder();
            if (bidder != null && !participants().contains(bidder)) {
                participants().add(bidder);
            }
        }
        return participants();
    }

    public Member getWinner() {
        return winner instanceof Member member ? member : null;
    }

    private void refreshTimedStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (status == AuctionStatus.OPEN && startingTime != null && now.isAfter(startingTime)) {
            start();
        }

        if (status == AuctionStatus.RUNNING && endingTime != null && now.isAfter(endingTime)) {
            AuctionManager.getInstance().closeAuction(this);
        }
    }

    private boolean isValidTransition(AuctionStatus next) {
        return switch (status) {
            case OPEN -> next == AuctionStatus.RUNNING || next == AuctionStatus.CANCELED;
            case RUNNING -> next == AuctionStatus.FINISHED || next == AuctionStatus.CANCELED;
            case FINISHED -> next == AuctionStatus.PAID || next == AuctionStatus.CANCELED;
            case PAID, CANCELED -> false;
        };
    }

    private Member validateBidder(Bidder bidder, double amount)
            throws AuctionClosedException, AuthenticationException, InvalidBidException {
        if (!(bidder instanceof Member member)) {
            throw new AuthenticationException("[Error]: Invalid Bidder type.");
        }

        if (getStatus() != AuctionStatus.RUNNING) {
            throw new AuctionClosedException(status.toString());
        }

        if (member.isEqual(owner)) {
            throw new AuthenticationException("[Error]: Sellers cannot bid on their own listings!");
        }

        if (amount <= currentPrice) {
            throw new InvalidBidException(currentPrice, amount);
        }

        return member;
    }

    private void replaceSelfBidHold(Member member, double previousSelfBid, double newBidAmount)
            throws InvalidBidException {
        if (previousSelfBid > 0) {
            boolean unfrozen = member.unfreezeMoney(previousSelfBid);

            if (unfrozen) {
                System.out.println("[System]: Unfrozen old self-bid of " + previousSelfBid + " for " + member.getFullName());
            }
        }

        if (newBidAmount < 0) {
            throw new InvalidBidException(currentPrice, newBidAmount);
        }

        if (!member.freezeMoney(newBidAmount)) {
            if (previousSelfBid > 0) {
                member.freezeMoney(previousSelfBid);
            }
            throw new IllegalArgumentException("[Error]: Insufficient balance for bidding.");
        }
    }

    private void releasePreviousWinnerHold(Bidder previousWinner, Member currentBidder) {
        if (!(previousWinner instanceof Member oldMember) || oldMember.getId() == currentBidder.getId()) {
            return;
        }

        double oldBidAmount = oldMember.getHighestBid(this);
        if (oldBidAmount > 0) {
            boolean success = oldMember.unfreezeMoney(oldBidAmount);
            if (success) {
                oldMember.addTransaction("🔓 UNFREEZE | +" + oldBidAmount + " | Balance: " + String.format("%.2f", oldMember.getBalance()));
            }
            System.out.println("[System]: Unfrozen " + oldBidAmount + " for previous winner: " + oldMember.getFullName());
        }
    }

    private void applyWinningBid(Bidder bidder, Price bidAmount) {
        currentPrice = bidAmount.getPrice();
        winner = bidder;
        handleSniping();
    }

    private void persistBid(Member member, Price bidAmount) {
        bids.add(new Bid(this, member, bidAmount));
    }

    private void notifyBidPlaced(Bidder bidder, double amount) {
        for (AuctionObserver observer : observers()) {
            observer.onBidPlaced(this, bidder, amount);
        }
    }

    private void processPreviousWinnerAutoBid(Bidder previousWinner, Bidder bidder) {
        if (!(previousWinner instanceof Member oldMember)
                || !(bidder instanceof Member newMember)
                || oldMember.getId() == newMember.getId()) {
            return;
        }
    }

    private void handleSniping() {
        LocalDateTime now = LocalDateTime.now();

        if (endingTime == null || !now.isBefore(endingTime)
                || !now.isAfter(endingTime.minusMinutes(SNIPE_WINDOW_MINUTES))) {
            return;
        }

        if (extendCount < MAX_EXTENDS) {
            endingTime = now.plusMinutes(SNIPE_WINDOW_MINUTES);
            extendCount++;
            isInCountDown = false;
            System.out.println("[System]: Auction extended (" + extendCount + "/" + MAX_EXTENDS
                    + "). New end time: " + endingTime);
        } else {
            isInCountDown = true;
            System.out.println("[System]: Max extensions reached! Final countdown active.");
        }
    }

    private ReentrantLock lock() {
        if (lock == null) {
            lock = new ReentrantLock();
        }
        return lock;
    }

    private List<AuctionObserver> observers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        return observers;
    }

    private List<Member> participants() {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        return participants;
    }
}