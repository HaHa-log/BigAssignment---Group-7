package Branch;

import Branch.Common.Price;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;

public class Auction implements Serializable {
    private int auctionId;
    private final Member owner;
    private final LocalDateTime createdAt;
    private LocalDateTime terminatedAt;
    private final Item item;
    private AuctionStatus status;
    public enum AuctionStatus {
        OPEN, RUNNING, FINISHED, PAID, CANCELED
    }
    //Coder's note:
    //currentPrice sẽ là giá của Item được đấu giá
    //Khi khởi tạo Item, startingPrice của item có thể tùy ý chỉnh sửa
    //Trong Auction, sau khi giao bán Item, mặt khác...
    //...Sẽ không thể sửa được giá ban đầu của Item trong sàn đầu nữa
    //Khi ấy, currentPrice sẽ được cập nhận dựa trên số bid được đặt
    private volatile double currentPrice;
    private boolean isInCountDown;
    private Bidder winner;
    private static final int EXTENSION_THRESHOLD_MINUTES = 5;
    private static final int EXTENSION_MINUTES = 5;

    private final transient ReentrantLock lock = new ReentrantLock();

    public Auction(Member owner, Item item, LocalDateTime terminatedAt) {
        this.auctionId = 0;
        this.owner = owner;
        this.item = item;
        this.currentPrice = item.getStartingPrice();
        this.status = AuctionStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.terminatedAt = terminatedAt;
        this.isInCountDown = false;
    }

    //Coder's note as to not to go insane:
    //giá được cập nhận là double vì khi ấy nó đã tự truyền bid vào
    //mà bid đã được lọc trước qua Price => không cần set condition cho currentPrice
    public Auction(int auctionId, Member owner, Item item, double currentPrice,
                   AuctionStatus status, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        this.auctionId = auctionId;
        this.owner = owner;
        this.item = item;
        this.currentPrice = currentPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.terminatedAt = terminatedAt;
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

    public AuctionStatus getStatus() {
        return status;
    }

    public void notifyAllBidders(Bidder bidder, double bidderAmount) {
        String name = "Unknown";
        if (bidder instanceof User) {
            name = ((User) bidder).getName();
        }
        System.out.println("[Announcement]: " + name + " has the highest bid of " + bidderAmount);
    }

    //Coder's note for future ref:
    //Bidder.placeBid để kiểm tra cá nhân
    //Auction.placeBid để kiểm tra bid toàn buổi đấu giá
    public boolean placeBid(Bidder bidder, Price bidAmount) {
        lock.lock();
        try {
            if (status != AuctionStatus.RUNNING) {
                System.out.println("The auction hasn't started or has already ended");
                return false;
            }

            if (bidder instanceof User && ((User) bidder).getId() == owner.getId()) {
                System.out.println("Auction owner cannot place bid");
                return false;
            }

            if (bidAmount.getPrice() > currentPrice) {
                currentPrice = bidAmount.getPrice();
                winner = bidder;

                handleSniping();

                notifyAllBidders(bidder, bidAmount.getPrice());
                return true;
            } else {
                System.out.println("Bid price has to be greater than the current price");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    private void handleSniping() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesBeforeEnd = terminatedAt.minusMinutes(5);

        if (now.isAfter(fiveMinutesBeforeEnd) && now.isBefore(terminatedAt)) {
            this.terminatedAt = terminatedAt.plusMinutes(5);
            this.isInCountDown = true;

            System.out.println("[System]: Auction extended to: " + terminatedAt);
        }
    }

    public void setAuctionId(int idOfAuction) {
        lock.lock();
        try {
            if (this.auctionId == 0) {
                this.auctionId = idOfAuction;
            }
        } finally { lock.unlock(); }
    }

    public void setStatus(AuctionStatus status) {
        boolean check = transitionTo(status);

        if (!check) {
            System.out.println("[System]: Status transition failure from " + this.status + " to " + status);
        } else {
            System.out.println("[System]: The auction is now " + this.status);
        }
    }

    public int getAuctionId() {
        return auctionId;
    }

    public Member getOwner() {
        return owner;
    }

    public LocalDateTime getTerminatedAt() {
        return terminatedAt;
    }

    public Item getItem() {
        return item;
    }

    public double getCurrentPrice() { return currentPrice; }

    public Bidder getWinner() { return winner; }
}