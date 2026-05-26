package models;

import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;

    private final List<Auction> activeSessions;
    private final List<Auction> completedSessions;

    private AuctionManager() {
        activeSessions = new ArrayList<>();
        completedSessions = new ArrayList<>();
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
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

    public List<Auction> getAllSessions() {
        List<Auction> all = new ArrayList<>(activeSessions);
        all.addAll(completedSessions);
        return all;
    }

    private Auction findActiveAuction(int auctionId) {
        return activeSessions.stream()
                .filter(auction -> auction.getAuctionId() == auctionId)
                .findFirst()
                .orElse(null);
    }

    private void moveToCompleted(Auction session) {
        activeSessions.removeIf(auction -> auction.getAuctionId() == session.getAuctionId());
        if (completedSessions.stream().noneMatch(auction -> auction.getAuctionId() == session.getAuctionId())) {
            completedSessions.add(session);
        }
    }
}