package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;

    private final List<Auction> activeSessions;
    private final List<Auction> completedSessions;
    private final List<Transaction> transactions;
    private int nextAuctionId = 1;
    private int nextItemId = 1;
    private int nextTransactionId = 1;

    private AuctionManager() {
        activeSessions = new ArrayList<>();
        completedSessions = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public void processAutoBids(Auction auction, AutoBid userConfig) {
        if (auction == null || userConfig == null) {
            return;
        }

        if (auction.getWinner() != null && auction.getWinner().isEqual(userConfig.getUser())) {
            return;
        }

        double nextPrice = auction.getCurrentPrice() + userConfig.getIncrement();
        if (nextPrice > userConfig.getMaxBid()) {
            System.out.println("[System]: Automatic bidding stopped due to maximum bid limit reached");
            return;
        }

        userConfig.getUser().placeBid(auction, nextPrice);
    }

    public void createAuction(Member owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        item.setOwnerId(owner.getId());
        item.setId(nextItemId++);
        item.setStatus(Item.Status.IN_AUCTION);

        Auction session = new Auction(owner, item, startingTime, endingTime);
        session.setAuctionId(nextAuctionId++);
        activeSessions.add(session);
        session.start();
    }

    public void closeAuction(Auction session) {
        if (session == null || session.getRawStatus() == Auction.AuctionStatus.FINISHED
                || session.getRawStatus() == Auction.AuctionStatus.PAID
                || session.getRawStatus() == Auction.AuctionStatus.CANCELED) {
            return;
        }

        if (!session.transitionTo(Auction.AuctionStatus.FINISHED)) {
            return;
        }

        moveToCompleted(session);

        User winner = session.getWinner();
        if (winner == null) {
            session.getItem().setStatus(Item.Status.AVAILABLE);
            return;
        }

        createPendingTransaction(session, winner);
        session.getItem().setStatus(Item.Status.SOLD);
    }

    public boolean cancelAuction(int auctionId) {
        Auction canceledAuction = findActiveAuction(auctionId);
        if (canceledAuction == null) {
            return false;
        }

        canceledAuction.getItem().setStatus(Item.Status.AVAILABLE);
        canceledAuction.transitionTo(Auction.AuctionStatus.CANCELED);
        moveToCompleted(canceledAuction);
        return true;
    }

    public Transaction confirmReceipt(Auction auction, Member buyer) {
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction is required.");
        }
        return confirmReceipt(auction.getId(), buyer);
    }

    public Transaction confirmReceipt(int auctionId, Member buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException("[Error]: Buyer is required.");
        }

        Transaction transaction = transactions.stream()
                .filter(t -> t.getAuction().getId() == auctionId
                        && t.getBuyer().isEqual(buyer)
                        && t.getStatus() == Transaction.TransactionStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "[Error]: No pending transaction found for this auction."));

        transaction.markCompleted();
        transaction.getAuction().transitionTo(Auction.AuctionStatus.PAID);
        return transaction;
    }

    public List<Auction> getActiveSessions() {
        return new ArrayList<>(activeSessions);
    }

    public List<Auction> getAllSessions() {
        List<Auction> all = new ArrayList<>(activeSessions);
        all.addAll(completedSessions);
        return all;
    }

    private Auction findActiveAuction(int auctionId) {
        return activeSessions.stream()
                .filter(auction -> auction.getId() == auctionId)
                .findFirst()
                .orElse(null);
    }

    private void moveToCompleted(Auction session) {
        activeSessions.removeIf(auction -> auction.getId() == session.getId());
        if (completedSessions.stream().noneMatch(auction -> auction.getId() == session.getId())) {
            completedSessions.add(session);
        }
    }

    private void createPendingTransaction(Auction session, User winner) {
        double finalPrice = session.getCurrentPrice();
        Transaction transaction = new Transaction(session, (Member) winner, session.getOwner(), finalPrice);
        transaction.setTransactionId(nextTransactionId++);
        transactions.add(transaction);
    }
}
